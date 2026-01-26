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
public class Vezeni extends Karta implements VylozitelnaKarta, Efekt{

    public Vezeni(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "vezeni";
    }

    @Override
    public String getJmeno() {
        return "Vězení";
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        return (predKoho.equals(kym));
    }

    @Override
    public Efekt getEfekt() {
        return this;
        
    }

    @Override
    public void spalitVylozenou() {
        //nesmí se
    }

    @Override
    public void naZacatekTahu(Hra hra, Hrac hrac) {
        Efekt.super.naZacatekTahu(hra, hrac); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void odebrani(Hrac odKoho) {
        // nic
    }

    @Override
    public void prirazeni(Hrac komu) {
        // nic
    }
    
}
