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
public class PrsiSedmicka extends PrsiKarta implements HratelnaKarta{
    
    public PrsiSedmicka(Hra hra, Balicek<Karta> balicek,PrsiBarva barva,PrsiHodnota hodnota) {
        super(hra, balicek,barva,hodnota);
    }
    public PrsiSedmicka(Hra hra, Balicek<Karta> balicek, PrsiBarva barva) {
        super(hra, balicek, barva, PrsiHodnota.SEDMA);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if(!super.odehrat(kym)){
            return false    ;
        }
        int pocetLiznutychKaret = 2;
        if(barva == PrsiBarva.CERVENE){
            pocetLiznutychKaret = 4;
        }
        for (int i = 0; i < pocetLiznutychKaret; i++) {
            kym.vzdalenostPod(1, false).get(0).lizni();
        }      
        return true;
    }
    
}
