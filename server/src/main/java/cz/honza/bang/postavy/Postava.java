/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.postavy;

import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;

/**
 *
 * @author honza
 */
public enum Postava implements cz.honza.bang.sdk.Postava{
     TESTOVACI(4,"testovací postava", "nedělá vůbec nic."){
         
     },
     TESTOVACI2(3, "testovací postava dvě", "Přidá život, pokud hráč nemá karty") {
        @Override
        public void kdyzNemaKarty(HraImp hra, HracImp hrac) {
            hrac.pridejZivot();
        }
    },
    VULTURE_SAM(4,"Vulture Sam ","Kdykoli je nějaká postava zabita, vezměte si do ruky všechny karty, které má její hráč v ruce a ve hře."){
        @Override
        public void poZabitiKohokoliv(HracImp ja,HracImp zabity){
            //TODO: přendat karty
        }
    };
    
     public final int maximumZivotu;
     public final String jmeno;
     public final String popis;
     
     
     Postava(int zivoty, String jmeno,String popis){
         maximumZivotu = zivoty;
         this.jmeno = jmeno;
         this.popis = popis;
     }
             
     
}
