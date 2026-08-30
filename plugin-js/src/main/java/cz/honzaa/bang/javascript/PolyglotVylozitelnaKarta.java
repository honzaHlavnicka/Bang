package cz.honzaa.bang.javascript;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import cz.honzaa.bang.sdk.VylozitelnaKarta;
import cz.honzaa.bang.sdk.Efekt;


/**
 *
 * @author honza
 */


import org.graalvm.polyglot.Value;

public class PolyglotVylozitelnaKarta extends PolyglotKarta implements VylozitelnaKarta {
    
    private final PolyglotEfekt jejiEfekt;

    public PolyglotVylozitelnaKarta(Hra hra, Balicek<Karta> balicek, Value jsObjekt) {
        super(hra, balicek, jsObjekt);

        // Efekt se pro kartu vyrobí ze stejného objektu jako karta (VylozitelnaKarta = efekt)
        this.jejiEfekt = new PolyglotEfekt(jsObjekt);
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        if (jsObjektKarty.hasMember("vylozit")) {
            return jsObjektKarty.invokeMember("vylozit", predKoho, kym).asBoolean();
        }
        return true;
    }

    @Override
    public Efekt getEfekt() {
        return this.jejiEfekt;
    }

    @Override
    public void spalitVylozenou() {
        if (jsObjektKarty.hasMember("spalitVylozenou")) {
            jsObjektKarty.invokeMember("spalitVylozenou");
        }
    }
}
