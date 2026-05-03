package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.List;

/**
 * Král (hodnota 7, 1 kus)
 *
 * Vyber si jiného hráče a vyměňte si karty v ruce.
 */
public class Kral extends Karta implements HratelnaKarta {

    public Kral(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Kral"; }
    @Override public String getObrazek() { return "kral"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        List<Hrac> cile = p.getValidniCile(kym, false);

        if (cile.isEmpty()) return true;

        p.zacniEfekt();

        hra.getKomunikator()
           .pozadejOHrace(kym, cile, "Král: Vyber hráče k výměně karet", 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   Hrac cil = hra.getHrac(p.parseId(idStr));
                   if (!kym.getKarty().isEmpty() && !cil.getKarty().isEmpty()) {
                       Karta kA = kym.getKarty().get(0);
                       Karta kC = cil.getKarty().get(0);
                       kym.getKarty().remove(kA);
                       cil.getKarty().remove(kC);
                       kym.getKarty().add(kC);
                       cil.getKarty().add(kA);
                       hra.getKomunikator().posliNovouKartu(kym, kC);
                       hra.getKomunikator().posliNovouKartu(cil, kA);
                       hra.getKomunikator().posliRychleOznameniVsem(
                           kym.getJmeno() + " a " + cil.getJmeno() + " si vyměnili karty.", null);
                   }
               } catch (Exception ignored) {}
               p.skonciEfekt(kym);
           });
        return true;
    }
}