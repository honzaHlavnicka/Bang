/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Pivo extends Karta implements HratelnaKarta{
    
    public Pivo(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getJmeno() {
        return "pivo";
    }

    @Override
    public String getObrazek() {
        return "pivo";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        kym.pridejZivot();
        return true;
    }
}
