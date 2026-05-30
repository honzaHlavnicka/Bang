/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import org.graalvm.polyglot.Value;

/**
 * Tato třída bohužel reprezentuje adaptér vyložitelné a zároven hrateln karty.
 * Zde je zamerne porusen DRY, jeliko v javě nelze dědit ze dvou tříd.
 * @author honza
 */
public class PolyglotHybridniKarta  extends PolyglotVylozitelnaKarta implements HratelnaKarta{ 
    
    public PolyglotHybridniKarta(Hra hra, Balicek<Karta> balicek, Value jsObjekt) {
        super(hra, balicek, jsObjekt);
    }
    
    @Override
    public boolean odehrat(Hrac kym) {
        if (jsObjektKarty.hasMember("odehrat")) {
            return jsObjektKarty.invokeMember("odehrat", hra, kym).asBoolean();
        }
        return false;
    }
}
