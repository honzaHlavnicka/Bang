/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class PrsiEso extends PrsiKarta{
    
    public PrsiEso(Hra hra, Balicek<Karta> balicek, PrsiBarva b, PrsiHodnota h) {
        super(hra, balicek, b, h);
    }
    
    @Override
    public boolean odehrat(Hrac kym){
        if(!super.odehrat(kym)){
            return false;
        }
        hra.getSpravceTahu().eso();
        return true;
    }   
    
}
