
package cz.honzaa.bang.sdk;

/**
 * Chyba ve hře a její kody, které jsou společné pro všechny strany komunikace.
 * @author honza
 */
@PovolenePluginu
public enum Chyba {
    NEPRIPOJEN_KE_HRE("$error.nepripojen_ke_hre",1,2),
    KARTA_NEEXISTUJE("$error.karta_neexistuje",2,2),
    KARTA_NENI_HRATELNA("$error.karta_neni_hratelna",3,1),
    KARTA_NEJDE_ZAHRAT("$error.karta_nejde_zahrat",4,3),
    HRA_NEEXISTUJE("$error.hra_neexistuje",5,1),
    POSTAVA_NENI_NA_VYBER("$error.postava_neni_na_vyber",6,1),
    NEJSI_NA_TAHU("$error.nejsi_na_tahu",7,3),
    NEMUZES_UKONCIT_TAH("$error.nemuzes_ukoncit_tah",8,3),
    KARTA_NEJDE_SPALIT("$error.karta_nejde_spalit",9,1),
    NENI_VYLOZITELNA("$error.neni_vylozitelna",10,1),
    KARTU_NEJDE_VYLOZIT("$error.kartu_nejde_vylozit",11,3),
    CHYBA_PROTOKOLU("$error.chyba_protokolu",12,1),
    NEJDE_SI_LIZNOUT("$error.nejde_si_liznout",13,3),
    SPATNE_HESLO("$error.spatne_heslo",14,1),
    UZ_PRIPOJEN("$error.uz_pripojen",15,2),
    DOSLI_KARTY_V_BALICKU("$error.dosli_karty_v_balicku",16,3),
    NEJSI_ADMIN_HRY("$error.nejsi_admin_hry",17,1),
    PLNY_SERVER("$error.plny_server",18,1),
    VYHOZEN_ZE_HRY("$error.vyhozen_ze_hry",19,1)
    
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
     * @return zpráva v sroézumitelném jazyce
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
