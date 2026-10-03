package cz.honzaa.bang.sdk;

/**
 * Výjimka, kterou může karta, pravidla nebo efekt vyhodit při pokusu o odehrání,
 * vyložení nebo spálení karty, pokud akci nelze v dané situaci provést.
 * 
 * Umožňuje definovat vlastní chybovou zprávu nebo lokalizační klíč (např. "$bang.error.limit_bang"),
 * který se odešle hráči místo výchozí chyby.
 * 
 * @author honza
 */
@PovolenePluginu
public class NejdeZahratException extends RuntimeException {
    private final Chyba chyba;

    /**
     * Vytvoří novou výjimku s vlastní zprávou (nebo překladovým klíčem).
     * Typ chyby bude automaticky určen podle kontextu akce (např. KARTA_NEJDE_ZAHRAT, KARTU_NEJDE_VYLOZIT nebo KARTA_NEJDE_SPALIT).
     * @param zprava Vlastní text chyby nebo překladový klíč začínající na '$'
     */
    public NejdeZahratException(String zprava) {
        super(zprava);
        this.chyba = null;
    }

    /**
     * Vytvoří novou výjimku se specifickým typem chyby a vlastní zprávou (nebo překladovým klíčem).
     * @param chyba Typ chyby (určuje kód a skupinu chyby v protokolu)
     * @param zprava Vlastní text chyby nebo překladový klíč začínající na '$'
     */
    public NejdeZahratException(Chyba chyba, String zprava) {
        super(zprava);
        this.chyba = chyba;
    }

    /**
     * Vytvoří novou výjimku se specifickým typem chyby a jeho výchozí zprávou.
     * @param chyba Typ chyby
     */
    public NejdeZahratException(Chyba chyba) {
        super(chyba != null ? chyba.getZprava() : "");
        this.chyba = chyba;
    }

    /**
     * Vrátí specifický typ chyby, nebo null pokud má být použit výchozí typ podle kontextu akce.
     * @return Typ chyby
     */
    @PovolenePluginu
    public Chyba getChyba() {
        return chyba;
    }
}
