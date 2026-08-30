/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Efekt;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import cz.honzaa.bang.sdk.VylozitelnaKarta;

/**
 *
 * @author honza
 */
public class Hledi extends Karta implements Efekt, VylozitelnaKarta {

    public Hledi(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "hledi";
    }

    @Override
    public String getJmeno() {
        return "Hledí";
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
    public int getBonusDosahu() {
        return 1;
    }

}
