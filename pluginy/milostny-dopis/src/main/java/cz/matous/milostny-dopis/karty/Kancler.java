package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.ArrayList;

/**
 * Kancléř (hodnota 6, 2 kusy) — novinka edice 2019
 *
 * Doberte si z balíčku dvě karty. Jednu ze tří karet v ruce si nechte
 * a zbylé dvě vraťte lícem dolů dospod balíčku (v libovolném pořadí).
 *
 * Pokud v balíčku zbývá jen jedna karta, doberte si ji a vraťte jen jednu.
 * Pokud je balíček prázdný, Kancléř nemá žádný efekt.
 */
public class Kancler extends Karta implements HratelnaKarta {

    public Kancler(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Kancler"; }
    @Override public String getObrazek() { return "kancler"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();

        if (hra.getBalicek().jePrazdny()) return true; // Žádný efekt

        p.zacniEfekt();

        // Líznutí max 2 karet
        int pocetLiznutych = Math.min(2, hra.getBalicek().pocet());
        for (int i = 0; i < pocetLiznutych; i++) {
            Karta k = hra.getBalicek().lizni();
            if (k != null) {
                kym.getKarty().add(k);
                hra.getKomunikator().posliNovouKartu(kym, k);
            }
        }

        // Vracení: hráč musí vrátit pocetLiznutych karet dospod balíčku
        vybratProVraceni(kym, p, pocetLiznutych);
        return true;
    }

    /**
     * Rekurzivně žádá hráče o výběr karet k vrácení dospod balíčku.
     * Po vrácení všech karet zavolá skonciEfekt().
     */
    private void vybratProVraceni(Hrac kym, MilostnyDopisPravidla p, int zbyvajici) {
        if (zbyvajici <= 0 || kym.getKarty().size() <= 1) {
            p.skonciEfekt(kym);
            return;
        }
        String nadpis = "Kancléř: Vrať kartu dospod balíčku (" + zbyvajici + " zbývají)";
        hra.getKomunikator()
           .pozadejOKarty(kym, new ArrayList<>(kym.getKarty()), nadpis, 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   int id = p.parseId(idStr);
                   kym.getKarty().stream()
                      .filter(k -> k.getId() == id)
                      .findFirst()
                      .ifPresent(vracena -> {
                          kym.getKarty().remove(vracena);
                          // Sdělit klientovi že karta zmizela z ruky (bez přidání do odhozu)
                          hra.getKomunikator().posliOdebraniKarty(kym, vracena);
                          // Vrátit dospod balíčku (ne do odhozu)
                          hra.getBalicek().vratNaSpodek(vracena);
                      });
               } catch (Exception ignored) {}
               vybratProVraceni(kym, p, zbyvajici - 1);
           });
    }
}