/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.pluginy.bang.BarelEfekt;
import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

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
