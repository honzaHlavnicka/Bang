/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.kvarteto;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

import cz.honza.bang.sdk.HratelnaKarta;

/**
 *
 * @author honza
 */
public class KartaKvarteta extends Karta {
    private final int hodnota; //1,2,3,4
    private final int barva; // A,B,C,D ... = 1,2,3,4

    public KartaKvarteta(int hodnota, int barva, Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
        this.hodnota = hodnota;
        this.barva = barva;
    }
    
    

    @Override
    public String getObrazek() {
        switch (barva) {
            case 0:
                return "uno/blue" + hodnota;
            case 1:
                return "uno/green" + hodnota;
            case 2:
                return "uno/red" + hodnota;
            case 3:
                return "uno/yellow" + hodnota;
            default:
                return "zezadu";
        }
    }

    @Override
    public String getJmeno() {
        return hodnota + " " + barva;
    } 

    public int getHodnota() {
        return hodnota;
    }

    public int getBarva() {
        return barva;
    }
    
    
    
}
