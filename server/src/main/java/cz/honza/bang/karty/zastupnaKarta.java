/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;

/**
 *
 * @author honza
 */
public class zastupnaKarta extends Karta {
    private final String obrazek;
    private final String jmeno;

    private zastupnaKarta(String obrazek, String jmeno) {
        super(null, null);
        this.obrazek = obrazek;
        this.jmeno = jmeno;
    }
    
    private static final zastupnaKarta nahodna;
    
    static{
        nahodna = new zastupnaKarta("nahodna","Reprezentace náhodné karty");
    }
    
    public static zastupnaKarta getNahodna(){
        return nahodna;
    }

    
    
    @Override
    public String getObrazek() {
        return obrazek;
    }

    @Override
    public String getJmeno() {
        return jmeno;
    }
    
}
