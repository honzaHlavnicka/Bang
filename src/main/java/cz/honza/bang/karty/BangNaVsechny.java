/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;

/**
 *
 * @author honza
 */
public class BangNaVsechny extends Bang{
    
    public BangNaVsechny(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    @Override
    public boolean odehrat(Hrac kym){
        for (Hrac hrac : hra.getHraci()) {
            if(!hrac.equals(kym)){
                hrac.odeberZivot();
            }
            
        }
        return true;
    }
    @Override
    public String getObrazek(){
        return "dvojitybang";
    }
    
    @Override
    public String getJmeno(){
        return "Bang! na všechny.";
    }
    
}
