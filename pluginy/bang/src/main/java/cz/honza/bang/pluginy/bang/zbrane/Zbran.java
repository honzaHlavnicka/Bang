/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.zbrane;

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
public abstract class Zbran extends Karta implements VylozitelnaKarta, Efekt{

    public Zbran(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        if(!predKoho.equals(kym)){
            return false;
        }
        
        return !(predKoho.getEfekty().stream().anyMatch(e -> e instanceof Zbran));
    }

    @Override
    public Efekt getEfekt() {
        return this;
    }

    @Override
    public void spalitVylozenou() {
    }
    
    
    public abstract int getVzdalenost();

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public void prirazeni(Hrac komu) {
    }
    
    public boolean umoznujeBangBezLimitu() {
        return false;
    }

    
    
}
