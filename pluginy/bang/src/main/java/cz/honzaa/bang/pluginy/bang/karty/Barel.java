/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.pluginy.bang.BarelEfekt;
import cz.honzaa.bang.sdk.Efekt;
import cz.honzaa.bang.sdk.VylozitelnaKarta;
import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;

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
    @Override
    public void spalitVylozenou(){
    }
    
}