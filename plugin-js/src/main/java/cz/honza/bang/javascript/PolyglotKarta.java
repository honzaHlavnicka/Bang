package cz.honza.bang.javascript;

/**
 *
 * @author honza
 */

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import org.graalvm.polyglot.Value;
import cz.honza.bang.sdk.Karta;

/**
 * Univerzální karta, která veškerou logiku deleguje do skriptu (JS/Python).
 */
public class PolyglotKarta extends Karta {

    // V obejtu jsou všechna data o kartě
    protected final Value jsObjektKarty;

    public PolyglotKarta(Hra hra, Balicek<Karta> balicek, Value jsObjektKarty) {
        super(hra, balicek);
        this.jsObjektKarty = jsObjektKarty;
    }

    @Override
    public String getJmeno() {
        if (jsObjektKarty.hasMember("getJmeno")) {
            return jsObjektKarty.invokeMember("getJmeno").asString();
        }
        return "Neznámá karta";
    }

    @Override
    public String getObrazek() {
        if (jsObjektKarty.hasMember("getObrazek")) {
            return jsObjektKarty.invokeMember("getObrazek").asString();
        }
        return "vychozi";
    }
    
    @Override
    public String getZadniObrazek() {
        if (jsObjektKarty.hasMember("getZadniObrazek")) {
            return jsObjektKarty.invokeMember("getZadniObrazek").asString();
        }
        return super.getZadniObrazek();
    }

    @Override
    public void predSpalenim() {
        if (jsObjektKarty.hasMember("predSpalenim")) {
            jsObjektKarty.invokeMember("predSpalenim").asString();
        }
        super.predSpalenim(); // Nic nedělá, ale pro budoucí čistý kod, co kdyby.
    }

    
}
