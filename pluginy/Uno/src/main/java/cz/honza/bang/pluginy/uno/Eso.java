/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.uno;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Eso extends Karta implements HratelnaKarta {

    public Eso(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "uno/eso";
    }

    @Override
    public String getJmeno() {
        return "eso";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        hra.getSpravceTahu().eso();
        return true;
    }
    
}
