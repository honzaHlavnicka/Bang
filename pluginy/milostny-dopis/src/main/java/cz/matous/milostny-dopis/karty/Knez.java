package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.List;

/**
 * Kněz (hodnota 2, 2 kusy)
 *
 * Vyber jiného hráče a tajně se podívej na jeho kartu v ruce.
 * Neukazuj ji ani neprozrazuj nikomu jinému.
 */
public class Knez extends Karta implements HratelnaKarta {

    public Knez(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Knez"; }
    @Override public String getObrazek() { return "knez"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        List<Hrac> cile = p.getValidniCile(kym, false);

        if (cile.isEmpty()) return true;

        p.zacniEfekt();

        hra.getKomunikator()
           .pozadejOHrace(kym, cile, "Kněz: Vyber hráče", 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   Hrac cil = hra.getHrac(p.parseId(idStr));
                   String info = cil.getKarty().isEmpty()
                           ? "žádnou kartu"
                           : cil.getKarty().get(0).getJmeno()
                             + " (" + p.getHodnotu(cil.getKarty().get(0)) + ")";
                   // Tajně — pouze aktivnímu hráči
                   hra.getKomunikator().posliRychleOznameni(
                       cil.getJmeno() + " drží: " + info, kym);
               } catch (Exception ignored) {}
               p.skonciEfekt(kym);
           });
        return true;
    }
}