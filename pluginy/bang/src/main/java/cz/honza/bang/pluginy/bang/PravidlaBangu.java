/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.pluginy.bang.karty.Hledi;
import cz.honza.bang.pluginy.bang.karty.Panika;
import cz.honza.bang.pluginy.bang.karty.Pivo;
import cz.honza.bang.pluginy.bang.karty.CatBalou;
import cz.honza.bang.pluginy.bang.karty.Bang;
import cz.honza.bang.pluginy.bang.karty.Mustang;
import cz.honza.bang.pluginy.bang.karty.WellsFargo;
import cz.honza.bang.pluginy.bang.karty.Dostavnik;
import cz.honza.bang.pluginy.bang.karty.BangNaVsechny;
import cz.honza.bang.pluginy.bang.karty.Barel;
import cz.honza.bang.pluginy.bang.karty.Duel;
import cz.honza.bang.pluginy.bang.karty.Dynamit;
import cz.honza.bang.pluginy.bang.karty.Hokynarstvi;
import cz.honza.bang.pluginy.bang.karty.Indiani;
import cz.honza.bang.pluginy.bang.karty.Kulomet;
import cz.honza.bang.pluginy.bang.karty.Salon;
import cz.honza.bang.pluginy.bang.karty.Vedle;
import cz.honza.bang.pluginy.bang.karty.Vezeni;
import cz.honza.bang.pluginy.bang.postavy.BartCassidy;
import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.postavy.Jourdonnais;
import cz.honza.bang.pluginy.bang.postavy.PaulRegret;
import cz.honza.bang.pluginy.bang.postavy.RoseDoolan;
import cz.honza.bang.pluginy.bang.postavy.SidKetchum;
import cz.honza.bang.pluginy.bang.zbrane.Remington;
import cz.honza.bang.pluginy.bang.zbrane.RevCarabine;
import cz.honza.bang.pluginy.bang.zbrane.Schofield;
import cz.honza.bang.pluginy.bang.zbrane.Volcanic;
import cz.honza.bang.pluginy.bang.zbrane.Winchester;
import cz.honza.bang.sdk.HerniPravidla;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.SpravceTahu;
import cz.honza.bang.sdk.VylozitelnaKarta;
import cz.honza.bang.sdk.ZastupnaKarta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaBangu implements HerniPravidla{
    private final Hra hra;

    public PravidlaBangu(Hra hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(Hrac kym) {
        return; 
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        
        // Kontrola piva před smrtí
        
        List<Karta> piva = new ArrayList<>();
        for (Karta karta : komu.getKarty()) {
            if(karta instanceof Pivo){
                piva.add(karta);
            }
        }
        
        if(!piva.isEmpty()){
            piva.add(ZastupnaKarta.getSmrt());
            hra.getKomunikator().pozadejOKarty(komu, piva, "Došli ti životy! Co chceš? Pokud zbíváte jen 2, tak pivo může být kničemu!", 1, 1, false)
                    .thenAccept(id->{
                        try{
                            int idInt = Integer.parseInt(id);
                            if(idInt == ZastupnaKarta.getSmrt().getId()){
                                smrtHrace(komu);
                                //smrt
                                return;
                            }
                            
                            for (Karta karta : piva) {
                                if(karta.getId() == idInt){
                                    komu.getKarty().remove(karta);
                                    hra.getOdhazovaciBalicek().vratNahoru(karta);
                                    ((HratelnaKarta) karta).odehrat(komu);
                                    hra.getKomunikator().posliSpaleniKarty(komu, karta);
                                    hra.getKomunikator().posliZmenuPoctuKaret(komu);
                                    
                                    if(komu.getZivoty() <= 0){
                                        // Pivo nefungovalo (Např. zbívají jen 2 hráči)
                                        smrtHrace(komu);
                                        return;
                                    }
                                    
                                    hra.getKomunikator().posliRychleOznameni(komu.getJmeno() + " těsně zachráněn!", komu);
                                    // nesmrt
                                    return;
                                }
                                
                                System.out.println("karta nenalezena");
                                smrtHrace(komu);
                                hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                            }
                        }catch(NumberFormatException ex){
                            hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                            smrtHrace(komu);
                            System.out.println("špatný parse: "+id);

                            // smrt
                        }
                    });
        }else{
            smrtHrace(komu);
        }
        
    }

    private void smrtHrace(Hrac komu){
        hra.getKomunikator().posliRychleOznameni(komu.getJmeno() + " umřel/a 💔", null);
        
        hra.getSpravceTahu().vyraditHrace(komu);
        
        if(komu.jeNaTahu()){
            hra.getSpravceTahu().dalsiHracSUpozornenim();
        }

        long pocetZivychBanditu = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA && h.jeZivy()).count();
        long pocetZivychOdpadliku = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK && h.jeZivy()).count();
        long celkemZivych = hra.getHraci().stream().filter(Hrac::jeZivy).count();
        boolean zivySerif = hra.getHraci().stream().anyMatch(h -> h.getRole() == Role.SERIF && h.jeZivy());

        Hrac[][] poradi = null;

        if (!zivySerif) {
            // Šerif zemřel
            if (celkemZivych == 1 && pocetZivychOdpadliku == 1) {
                poradi = new Hrac[2][];
                poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
                poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() != Role.ODPADLIK).toArray(Hrac[]::new);
            } else {
                poradi = new Hrac[3][];
                poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA).toArray(Hrac[]::new);
                poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
                poradi[2] = hra.getHraci().stream().filter(h -> h.getRole() == Role.POMOCNIK || h.getRole() == Role.SERIF).toArray(Hrac[]::new);
            }
        } else if (pocetZivychBanditu == 0 && pocetZivychOdpadliku == 0) {
            // Šerif a pomocníci vyhráli
            poradi = new Hrac[3][];
            poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.SERIF || h.getRole() == Role.POMOCNIK).toArray(Hrac[]::new);
            poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
            poradi[2] = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA).toArray(Hrac[]::new);
        }

        if (poradi != null) {
            // OPRAVA: Vyčistíme pole od prázdných rolí (např. když chybí odpadlík nebo pomocník)
            Hrac[][] vycistenePoradi = Arrays.stream(poradi)
                    .filter(skupina -> skupina.length > 0)
                    .toArray(Hrac[][]::new);

            // Pošleme komunikátoru jen ty skupiny hráčů, které opravdu existují
            hra.getKomunikator().posliVysledky(vycistenePoradi);
            hra.getKomunikator().posliKonecHry();
        }
        
        Hrac vultureSam = hra.getHrajiciHraci().stream().filter(h->h.getPostava().equals(JednoduchePostavy.VULTURE_SAM)).findFirst().orElse(null);

            
        List<Karta> karty = komu.getKarty();
        for (Karta karta : karty) {
            if (vultureSam != null) {
                vultureSam.getKarty().add(karta);
                hra.getKomunikator().posliOdebraniKarty(komu, karta);
                hra.getKomunikator().posliZmenuPoctuKaret(komu);
                hra.getKomunikator().posliZmenuPoctuKaret(vultureSam);
                hra.getKomunikator().posliNovouKartu(vultureSam, karta);
            }else{
                hra.getOdhazovaciBalicek().vratNahoru(karta);
                hra.getKomunikator().posli(komu, "odehrat:" + komu.getId() + "," + karta.toJSON());
                hra.getKomunikator().posliSpaleniKarty(komu, karta);
            }
        }
        karty.clear();

        karty = komu.getVylozeneKarty();
        for (Karta karta : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(karta);
            hra.getKomunikator().posliSpaleniVylozenéKarty(karta, komu);
       }
       karty.clear();    
       
       hra.getKomunikator().posliZmenuPoctuKaret(komu);
                
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        if(kdo.jeNaTahu()){
            kdo.konecTahu();
        }
        return true;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        if(kdo.getPostava().equals(JednoduchePostavy.SUZY_LAFAYTTE) && kdo.getKarty().isEmpty()){
            kdo.lizni();
            return true;
        }
        return false; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        // Základní časté
        for (int i = 0; i < 25; i++) {
            balicek.vratNahoru(new Bang(hra, balicek));

        }
        for (int i = 0; i < 12; i++) {
            balicek.vratNahoru(new Vedle(hra, balicek));
        }
        for (int i = 0; i < 6; i++) {
            balicek.vratNahoru(new Pivo(hra, balicek));
        }

        // Karty, které jsou v balíčku 4x 
        for (int i = 0; i < 4; i++) {
            balicek.vratNahoru(new CatBalou(hra, balicek));
            balicek.vratNahoru(new Panika(hra, balicek));
        }

        // Karty, které jsou v balíčku 3x 
        for (int i = 0; i < 3; i++) {
            balicek.vratNahoru(new Duel(hra, balicek));
            balicek.vratNahoru(new Schofield(hra, balicek));
            balicek.vratNahoru(new Vezeni(hra, balicek));
        }

        // Karty, které jsou v balíčku 2x 
        for (int i = 0; i < 2; i++) {
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Dostavnik(hra, balicek));
            balicek.vratNahoru(new Mustang(hra, balicek));
            balicek.vratNahoru(new Volcanic(hra, balicek));
            balicek.vratNahoru(new Indiani(hra, balicek));
            balicek.vratNahoru(new Hokynarstvi(hra, balicek));
        }

        // --- Unikátní a silné karty (pouze 1x v balíčku) 
        balicek.vratNahoru(new Kulomet(hra, balicek)); 
        balicek.vratNahoru(new WellsFargo(hra, balicek));
        balicek.vratNahoru(new Remington(hra, balicek));
        balicek.vratNahoru(new RevCarabine(hra, balicek));
        balicek.vratNahoru(new Winchester(hra, balicek));
        balicek.vratNahoru(new Hledi(hra, balicek)); 
        balicek.vratNahoru(new Salon(hra, balicek));
        balicek.vratNahoru(new Dynamit(hra, balicek));

        balicek.zamichej();
    }

    @Override
    public void zacalTah(Hrac komu) {
        komu.lizni();
        komu.lizni();
    }

    @Override
    public void skoncilTah(Hrac komu) {
        //zatím nic
    }

    @Override
    public boolean muzeSpalit(Karta co) {
        return true;
    }
    
    @Override
    public void poSpusteniHry() {
        List<Role> role = new ArrayList<>(Role.poleRoliBangu(hra.getHraci().size()));
        Collections.shuffle(role);
        
        for (int i = 0; i < role.size(); i++) {
            hra.getHraci().get(i).priraditRoliNaZacatkuHry(role.get(i));
        }
        
    }

    @Override
    public void pripravitHrace(Hrac hrac) {
        if (hrac.getRole() != Role.SERIF) {
            hrac.setMaximumZivotu(hrac.getPostava().getMaximumZivotu());
        } else {
            hra.getKomunikator().posliVsem("noveJmeno:" + hrac.getId() + ",✪ " + hrac.getJmeno() );
            hrac.setMaximumZivotu(hrac.getPostava().getMaximumZivotu() + 1);
        }
        
        for (int i = 0; i < hrac.getMaximumZivotu(); i++) {
            hrac.lizni();
        }
        
        hrac.setZivoty(hrac.getMaximumZivotu());
    }

    @Override
    public boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co) {
        Karta kontrolovanaKarta = (Karta) co;
        return kdo.getVylozeneKarty().stream().noneMatch(karta -> karta.getJmeno().equals(kontrolovanaKarta.getJmeno()));
    }
    
    @Override
    public void pripravBalicekPostav(java.util.Stack<cz.honza.bang.sdk.Postava> balicekPostav){
        for (int i = 0; i < 2; i++) {
            balicekPostav.add(new RoseDoolan());
            balicekPostav.add(new PaulRegret());
            balicekPostav.add(JednoduchePostavy.WILLY_THE_KID);
            balicekPostav.add(JednoduchePostavy.VULTURE_SAM);
            balicekPostav.add(JednoduchePostavy.SUZY_LAFAYTTE);
            balicekPostav.add(new SidKetchum(hra));
            balicekPostav.add(new BartCassidy());
            balicekPostav.add(new Jourdonnais());
        }

        Collections.shuffle(balicekPostav);
    }
    
    @Override
    public cz.honza.bang.sdk.UIPrvek[] getViditelnePrvky() {
        return cz.honza.bang.sdk.UIPrvek.values();
    }
    
    @Override
    public boolean muzeZahrat(Karta co, Hrac kdo) {
        return true; // V bangu lze hrát jakoukoli kartu, pokud jsou splněny specifické podmínky v samotné kartě
    }
    
    @Override
    public String getVychoziZadniObrazek() {
        return "bang";
    }
    
    public int vzdalenost(Hrac od, Hrac k){
        return 0;
    }

    @Override
    public void spustitPrvniTah(SpravceTahu spravceTahu) {
        spravceTahu.dalsiHracPodleRole(Role.SERIF);
    }
    
    /**
     * Pokusí se zastřelit hráče naKoho, ale nechá ho použít barel a vedle.
     * @param kym kdo ho vyvolal
     * @param naKoho kdo bude zastřelen
     * @param poUtoku co udělat po útoku
     */
    public void vyvolejAkciBang(Hrac kym, Hrac naKoho, BiConsumer<Hrac, Hrac> poUtoku){
        boolean melBarel = naKoho.getEfekty().stream().anyMatch(e -> e instanceof BarelEfekt);

        if (melBarel) {
            boolean zachranen = ((BarelEfekt) naKoho.getEfekty().stream().filter(e -> e instanceof BarelEfekt).findFirst().get()).aktivovat(hra, naKoho);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    vyresitVedleNeboZasah(kym, naKoho, zachranen,poUtoku);
                }
            }, 10000);

        } else {
            // Hráč nemá barel
            vyresitVedleNeboZasah(kym, naKoho, false,poUtoku);
        }
    }
    
    /**
     * Pomocná metoda pro vyvolejAkciBang, která se volá po kontrole barelu.
     * @param kym
     * @param naKoho
     * @param zachranenBarelem
     * @param poUtoku 
     */
    private void vyresitVedleNeboZasah(Hrac kym, Hrac naKoho, boolean zachranenBarelem, BiConsumer<Hrac, Hrac> poUtoku) {
        if (!zachranenBarelem) {
            List<Karta> vedleNaKoho = naKoho.getKarty().stream()
                    .filter(k -> k instanceof Vedle)
                    .collect(java.util.stream.Collectors.toList());

            if (!vedleNaKoho.isEmpty()) {
                vedleNaKoho.add(ZastupnaKarta.getZivot());
                hra.getKomunikator().posliStavovuZpravu(naKoho.getJmeno() + " může ještě použít vedle na odražení útoku!");
                
                hra.getKomunikator().pozadejOKarty(naKoho, vedleNaKoho, "Vyber o co přijdeš. (Může za to " + kym.getJmeno() + " )", 1, 1, false)
                        .thenAccept(id -> {
                            int idInt;
                            try {
                                idInt = Integer.parseInt(id);
                            } catch (NumberFormatException ex) {
                                naKoho.odeberZivot();
                                poUtoku.accept(kym,naKoho);
                                return;
                            }

                            if (idInt == ZastupnaKarta.getZivot().getId()) {
                                hra.getKomunikator().posliRychleOznameni("Trefa!", null);
                                naKoho.odeberZivot();
                            } else {
                                for (Karta karta : vedleNaKoho) {
                                    if (karta.getId() == idInt) {
                                        naKoho.getKarty().remove(karta);
                                        hra.getOdhazovaciBalicek().vratNahoru(karta);
                                        hra.getKomunikator().posliOdebraniKarty(naKoho, karta);
                                        hra.getKomunikator().posliZmenuPoctuKaret(naKoho);
                                        hra.getKomunikator().posliRychleOznameni("Vedle!", null);
                                        break;
                                    }
                                }
                            }
                            poUtoku.accept(kym, naKoho);
                        });
            } else {
                // Nemá ani barel, ani Vedle, přichází o život
                hra.getKomunikator().posliRychleOznameni("Trefa!", null);
                naKoho.odeberZivot();
                poUtoku.accept(kym, naKoho);
            }
        } else {
            // Byl zachráněn barelem
            poUtoku.accept(kym, naKoho);
        }
    }

    
}
