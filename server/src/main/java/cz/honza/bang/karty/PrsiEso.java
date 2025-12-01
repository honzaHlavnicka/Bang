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
public class PrsiEso extends PrsiKarta{
    
    public PrsiEso(HraImp hra, BalicekImp<Karta> balicek, PrsiBarva b, PrsiHodnota h) {
        super(hra, balicek, b, h);
    }
    
    @Override
    public boolean odehrat(HracImp kym){
        if(!super.odehrat(kym)){
            return false;
        }
        hra.getSpravceTahu().eso();
        return true;
    }   
    
}
