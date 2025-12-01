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
public class UnoKarta extends Karta implements HratelnaKarta{
    private final int hodnota;
    private final String barva;

    public UnoKarta(int hodnota, String barva, HraImp hra, BalicekImp<Karta> balicek) {
        super(hra, balicek);
        this.hodnota = hodnota;
        this.barva = barva;
    }

    public int getHodnota() {
        return hodnota;
    }

    public String getBarva() {
        return barva;
    }
     
    @Override
    public String getObrazek() {
        return "uno/" + barva + hodnota;
    }

    @Override
    public String getJmeno() {
        return barva + hodnota;
    }

    @Override
    public boolean odehrat(HracImp kym) {
        if(hra.getOdhazovaciBalicek().jePrazdny()){
            return true;
        }
        Karta predchoziKarta = hra.getOdhazovaciBalicek().nahledni(1).get(0);
        if (predchoziKarta instanceof UnoKarta) {
            UnoKarta unoKarta = (UnoKarta) predchoziKarta;
            if(unoKarta.getBarva().equals(barva) || unoKarta.getHodnota() == hodnota ){
                return true;
            }else{
                return false;
            }
        }
        return true;

    }
    
    
    
}
