/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.karty.HratelnaKarta;

/**
 *
 * @author honza
 */
public class PrsiKarta extends Karta implements HratelnaKarta{
    PrsiBarva barva;
    PrsiHodnota hodnota;
    
    public PrsiKarta(HraImp hra, BalicekImp<Karta> balicek,PrsiBarva b,PrsiHodnota h) {
        super(hra, balicek);
        barva = b;
        hodnota = h;
    }

    public PrsiBarva getBarva() {
        return barva;
    }

    public void setBarva(PrsiBarva barva) {
        this.barva = barva;
    }

    public PrsiHodnota getHodnota() {
        return hodnota;
    }

    public void setHodnota(PrsiHodnota hodnota) {
        this.hodnota = hodnota;
    }
    
    

    @Override
    public String getObrazek() {
        return "marias/" + hodnota.toString().toLowerCase() + "_" + barva.getImagePrefix();
    }

    @Override
    public String getJmeno() {
        return getObrazek();
    }

    @Override         

    public boolean odehrat(HracImp kym) {
        if (hra.getOdhazovaciBalicek().jePrazdny()) {
            return true;
        }
        Karta predchoziKarta = hra.getOdhazovaciBalicek().nahledni();
        if (predchoziKarta instanceof PrsiKarta ) {
            PrsiKarta prsiKarta = (PrsiKarta) predchoziKarta;
            if (prsiKarta.getBarva().equals(barva) || prsiKarta.getHodnota().equals(hodnota)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
