/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

/**
 *
 * @author honza
 */
public enum Chyba {
    NEPRIPOJEN_KE_HRE("Nejsi připojen ke hře.",1,2),
    KARTA_NEEXISTUJE("Tato karta neexistuje",2,2),
    KARTA_NENI_HRATELNA("Tato karta není hratelná.",3,1),
    KARTA_NEJDE_ZAHRAT("Tuto kartu ted nemuzes zahrat.",4,3),
    HRA_NEEXISTUJE("Hra, ke které se snažíš připojit neexistuje.",5,1),
    POSTAVA_NENI_NA_VYBER("Postava není na výběr",6,1),
    NEJSI_NA_TAHU("Nejsi na tahu.",7,3),
    NEMUZES_UKONCIT_TAH("Takhle tah ukončit nejde.",8,3),
    KARTA_NEJDE_SPALIT("Tuhle kartu bohužel nemůžeš spálit.",9,1),
    NENI_VYLOZITELNA("Tahle karta není vyložitelná.",10,1),
    KARTU_NEJDE_VYLOZIT("Tuhle kartu teď nemůžeš vyložit.",11,3),
    CHYBA_PROTOKOLU("Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi.",12,1)
    ;
    private final String zprava;
    private final int kod;
    private final int skupina;
    
    
    private Chyba(String zprava, int kod, int skupina){
        this.kod = kod;
        this.zprava = zprava;
        this.skupina = skupina;   
    }
    
    /**
     * Vrací zprávu, která by se měla zobrazit uživateli. Tato zpráva by se neměla používat k identifikaci zpráv, protože se může měnit nezávisle na verzi protokolu. Místo toho použij {@link #getKod() metodu getKod()} 
     * @return zpráva v srozumitelném jazyce
     */
    public String getZprava() {
        return zprava;
    }
    
    /**
     *
     * @return identifikační kod zprávy, pro rozpoznávací účeli v klientovi
     */
    public int getKod() {
        return kod;
    }

    /**
     * Vrací skupinu kam chyba spadá.
     * seznam skupin:
     * <ul>
     *  <li> 0 = interní chyba serveru
     *  <li> 1 = špatný formát zprávy
     *  <li> 2 = chybějící data
     *  <li> 3 = nedodržuje herní pravidla
     * </ul>
     * @return kód skupiny, ke které zpráva patří.
     */
    public int getSkupina() {
        return skupina;
    }
    
    
    
}
