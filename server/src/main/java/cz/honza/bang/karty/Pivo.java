/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
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
