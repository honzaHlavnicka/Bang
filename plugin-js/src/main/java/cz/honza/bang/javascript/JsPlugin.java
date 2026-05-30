/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
 * @author honza
 */
public class JsPlugin implements HerniPlugin{
    private final String jmeno;
    private final String popis;
    private final String URLPravidel;
    
    
    private final Source zdrojak;

    public JsPlugin(String jmeno, String popis, String URLPravidel, Source zdrojak) {
        this.jmeno = jmeno;
        this.popis = popis;
        this.URLPravidel = URLPravidel;
        this.zdrojak = zdrojak;
    }
    
    
    
    
    @Override
    public String getJmeno() {
        return jmeno;
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public String getURLPravidel() {
        return URLPravidel;
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        Context kontextProTutoHru = Tovarna.vytvorBezpecnyKontext();
        kontextProTutoHru.eval(zdrojak);
        
        Value jsGlobalniProstor = kontextProTutoHru.getBindings("js");
        jsGlobalniProstor.putMember("Nastroje", new NastrojePluginu());
        Value jsFunkce = kontextProTutoHru.getBindings("js");

        return new PoligotHerniPravidla(hra, jsFunkce, kontextProTutoHru);
    }
    
}
