package cz.honzaa.bang.pluginy.uno;


import cz.honzaa.bang.pluginy.uno.UnoKarta;
import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */

/**
 *
 * @author honza
 */
public class ZmenaSmeru extends UnoKarta{
    
    public ZmenaSmeru(String barva, Hra hra, Balicek<Karta> balicek) {
        super(11, barva, hra, balicek);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if (super.odehrat(kym)){
            hra.getSpravceTahu().zmenaSmeru();
            hra.getSpravceTahu().eso();
            return true;
        }
        return false;
    }
    
    
    
}
