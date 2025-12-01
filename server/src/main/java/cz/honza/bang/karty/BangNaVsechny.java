/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;

/**
 *
 * @author honza
 */
public class BangNaVsechny extends Bang{
    
    public BangNaVsechny(HraImp hra, BalicekImp<Karta> balicek) {
        super(hra, balicek);
    }
    @Override
    public boolean odehrat(HracImp kym){
        for (HracImp hrac : hra.getHraci()) {
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
