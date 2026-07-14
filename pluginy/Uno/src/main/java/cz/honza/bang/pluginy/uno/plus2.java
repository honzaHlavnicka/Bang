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
public class plus2 extends UnoKarta{
    
    public plus2( String barva, Hra hra, Balicek<Karta> balicek) {
        super(12, barva, hra, balicek);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if(super.odehrat(kym)){
            Hrac pristiHrac = hra.getSpravceTahu().getHrajiciHraci().get(0);
            
            // Oznámíme všem hráčům, co se stalo
            String oznameni = "Hráč " + pristiHrac.getJmeno() + " si lízá 2 karty a stojí kvůli +2!";
            hra.getKomunikator().posliVsem("rychleOznameni:" + oznameni);
            
            hra.getSpravceTahu().eso();
            pristiHrac.lizni();
            pristiHrac.lizni();
            
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
