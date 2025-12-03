/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;



/**
 *
 * @author honza
 */
public class Dostavnik extends Karta implements HratelnaKarta{

    public Dostavnik(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    @Override
    public String getJmeno(){
        return "Dostavník";
    }
    @Override
    public String getObrazek(){
        return "dostavnik";
    }

    @Override
    public boolean odehrat(HracImp kym) {
        kym.lizni();
        kym.lizni();
        return true;
    }
    
}
