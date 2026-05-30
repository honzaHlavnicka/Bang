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

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;

import org.graalvm.polyglot.Value;

public class PolyglotEfekt implements Efekt {

    private final Value jsObjekt;

    public PolyglotEfekt(Value jsObjekt) {
        this.jsObjekt = jsObjekt;
    }

    @Override
    public int getBonusDosahu() {
        if (jsObjekt.hasMember("getBonusDosahu")) {
            return jsObjekt.invokeMember("getBonusDosahu").asInt();
        }
        return Efekt.super.getBonusDosahu(); // Vrátí 0
    }

    @Override
    public int getBonusOdstupu() {
        if (jsObjekt.hasMember("getBonusOdstupu")) {
            return jsObjekt.invokeMember("getBonusOdstupu").asInt();
        }
        return Efekt.super.getBonusOdstupu(); // Vrátí 0
    }

    @Override
    public void naZacatekTahu(Hra hra, Hrac hrac) {
        if (jsObjekt.hasMember("naZacatekTahu")) {
            jsObjekt.invokeMember("naZacatekTahu", hra, hrac);
        } else {
            Efekt.super.naZacatekTahu(hra, hrac);
        }
    }
    
    // TODO: musi implementovat vše.
    // ... a tak dále pro zbytek metod z rozhraní Efekt
}
