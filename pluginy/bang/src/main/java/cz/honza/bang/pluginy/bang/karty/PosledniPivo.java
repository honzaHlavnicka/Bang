/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class PosledniPivo extends Pivo{
    
    public PosledniPivo(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        kym.pridejZivot();
        return true;
    }

    @Override
    public String getObrazek() {
        return "posledniPivo";
    }

    @Override
    public String getJmeno() {
        return "Poslední pivo";
    }
    
    
    
}
