package cz.honza.bang.sdk;

/**
 * Postava ve hře. Každý hráč může mít jednu postavu. Narozdíl od role všichni vidí postavy všech a postavy mohou dělat nějaké akce.
 * 
 * Určena pro implementaci od autora pluginu.
 * @author honza
 */
public interface Postava {
    
    @PovolenePluginu
    String getJmeno();
    
    /**
     * Jedinečný identifikátor, zároven název obrázku
     * @return 
     */
    
    @PovolenePluginu
    String name();
    /**
     * Vrátí popis postavy
     * @return 
     */
    @PovolenePluginu
    String getPopis();
    /**
     * Vrátí maximální počt životů které postava může mít. Tuto hodnotu engine nikde nepoužívá.
     * @return 
     */
    @PovolenePluginu
    int getMaximumZivotu();
    /**
     * Akce, která se spustí při přiřazení postavy. Hodí se, ay postava mohlo něco reálně dělat,
     * třeba přidat efekt hráčovi. Vše co se v této metodě změní, se musí vrátit v {@link #odebraniPostavy(cz.honza.bang.sdk.Hrac)}.
     * Může také udělat nějakou akci, která se umísně nebude rušit, pokud je její vlastností není například "Nemůže prohrát", ale například "Při dostání postavy hráč dostane život".
     * @param komu 
     */
    @PovolenePluginu
    default void pridaniPostavy(Hrac komu){};
    
    /**
     * Akce, která se spustí při odebrání postavy. Měla by zrušit vše co zavedla metoda {@link #pridaniPostavy(cz.honza.bang.sdk.Hrac)},
     * může také udělat nějakou jednorázovou akci.
     * @param komu
     */
    @PovolenePluginu
    default void odebraniPostavy(Hrac komu){};

}
