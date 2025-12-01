/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.pravidla.PravidlaPrsi;

/**
 *
 * @author honza
 */
public class PrsiSedmicka extends PrsiKarta implements HratelnaKarta{
    public final PravidlaPrsi pravidla;
    
    
    public PrsiSedmicka(HraImp hra, BalicekImp<Karta> balicek,PrsiBarva barva,PrsiHodnota hodnota,PravidlaPrsi pravidla) {
        super(hra, balicek,barva,hodnota);
        this.pravidla = pravidla;
    }
    public PrsiSedmicka(HraImp hra, BalicekImp<Karta> balicek, PrsiBarva barva,PravidlaPrsi pravidla) {
        super(hra, balicek, barva, PrsiHodnota.SEDMA);
        this.pravidla = pravidla;
    }

    @Override
    public boolean odehrat(HracImp kym) {
        if(!super.odehrat(kym)){
            return false    ;
        }
        pravidla.zahranaSedmicka(barva == PrsiBarva.CERVENE);
        return true;
    }
    
}
