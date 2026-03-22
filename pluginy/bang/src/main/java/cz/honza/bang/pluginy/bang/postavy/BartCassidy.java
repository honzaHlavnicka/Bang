/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public class BartCassidy implements Postava, Efekt{

    @Override
    public String getJmeno() {
        return "Bart Cassidy";
    }

    @Override
    public String name() {
        return "bartCassidy";
    }

    @Override
    public String getPopis() {
        return "Klikoliv je zasažen, lízne si kartu.";
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
    public void poZtrateZivota(Hra hra, Hrac hrac) {
        hrac.lizni();
    }
    
}
