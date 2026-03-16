/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.zbrane;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Remington extends Zbran{

    public Remington(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public int getVzdalenost() {
        return 3;
    }

    @Override
    public String getObrazek() {
        return "remington";
    }

    @Override
    public String getJmeno() {
        return "REMINGTON";
    }
    
}
