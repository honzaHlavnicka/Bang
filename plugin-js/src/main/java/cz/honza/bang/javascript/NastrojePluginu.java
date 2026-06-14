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
import org.graalvm.polyglot.HostAccess;

import org.graalvm.polyglot.Value;

/**
 * Tuto třídu poskytneme JavaScriptu, aby si mohl tvořit Java objekty,
 * aniž by měl přímý přístup k Java classloaderu.
 */
public class NastrojePluginu {
    
    
    @HostAccess.Export
    public Efekt vytvorEfekt(Value jsObjektEfektu) {
        return new PolyglotEfekt(jsObjektEfektu);
    }
    
    
}
