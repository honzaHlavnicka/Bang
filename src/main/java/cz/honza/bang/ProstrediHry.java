/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.karty.Karta;
import cz.honza.bang.net.KomunikatorHry;

/**
 *
 * @author honza
 */
public class ProstrediHry {
    private Hrac[] hraci;
    private Balicek hraciBalicek;
    private Balicek odhazovaciBalicek;
    private KomunikatorHry komunikator;
    
    public ProstrediHry(Hrac[] hraci) {
        this.hraci = hraci;
        hraciBalicek = new Balicek();
        odhazovaciBalicek = new Balicek();
        //hraciBalicek.vygeneruj();
    }
    
    /*public Karta[] liznoutKarty(int kolik){
        Karta[] karty = new Karta[kolik];
        for (int i = 0; i < kolik; i++) {
            karty[i] = liznoutKartu();
        }
        return karty;
    }
    */
   /* public Karta liznoutKartu(){
       
        
    }*/
    
    
    
}
