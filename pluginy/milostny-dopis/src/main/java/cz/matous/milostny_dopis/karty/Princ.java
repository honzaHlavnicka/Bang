package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;
import java.util.List;

/**
 * Princ (hodnota 5, 2 kusy)
 *
 * Vyber libovolného hráče (včetně sebe). Zvolený hráč odhodí kartu v ruce
 * (bez vyhodnocení efektu) a dobere si novou.
 * Pokud zvolený hráč odhodí Princeznu, kolo pro něj okamžitě končí.
 * Pokud je balíček prázdný, dostane odloženou kartu lícem dolů.
 */
public class Princ extends Karta implements HratelnaKarta {

    public Princ(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Princ"; }
    @Override public String getObrazek() { return "princ"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        // Cíle včetně sebe; pokud jsou všichni ostatní chráněni, musí zvolit sebe
        List<Hrac> cile = p.getValidniCile(kym, true);

        if (cile.isEmpty()) return true;

        p.zacniEfekt();

        hra.getKomunikator()
           .pozadejOHrace(kym, cile, "Princ: Vyber hráče (i sebe)", 1, 1, false)
           .thenAccept(idStr -> {
               try {
                   Hrac cil = hra.getHrac(p.parseId(idStr));

                   if (!cil.getKarty().isEmpty()) {
                       Karta zahozena = cil.getKarty().get(0);
                       cil.getKarty().remove(zahozena);
                       hra.getOdhazovaciBalicek().vratNahoru(zahozena);
                       hra.getKomunikator().posliSpaleniKarty(cil, zahozena);

                       if (zahozena instanceof Princezna) {
                           // Zahazovatel Princezny vypadá z kola
                           hra.getKomunikator().posliRychleOznameniVsem(
                               cil.getJmeno() + " zahodil/a Princeznu a vypadá!", null);
                           p.vyraditHrace(cil);
                       } else {
                           if (zahozena instanceof Spionka) p.zaznacitSpionku(cil.getId());
                           // Dobrat novou kartu
                           Karta nova = hra.getBalicek().jePrazdny()
                               ? p.getOdlozenou()
                               : hra.getBalicek().lizni();
                           if (nova != null) {
                               cil.getKarty().add(nova);
                               hra.getKomunikator().posliNovouKartu(cil, nova);
                           }
                       }
                   }
               } catch (Exception ignored) {}
               p.skonciEfekt(kym);
           });
        return true;
    }
}