/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class PrsiSedmicka extends PrsiKarta implements HratelnaKarta{
    public final PravidlaPrsi pravidla;
    
    
    public PrsiSedmicka(Hra hra, Balicek<Karta> balicek,PrsiBarva barva,PrsiHodnota hodnota,PravidlaPrsi pravidla) {
        super(hra, balicek,barva,hodnota);
        this.pravidla = pravidla;
    }
    public PrsiSedmicka(Hra hra, Balicek<Karta> balicek, PrsiBarva barva,PravidlaPrsi pravidla) {
        super(hra, balicek, barva, PrsiHodnota.SEDMA);
        this.pravidla = pravidla;
    }

    @Override
    public boolean odehrat(Hrac kym) {
        if(!super.odehrat(kym)){
            return false    ;
        }
        pravidla.zahranaSedmicka(barva == PrsiBarva.CERVENE);
        return true;
    }
    
}
