/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.zbrane;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class RevCarabine extends Zbran{

    public RevCarabine(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public int getVzdalenost() {
        return 4;
    }

    @Override
    public String getObrazek() {
        return "revCarabine";
    }

    @Override
    public String getJmeno() {
        return "REV CARABINE";
    }
    
}
