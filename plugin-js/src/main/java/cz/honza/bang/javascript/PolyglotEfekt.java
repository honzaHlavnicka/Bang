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
import cz.honza.bang.sdk.Karta;

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

    @Override
    public void naKonecTahu(Hra hra, Hrac hrac) {
        if (jsObjekt.hasMember("naKonecTahu")) {
            jsObjekt.invokeMember("naKonecTahu", hra, hrac);
        } else {
            Efekt.super.naKonecTahu(hra, hrac);
        }
    }

    @Override
    public void poZtrateZivota(Hra hra, Hrac hrac) {
        if (jsObjekt.hasMember("poZtrateZivota")) {
            jsObjekt.invokeMember("poZtrateZivota", hra, hrac);
        } else {
            Efekt.super.poZtrateZivota(hra, hrac);
        }
    }

    @Override
    public void poOdehraniKarty(Hra hra, Hrac hrac, Hrac kym, Karta karta) {
        if (jsObjekt.hasMember("poOdehraniKarty")) {
            jsObjekt.invokeMember("poOdehraniKarty", hra, hrac, kym, karta);
        } else {
            Efekt.super.poOdehraniKarty(hra, hrac, kym, karta);
        }
    }

    @Override
    public void kdyzNemaKarty(Hra hra, Hrac hrac) {
        if (jsObjekt.hasMember("kdyzNemaKarty")) {
            jsObjekt.invokeMember("kdyzNemaKarty", hra, hrac);
        } else {
            Efekt.super.kdyzNemaKarty(hra, hrac);
        }
    }

    @Override
    public void poZabitiKohokoliv(Hrac ja, Hrac zabity) {
        if (jsObjekt.hasMember("poZabitiKohokoliv")) {
            jsObjekt.invokeMember("poZabitiKohokoliv", ja, zabity);
        } else {
            Efekt.super.poZabitiKohokoliv(ja, zabity);
        }
    }

    @Override
    public void odebrani(Hrac odKoho) {
        if (jsObjekt.hasMember("odebrani")) {
            jsObjekt.invokeMember("odebrani", odKoho);
        } else {
            Efekt.super.odebrani(odKoho);
        }
    }

    @Override
    public void prirazeni(Hrac komu) {
        if (jsObjekt.hasMember("prirazeni")) {
            jsObjekt.invokeMember("prirazeni", komu);
        } else {
            Efekt.super.prirazeni(komu);
        }
    }
}
