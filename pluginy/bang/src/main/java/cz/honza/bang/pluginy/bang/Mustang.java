/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.VylozitelnaKarta;

/**
 *
 * @author honza
 */
public class Mustang extends Karta implements Efekt,VylozitelnaKarta{

    public Mustang(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "mustang";
    }

    @Override
    public String getJmeno() {
        return "Mustang";
    }

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public void prirazeni(Hrac komu) {
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        return predKoho.equals(kym);
    }

    @Override
    public Efekt getEfekt() {
        return this;
    }

    @Override
    public void spalitVylozenou() {
    }

    @Override
    public int getBonusOdstupu() {
        return 1;
    }
    
    
    
    
}
