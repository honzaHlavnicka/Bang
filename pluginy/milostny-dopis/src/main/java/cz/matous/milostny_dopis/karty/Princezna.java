package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;

/**
 * Princezna (hodnota 9, 1 kus)
 *
 * Pokud Princeznu z jakéhokoli důvodu zahrajete nebo odhodíte,
 * kolo pro vás okamžitě končí.
 */
public class Princezna extends Karta implements HratelnaKarta {

    public Princezna(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Princezna"; }
    @Override public String getObrazek() { return "princezna"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        hra.getKomunikator().posliRychleOznameniVsem(
            kym.getJmeno() + " zahrál/a Princeznu a vypadá z kola!", null);
        p.vyraditHrace(kym);
        // poOdehrani() zkontroluje konec kola a posune tah
        return true;
    }
}