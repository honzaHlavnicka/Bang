package cz.matous.milostny_dopis;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.karty.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Pravidla hry Milostný dopis (Love Letter, edice 2019).
 *
 * Tato třída řídí:
 * - průběh tahu (líznutí, konec tahu)
 * - konec kola a nové kolo
 * - systém žetonů přízně
 * - pomocné metody pro karty (validní cíle, vyřazení hráče...)
 *
 * Efekty jednotlivých karet jsou v jejich vlastních třídách ve složce karty/.
 */
public class MilostnyDopisPravidla implements HerniPravidla {

    // Počty žetonů potřebných k výhře dle počtu hráčů
    private static final Map<Integer, Integer> CILE_ZETONU;
    static {
        CILE_ZETONU = new HashMap<>();
        CILE_ZETONU.put(2, 7); CILE_ZETONU.put(3, 5);
        CILE_ZETONU.put(4, 4); CILE_ZETONU.put(5, 3); CILE_ZETONU.put(6, 3);
    }

    final Hra hra;

    // ID hráčů chráněných Komornou (do jejich příštího tahu)
    public final Set<Integer> chraneniHraci = new HashSet<>();

    // ID hráčů, kteří v tomto kole hráli nebo odhodili Špionku
    public final Set<Integer> spionkyHrali = new HashSet<>();

    // Žetony přízně každého hráče (klíč = ID hráče)
    private final Map<Integer, Integer> zetony = new HashMap<>();

    // true = právě běží async efekt karty, poOdehrani proto nic nedělá
    private boolean efektBezi = false;

    // Karta odložená lícem dolů na začátku každého kola
    private Karta odlozenaKarta = null;

    public MilostnyDopisPravidla(Hra hra) {
        this.hra = hra;
    }

    // =========================================================
    //  INICIALIZACE HRY
    // =========================================================

