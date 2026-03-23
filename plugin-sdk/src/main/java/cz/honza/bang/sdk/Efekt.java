package cz.honza.bang.sdk;

/**
 * Efekt je objekt, který má uložneý hráč a jakmile nastane nějaká situace,
 * tak na něm zavolá jeho metodu. Hodí se hlavně pro postavy a vyložitelné karty.
 * Vyložitelné karty dokonce nějaký efekt dělat musí.
 * 
 * Určena pro implementaci od autora pluginu.
 * @author honza
 */
public interface Efekt {
    /**
     * Volá se poté co začne tah.
     * @param hra
     * @param hrac 
     */
    default void naZacatekTahu(Hra hra, Hrac hrac) {}
    
    /**
     * Volá se po skončení tahu, tzn., že hráč už na tahu není.
     * @param hra
     * @param hrac
     */
    default void naKonecTahu(Hra hra, Hrac hrac) {}
    /**
     * 
     * Volá se po strátě života pomocí {@link Hrac#odeberZivot() }. Pokud už má hráč méně jak 1 život, tak se efekt nezavolá.
     * @param hra
     * @param hrac 
     */
    default void poZtrateZivota(Hra hra, Hrac hrac) {}
    /**
     * Volá se poté, co libovolný hráč úspěšně odehraje kartu. 
     * Volá se i pokud už hráč s daným efektem je mimo hru.
     * @param hra
     * @param hrac Hráč, kterému efekt patří
     * @param kym Hráč, který kartu odehrál
     * @param karta Karta, která byla odehrána
     */
    default void poOdehraniKarty(Hra hra, Hrac hrac, Hrac kym, Karta karta) {}
    
    /**
     * Zavolá se pokud hráčovy dojdou karty. Pokud plugin sebere kartu přímo, bez enginu, tak se efekt nezavolá
     * @param hra
     * @param hrac
     */
    default void kdyzNemaKarty(Hra hra, Hrac hrac) {}
    
    /**
     * Volá se pokud libovolnému hráči dojdou životy.
     * @param ja hráč, kterému efekt patří
     * @param zabity hráč, který má 0 životů
     */
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
    
    /**
     * Volá se při odebrání efektu, pokud je například potřeba něco uklidit.
     * @param odKoho Komu efekt patřil
    */
    default void odebrani(Hrac odKoho){};
    
    /**
     * Volá se hned poté, co hráč efekt dostane. Může například přenastavit nějaké externí věci (například v pravidlech hry)
     * Věci které změní by se měli zase v {@link #odebrani(cz.honza.bang.sdk.Hrac) } odstranit.
     * @param komu komu byl efekt přiřazen
     */
    default void prirazeni(Hrac komu){};
    
}
