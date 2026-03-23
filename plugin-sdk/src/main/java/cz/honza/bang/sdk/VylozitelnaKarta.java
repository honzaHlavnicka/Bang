package cz.honza.bang.sdk;


/**
 * Karta, která jde vyložit, položit před sebe či ostatní.
 * 
 * Tento interface se smí dát pouze na třídu dědící z {@link Karta karty}, nebo jejího potomka.
 * @author honza
 */
public interface VylozitelnaKarta{
    /**
     * Volá se před vyložením. Může protestovat jesli se vyložit nechá.
     * 
     * Často kontrola, jestli jji hráč vyložil řed sebe.
     * @param predKoho Před koho byla vyložena
     * @param kym kdo ji vyložil
     * @return má být vyložena?
     */
    public boolean vylozit(Hrac predKoho,Hrac kym);
    
    /**
     * Vrátí efekt, který karta dělá. Efekt si karta musí držet v sobě, aby pokaždé vracela ten samý.
     * 
     * Často se hodí, aby vyložitelná karta zároven implementovala Efekt, pak tato metoda může vracet this;
     * @return efekt, který se přiřadí hráči, před kterým je vyložena
     */
    public Efekt getEfekt();
    
    /**
     * Nemusí dělat nic. Volá se před spálením
     */
    public void spalitVylozenou();
}
