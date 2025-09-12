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
    public void odehrat(Hrac kym) {
        kym.lizni();
        kym.lizni();
    }
    
}
