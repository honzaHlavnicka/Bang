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
public class WellsFarkgo extends Karta implements HratelnaKarta{

    public WellsFarkgo(HraImp hra, BalicekImp<Karta> balicek) {
        super(hra, balicek);
    }
    @Override
    public String getJmeno(){
        return "Wells Fargo";
    }
    @Override
    public String getObrazek(){
        return "wellsfargo";
    }

    @Override
    public boolean odehrat(HracImp kym) {
        kym.lizni();
        kym.lizni();
        kym.lizni();
        return true;
    }
    
}
