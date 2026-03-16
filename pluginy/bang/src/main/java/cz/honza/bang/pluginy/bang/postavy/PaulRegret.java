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
public class PaulRegret implements Postava, Efekt{

    @Override
    public String getJmeno() {
        return "Paul Regret";
    }

    @Override
    public String name() {
        return "paulRegret";
    }

    @Override
    public String getPopis() {
        return "Je od všech hráčů vzdálen o jedna dále.";
    }

    @Override
    public int getMaximumZivotu() {
        return 3;
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
    public int getBonusOdstupu() {
        return 1;
    }

   
    
    

}
