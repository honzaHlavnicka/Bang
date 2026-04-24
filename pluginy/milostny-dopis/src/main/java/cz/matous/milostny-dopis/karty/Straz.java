package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.Arrays;
import java.util.List;

/**
 * Stráž (hodnota 1, 6 kusů)
 *
 * Vyber jiného hráče a jmenuj postavu (kromě Stráže).
 * Pokud zvolený hráč danou postavu drží, kolo pro něj okamžitě končí.
 */
public class Straz extends Karta implements HratelnaKarta {

    public Straz(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Straz"; }
    @Override public String getObrazek() { return "straz"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        List<Hrac> cile = p.getValidniCile(kym, false);

        if (cile.isEmpty()) {
            // Všichni ostatní jsou chráněni → zahrajeme bez efektu
            return true;
        }

        p.zacniEfekt();

        hra.getKomunikator()
           .pozadejOHrace(kym, cile, "Stráž: Vyber hráče", 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   Hrac cil = hra.getHrac(p.parseId(idStr));

                   // Nabídnout výběr postav (kromě Stráže)
                   List<String> moznosti = Arrays.asList(
                       "Špionka (0)", "Kněz (2)", "Baron (3)", "Komorná (4)",
                       "Princ (5)", "Kancléř (6)", "Král (7)", "Hraběnka (8)", "Princezna (9)"
                   );
                   int[] hodnoty = {0, 2, 3, 4, 5, 6, 7, 8, 9};

                   hra.getKomunikator()
                      .pozadejOVyberMoznosti(kym, moznosti,
                              "Stráž: Hádej kartu " + cil.getJmeno(), false)
                      .thenAccept(indexStr -> {
                          try {
                              int hadana = hodnoty[Integer.parseInt(indexStr.trim())];
                              boolean uhodl = !cil.getKarty().isEmpty()
                                      && p.getHodnotu(cil.getKarty().get(0)) == hadana;
                              if (uhodl) {
                                  hra.getKomunikator().posliRychleOznameniVsem(
                                      kym.getJmeno() + " uhodl! " + cil.getJmeno() + " vypadá.", null);
                                  p.vyraditHrace(cil);
                              } else {
                                  hra.getKomunikator().posliRychleOznameniVsem(
                                      kym.getJmeno() + " neuhodl.", null);
                              }
                          } catch (Exception ignored) {}
                          p.skonciEfekt(kym);
                      });
               } catch (Exception ignored) {
                   p.skonciEfekt(kym);
               }
           });
        return true;
    }
}