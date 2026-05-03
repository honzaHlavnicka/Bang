package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.*;
import cz.matous.milostny_dopis.MilostnyDopisPravidla;

/**
 * Špionka (hodnota 0, 2 kusy)
 *
 * Žádný okamžitý efekt při zahraní.
 * Na konci kola: pokud jsi jediný živý hráč, který hrál nebo odhodil Špionku,
 * získáváš žeton přízně navíc (navíc k případnému vítězství kola).
 */
public class Spionka extends Karta implements HratelnaKarta {

    public Spionka(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Spionka"; }
    @Override public String getObrazek() { return "spionka"; }

    @Override
    public boolean odehrat(Hrac kym) {
        MilostnyDopisPravidla pravidla = (MilostnyDopisPravidla) hra.getHerniPravidla();
        // Zaznamenat že tento hráč Špionku hrál — vyhodnotí se na konci kola
        pravidla.zaznacitSpionku(kym.getId());
        // Žádný async efekt → poOdehrani() posune tah automaticky
        return true;
    }
}