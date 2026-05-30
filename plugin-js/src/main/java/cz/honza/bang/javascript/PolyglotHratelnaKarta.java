/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

/**
 *
 * @author honza
 */
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import org.graalvm.polyglot.Value;

public class PolyglotHratelnaKarta extends PolyglotKarta implements HratelnaKarta {
    public PolyglotHratelnaKarta(Hra hra, Balicek<Karta> balicek, Value jsObjekt) {
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
