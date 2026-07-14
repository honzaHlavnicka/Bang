/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.uno;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class UnoEso extends UnoKarta{
    
    public UnoEso(String barva, Hra hra, Balicek<Karta> balicek) {
        super(13, barva, hra, balicek);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if(super.odehrat(kym)){
            hra.getSpravceTahu().eso();
            return true;
        }
        return false;
    }
    
    
    
}
