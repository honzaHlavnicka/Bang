package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public interface Efekt {
    default void naZacatekTahu(Hra hra, Hrac hrac) {}
    default void naKonecTahu(Hra hra, Hrac hrac) {}
    default void poZtrateZivota(Hra hra, Hrac hrac) {}
    default void poOdehraniKarty(Hra hra, Hrac hrac) {}
    default void kdyzNemaKarty(Hra hra, Hrac hrac) {}
    default void poZabitiKohokoliv(Hrac ja,Hrac zabity){}
    
    /**
     * Zvyšuje dosah tohoto hráče (vidí dál) při volání metody Hrac.vzdalenostPod().
     * @return číslo, které se přičte k fyzické vzdálenosti (tzn., že záporné číslo odčítá)
     * @see #getBonusOdstupu() 
     */
    default int getBonusDosahu() {
        return 0;
    }
    /**
     * Zvyšuje odstup od tohoto hráče (ostatní to k němu mají dál) při volání metody Hrac.vzdalenostPod().
     * @return číslo, které se přičte k fyzické vzdálenosti (tzn., že záporné číslo odčítá)
     * @see #getBonusDosahu() 
     */
    default int getBonusOdstupu() {
        return 0;
    }
    
    default void odebrani(Hrac odKoho){};
    default void prirazeni(Hrac komu){};
    
}
