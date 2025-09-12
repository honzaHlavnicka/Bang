/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.postavy;

import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;

/**
 *
 * @author honza
 */
public interface SchopnostPostavy {
    default void naZacatekTahu(Hra hra, Hrac hrac) {}
    default void naKonecTahu(Hra hra, Hrac hrac) {}
    default void poZtrateZivota(Hra hra, Hrac hrac) {}
    default void poOdehraniKarty(Hra hra, Hrac hrac) {}
    default void kdyzNemaKarty(Hra hra, Hrac hrac) {}
    default void poZabitiKohokoliv(Hrac ja,Hrac zabity){}
}

