package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;

/**
 * Komorná (hodnota 4, 2 kusy)
 *
 * Do začátku tvého příštího tahu si tě ostatní hráči nemohou zvolit
 * jako cíl efektů svých karet.
 */
public class Komorna extends Karta implements HratelnaKarta {

    public Komorna(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Komorna"; }
    @Override public String getObrazek() { return "komorna"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla p = (MilostnyDopisPravidla) hra.getHerniPravidla();
        p.chraneniHraci.add(kym.getId());
        hra.getKomunikator().posliRychleOznameniVsem(
            kym.getJmeno() + " je chráněn/a Komornou do příštího tahu.", null);
        // Žádný async efekt → poOdehrani() posune tah automaticky
        return true;
    }
}