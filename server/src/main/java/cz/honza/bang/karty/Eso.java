/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Eso extends Karta implements HratelnaKarta {

    public Eso(HraImp hra, BalicekImp<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "uno/eso";
    }

    @Override
    public String getJmeno() {
        return "eso";
    }

    @Override
    public boolean odehrat(HracImp kym) {
        hra.getSpravceTahu().eso();
        return true;
    }
    
}
