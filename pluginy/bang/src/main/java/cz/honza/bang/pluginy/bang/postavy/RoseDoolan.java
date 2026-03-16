/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public class RoseDoolan implements Postava, Efekt{

    @Override
    public String getJmeno() {
        return "Rose Doolan";
    }

    @Override
    public String name() {
        return "roseDoolan";
    }

    @Override
    public String getPopis() {
        return "Vidí všechny hráče na vzdálenost o jedna menší";
    }

    @Override
    public int getMaximumZivotu() {
        return 4;
    }

    @Override
    public void pridaniPostavy(Hrac komu) {
        komu.getEfekty().add(this);
    }

    @Override
    public void odebraniPostavy(Hrac komu) {
        komu.getEfekty().remove(this);
    }

    @Override
    public void prirazeni(Hrac komu) {
    }

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public int getBonusDosahu() {
        return 1;
    }
    
    

}
