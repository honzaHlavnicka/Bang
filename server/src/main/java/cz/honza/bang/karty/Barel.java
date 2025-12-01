/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.VylozitelnaKarta;
import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Barel extends Karta implements VylozitelnaKarta{
    private Efekt efekt;

    public Barel(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
        efekt = new BarelEfekt();
    }
    
    public String getJmeno(){
        return "barel";
    }
    
    public String getObrazek(){
        return "barel";
    }
    
    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        return predKoho.equals(kym);
    }
    
    
    @Override
    public Efekt getEfekt(){
        return efekt;
    }
    public void spalitVylozenou(){
        // udělat něco
    }
    
}