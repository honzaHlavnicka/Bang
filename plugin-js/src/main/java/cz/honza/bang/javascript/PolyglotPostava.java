/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;
import org.graalvm.polyglot.Value;

/**
 *
 * @author honza
 */
public class PolyglotPostava implements Postava{
    private final Value jsObjekt;

    public PolyglotPostava(Value jsObjekt) {
        this.jsObjekt = jsObjekt;
    }

    @Override
    public String getJmeno() {
        if(jsObjekt.hasMember("getJmeno")){
            return jsObjekt.invokeMember("getJmeno").asString();
        }

        return "Nepojmenovaná postava";
    }

    @Override
    public String name() {
        if(jsObjekt.hasMember("name")){
            return jsObjekt.invokeMember("name").asString();
        }

        return "vychozi";
    }

    @Override
    public String getPopis() {
        if(jsObjekt.hasMember("getPopis")){
            return jsObjekt.invokeMember("getPopis").asString();
        }

        return "Žádný popis";
    }

    @Override
    public int getMaximumZivotu() {
        if(jsObjekt.hasMember("getMaximumZivotu")){
            return jsObjekt.invokeMember("getMaximumZivotu").asInt();
        }

        return 1;
    }

    @Override
    public void pridaniPostavy(Hrac komu) {
        if(jsObjekt.hasMember("pridaniPostavy")){
            jsObjekt.invokeMember("pridaniPostavy", komu);
        }
        Postava.super.pridaniPostavy(komu);
    }

    @Override
    public void odebraniPostavy(Hrac komu) {
        if(jsObjekt.hasMember("odebraniPostavy")){
            jsObjekt.invokeMember("odebraniPostavy", komu);
        }
        Postava.super.odebraniPostavy(komu);
    }
    
}
