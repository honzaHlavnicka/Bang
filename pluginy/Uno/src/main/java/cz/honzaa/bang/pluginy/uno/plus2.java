/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.uno;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class plus2 extends UnoKarta{
    
    public plus2( String barva, Hra hra, Balicek<Karta> balicek) {
        super(12, barva, hra, balicek);
    }

    @Override
    public String getJmeno() {
        return "$uno.plus2:{\"color\":\"$uno.color_" + getBarva() + "\"}";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if(super.odehrat(kym)){
            Hrac pristiHrac = hra.getSpravceTahu().getHrajiciHraci().get(0);
            
            // Oznámíme všem hráčům, co se stalo
            String oznameni = "$uno.notification_plus2:{\"name\":\"" + Karta.escapeJson(pristiHrac.getJmeno()) + "\"}";
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
