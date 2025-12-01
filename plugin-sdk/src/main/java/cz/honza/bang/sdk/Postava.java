/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public interface Postava {
    String jmeno();
    String name();
    String popis();
    String maximumZivotu();
            
    
    default void naZacatekTahu(Hra hra, Hrac hrac) {}
    default void naKonecTahu(Hra hra, Hrac hrac) {}
    default void poZtrateZivota(Hra hra, Hrac hrac) {}
    default void poOdehraniKarty(Hra hra, Hrac hrac) {}
    default void kdyzNemaKarty(Hra hra, Hrac hrac) {}
    default void poZabitiKohokoliv(Hrac ja,Hrac zabity){}
}
