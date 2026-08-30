/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.postavy;

import cz.honzaa.bang.pluginy.bang.BarelEfekt;
import cz.honzaa.bang.sdk.Efekt;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public class Jourdonnais implements Postava{
    
    private Efekt efekt;
    
    @Override
    public String getJmeno() {
        return "Jourdonnais";
    }

    @Override
    public String name() {
        return "jourdonnais";
    }

    @Override
    public String getPopis() {
        return "Chová se jako barel";
    }

    @Override
    public int getMaximumZivotu() {
        return 4;
    }

    @Override
    public void pridaniPostavy(Hrac komu) {
        komu.getEfekty().add(efekt);
    }

    public Jourdonnais() {
        this.efekt = new BarelEfekt();
    }

    @Override
    public void odebraniPostavy(Hrac komu) {
        komu.getEfekty().remove(efekt);
    }
}