    @Override
    public void poSpusteniHry() {
        for (Hrac h : hra.getHraci()) {
            zetony.put(h.getId(), 0);
        }
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        // 21 karet edice 2019
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Spionka(hra, balicek));
        for (int i = 0; i < 6; i++) balicek.vratNahoru(new Straz(hra, balicek));
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Knez(hra, balicek));
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Baron(hra, balicek));
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Komorna(hra, balicek));
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Princ(hra, balicek));
        for (int i = 0; i < 2; i++) balicek.vratNahoru(new Kancler(hra, balicek));
        balicek.vratNahoru(new Kral(hra, balicek));
        balicek.vratNahoru(new Hrabenka(hra, balicek));
        balicek.vratNahoru(new Princezna(hra, balicek));
        balicek.zamichej();

        // 1 karta lícem dolů — nikdo ji nevidí
        odlozenaKarta = balicek.lizni();

        // Pro 2 hráče: 3 karty lícem nahoru (veřejně viditelné)
        if (hra.getHraci().size() == 2) {
            StringBuilder sb = new StringBuilder("Karty mimo hru (veřejné): ");
            for (int i = 0; i < 3; i++) {
                Karta k = balicek.lizni();
                if (k != null) sb.append(k.getJmeno()).append(" ");
            }
            hra.getKomunikator().posliStavovuZpravu(sb.toString().trim());
        }
    }

    @Override
    public void pripravitHrace(Hrac hrac) {
        // Každý hráč dostane 1 kartu; druhou dostane při zahájení svého tahu
        hrac.lizni();
    }

    // =========================================================
    //  PRŮBĚH TAHU
    // =========================================================

    @Override
    public void zacalTah(Hrac komu) {
        // Zrušit ochranu Komorné z předchozího tahu tohoto hráče
        chraneniHraci.remove(komu.getId());

        // Prázdný balíček = kolo okamžitě končí
        if (hra.getBalicek().jePrazdny()) {
            vyhodnotitKonecKola();
            return;
        }

        // Líznutí 1 karty → hráč má nyní 2 karty a vybere jednu k zahraní
        komu.lizni();

        zobrazitSkore();
    }

    @Override
    public void poOdehrani(Hrac kym) {
        // Pokud běží async efekt karty, on si sám zavolá skonciEfekt() → nic neděláme
        if (efektBezi) return;

        // Zkontrolovat konec kola (např. po zahraní Princezny)
        if (zkontrolovatKonecKola()) return;

        // Přejít na dalšího hráče
        hra.getSpravceTahu().dalsiHracSUpozornenim();
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        // Životy reprezentují žetony přízně — ignorujeme
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return false; // Líznutí probíhá automaticky v zacalTah
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return false; // Tah se ukončuje automaticky po zahraní karty
    }

    @Override
    public boolean muzeZahrat(Karta co, Hrac kdo) {
        // Hraběnka musí být zahrána pokud má hráč zároveň Krále nebo Prince
        boolean maHrabenku = kdo.getKarty().stream().anyMatch(k -> k instanceof Hrabenka);
        boolean maKraleNeboPrince = kdo.getKarty().stream()
                .anyMatch(k -> k instanceof Kral || k instanceof Princ);
        if (maHrabenku && maKraleNeboPrince && !(co instanceof Hrabenka)) {
            return false;
        }
        return true;
    }

    @Override
    public UIPrvek[] getViditelnePrvky() {
        return new UIPrvek[]{ UIPrvek.ODHAZOVACI_BALICEK, UIPrvek.ZIVOTY };
    }

    @Override
    public String getVychoziZadniObrazek() {
        return "zezadu";
    }

    // =========================================================
    //  POMOCNÉ METODY PRO KARTY
    // =========================================================

    /**
     * Volat na začátku async efektu karty.
     * Zabraňuje poOdehrani() posunout tah předčasně.
     */
    public void zacniEfekt() {
        efektBezi = true;
    }

    /**
     * Volat na konci async efektu karty.
     * Zkontroluje konec kola a posune tah na dalšího hráče.
     */
    public void skonciEfekt(Hrac kym) {
        efektBezi = false;
        if (zkontrolovatKonecKola()) return;
        hra.getSpravceTahu().dalsiHracSUpozornenim();
    }

    /**
     * Vyřadí hráče ze hry: odebere ho z tahu, pošle zprávu klientům
     * a odhodí jeho karty lícem nahoru.
     */
    public void vyraditHrace(Hrac hrac) {
        hra.getSpravceTahu().vyraditHrace(hrac);
        hra.getKomunikator().posliSkonceniHrace(hrac);
        List<Karta> karty = new ArrayList<>(hrac.getKarty());
        hrac.getKarty().clear();
        for (Karta k : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(k);
            hra.getKomunikator().posliSpaleniKarty(hrac, k);
        }
    }

    /**
     * Vrátí seznam hráčů, kteří mohou být cílem efektu karty.
     * Vylučuje chráněné hráče (Komorná) a hráče bez karet.
     *
     * @param kym       hráč, který kartu hraje
     * @param vcetneSebe true = hráč může cílit i sebe (Princ)
     */
    public List<Hrac> getValidniCile(Hrac kym, boolean vcetneSebe) {
        List<Hrac> cile = new ArrayList<>();
        for (Hrac h : hra.getSpravceTahu().getHrajiciHraci()) {
            if (!vcetneSebe && h.getId() == kym.getId()) continue;
            if (chraneniHraci.contains(h.getId())) continue;
            if (h.getKarty().isEmpty()) continue;
            cile.add(h);
        }
        // Pokud jsou všichni ostatní chráněni a voláme vcetneSebe, vrátit sebe
        if (cile.isEmpty() && vcetneSebe && !kym.getKarty().isEmpty()) {
            cile.add(kym);
        }
        return cile;
    }

    /** Vrátí kartu odloženou lícem dolů (pro efekt Prince při prázdném balíčku) */
    public Karta getOdlozenou() {
        return odlozenaKarta;
    }

    /** Zaznamenání že hráč hrál nebo odhodil Špionku (pro bonus žeton) */
    public void zaznacitSpionku(int idHrace) {
        spionkyHrali.add(idHrace);
    }

    /**
     * Vrátí číselnou hodnotu karty (0–9).
     * Potřebné pro Baron (porovnání) a Stráž (hádání).
     */
    public int getHodnotu(Karta k) {
        if (k == null)           return -1;
        if (k instanceof Spionka)  return 0;
        if (k instanceof Straz)    return 1;
        if (k instanceof Knez)     return 2;
        if (k instanceof Baron)    return 3;
        if (k instanceof Komorna)  return 4;
        if (k instanceof Princ)    return 5;
        if (k instanceof Kancler)  return 6;
        if (k instanceof Kral)     return 7;
        if (k instanceof Hrabenka) return 8;
        if (k instanceof Princezna) return 9;
        return -1;
    }

    /**
     * Parsuje ID z odpovědi dialogu.
     * Odpověď může mít formát "42" nebo "[42]".
     */
    public int parseId(String s) {
        return Integer.parseInt(
            s.trim().replace("[", "").replace("]", "").split(",")[0].trim()
        );
    }

    // =========================================================
    //  KONEC KOLA A NOVÉ KOLO
    // =========================================================

    /** Zkontroluje jestli kolo skončilo. Vrátí true pokud ano. */
    private boolean zkontrolovatKonecKola() {
        List<Hrac> aktivni = hra.getSpravceTahu().getHrajiciHraci();
        if (aktivni.size() <= 1 || hra.getBalicek().jePrazdny()) {
            vyhodnotitKonecKola();
            return true;
        }
        return false;
    }

    private void vyhodnotitKonecKola() {
        List<Hrac> aktivni = new ArrayList<>(hra.getSpravceTahu().getHrajiciHraci());

        // Odhalit karty živých hráčů
        StringBuilder sb = new StringBuilder("Odhalení: ");
        for (Hrac h : aktivni) {
            if (!h.getKarty().isEmpty()) {
                Karta k = h.getKarty().get(0);
                sb.append(h.getJmeno()).append("=")
                  .append(k.getJmeno()).append("(").append(getHodnotu(k)).append(") ");
            }
        }
        if (!aktivni.isEmpty()) hra.getKomunikator().posliStavovuZpravu(sb.toString().trim());

        // Vítěz kola = nejvyšší hodnota karty (při remíze dostávají žeton oba)
        List<Hrac> vitezove = new ArrayList<>();
        int nejvyssi = -1;
        for (Hrac h : aktivni) {
            if (h.getKarty().isEmpty()) continue;
            int hod = getHodnotu(h.getKarty().get(0));
            if (hod > nejvyssi) {
                nejvyssi = hod;
                vitezove.clear();
                vitezove.add(h);
            } else if (hod == nejvyssi) {
                vitezove.add(h);
            }
        }
        if (vitezove.isEmpty() && !aktivni.isEmpty()) vitezove.add(aktivni.get(0));

        // Bonus žeton za Špionku: jediný živý hráč, který ji hrál nebo odhodil
        List<Hrac> spionHraci = new ArrayList<>();
        for (Hrac h : hra.getSpravceTahu().getHrajiciHraci()) {
            if (spionkyHrali.contains(h.getId())) spionHraci.add(h);
        }
        if (spionHraci.size() == 1) {
            Hrac sp = spionHraci.get(0);
            pridatZeton(sp);
            hra.getKomunikator().posliRychleOznameniVsem(
                "🕵️ " + sp.getJmeno() + " získává bonus žeton za Špionku!", null);
        }

        // Přidat žetony vítězům kola
        String jmena = vitezove.stream().map(Hrac::getJmeno).collect(Collectors.joining(", "));
        for (Hrac v : vitezove) pridatZeton(v);
        hra.getKomunikator().posliRychleOznameniVsem("🏆 " + jmena + " vyhrál/i kolo!", null);

        // Zkontrolovat dosažení cíle → konec hry
        int cil = CILE_ZETONU.getOrDefault(hra.getHraci().size(), 4);
        for (Hrac v : vitezove) {
            if (zetony.getOrDefault(v.getId(), 0) >= cil) {
                hra.getKomunikator().posliRychleOznameniVsem(
                    "👑 " + v.getJmeno() + " vyhrává hru!", null);
                hra.getKomunikator().posliVysledky(new Hrac[][]{{v}});
                hra.getKomunikator().posliKonecHry();
                return;
            }
        }

        // Spustit nové kolo; začíná vítěz předchozího kola
        zacitNoveKolo(vitezove.isEmpty() ? null : vitezove.get(0));
    }

    private void pridatZeton(Hrac hrac) {
        int nove = zetony.merge(hrac.getId(), 1, Integer::sum);
        hrac.setZivoty(nove);
        hra.getKomunikator().posliZmenuPoctuZivotu(hrac);
    }

    private void zobrazitSkore() {
        int cil = CILE_ZETONU.getOrDefault(hra.getHraci().size(), 4);
        StringBuilder sb = new StringBuilder("Žetony přízně (cíl: " + cil + "): ");
        for (Hrac h : hra.getHraci()) {
            sb.append(h.getJmeno()).append("=")
              .append(zetony.getOrDefault(h.getId(), 0)).append(" ");
        }
        hra.getKomunikator().posliStavovuZpravu(sb.toString().trim());
    }

    private void zacitNoveKolo(Hrac vitezMinula) {
        chraneniHraci.clear();
        spionkyHrali.clear();
        efektBezi    = false;
        odlozenaKarta = null;

        // Odebrat karty z rukou a sdělit to klientům
        for (Hrac h : hra.getHraci()) {
            List<Karta> karty = new ArrayList<>(h.getKarty());
            h.getKarty().clear();
            for (Karta k : karty) {
                hra.getOdhazovaciBalicek().vratNahoru(k);
                hra.getKomunikator().posliSpaleniKarty(h, k);
            }
        }

        // Vyprázdnit oba balíčky
        Balicek<Karta> balicek = hra.getBalicek();
        Balicek<Karta> odhoz   = hra.getOdhazovaciBalicek();
        while (!balicek.jePrazdny()) balicek.lizni();
        while (!odhoz.jePrazdny())   odhoz.lizni();

        // Sestavit nový balíček
        pripravBalicek(balicek);

        // Vrátit všechny hráče do hry
        for (Hrac h : hra.getHraci()) {
            try { hra.getSpravceTahu().vratitHrace(h); } catch (Exception ignored) {}
        }

        // Rozdat každému 1 kartu
        for (Hrac h : hra.getHraci()) {
            h.lizni();
        }

        // Vítěz minulého kola začíná
        if (vitezMinula != null) {
            vitezMinula.zahajitTah();
        } else {
            hra.getSpravceTahu().dalsiHracSUpozornenim();
        }
    }
}