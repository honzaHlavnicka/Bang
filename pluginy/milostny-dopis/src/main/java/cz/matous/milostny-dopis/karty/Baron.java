package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.List;

/**
 * Baron (hodnota 3, 2 kusy)
 *
 * Vyber jiného hráče. Vy a zvolený hráč si porovnáte karty v ruce.
 * Hráč s nižší hodnotou karty ze hry vypadá. Při remíze oba pokračují.
 */
public class Baron extends Karta implements HratelnaKarta {

    public Baron(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Baron"; }
    @Override public String getObrazek() { return "baron"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        List<Hrac> cile = p.getValidniCile(kym, false);

        if (cile.isEmpty()) return true;

        p.zacniEfekt();

        hra.getKomunikator()
           .pozadejOHrace(kym, cile, "Baron: Vyber hráče k souboji", 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   Hrac cil = hra.getHrac(p.parseId(idStr));
                   int hA = kym.getKarty().isEmpty() ? -1 : p.getHodnotu(kym.getKarty().get(0));
                   int hC = cil.getKarty().isEmpty() ? -1 : p.getHodnotu(cil.getKarty().get(0));

                   if (hA > hC) {
                       hra.getKomunikator().posliRychleOznameniVsem(
                           cil.getJmeno() + " má nižší kartu a vypadá!", null);
                       p.vyraditHrace(cil);
                   } else if (hC > hA) {
                       hra.getKomunikator().posliRychleOznameniVsem(
                           kym.getJmeno() + " má nižší kartu a vypadá!", null);
                       p.vyraditHrace(kym);
                   } else {
                       hra.getKomunikator().posliRychleOznameniVsem("Baron: remíza!", null);
                   }
               } catch (Exception ignored) {}
               p.skonciEfekt(kym);
           });
        return true;
    }
}