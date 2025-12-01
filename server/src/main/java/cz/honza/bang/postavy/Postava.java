/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.postavy;


import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;

/**
 *
 * @author honza
 */
public enum Postava implements cz.honza.bang.sdk.Postava{
     TESTOVACI(4,"testovací postava", "nedělá vůbec nic."){
         
     },
     TESTOVACI2(3, "testovací postava dvě", "Přidá život, pokud hráč nemá karty") {
        @Override
        public void kdyzNemaKarty(Hra hra, Hrac hrac) {
            hrac.pridejZivot();
        }
    },
    VULTURE_SAM(4,"Vulture Sam ","Kdykoli je nějaká postava zabita, vezměte si do ruky všechny karty, které má její hráč v ruce a ve hře."){
        @Override
        public void poZabitiKohokoliv(Hrac ja,Hrac zabity){
            //TODO: přendat karty
        }
    };
    
     public final int maximumZivotu;
     public final String jmeno;
     public final String popis;

    @Override
    public int maximumZivotu() {
        return maximumZivotu;
    }

     @Override
    public String jmeno() {
        return jmeno;
    }

     @Override
    public String popis() {
        return popis;
    }
     
     
     
     Postava(int zivoty, String jmeno,String popis){
         maximumZivotu = zivoty;
         this.jmeno = jmeno;
         this.popis = popis;
     }
             
     
}
