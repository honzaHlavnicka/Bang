/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public class WildWill implements Postava, LizaciPostava{

    @Override
    public String getJmeno() {
        return "Wild Will";
    }

    @Override
    public String name() {
        return "wildWill"   ;
    }

    @Override
    public String getPopis() {
        return "Na začátku tahu si lízne tři karty místo dvou.";
    }

    @Override
    public int getMaximumZivotu() {
        return 3;
    }

    @Override
    public void lizniNaZacatkuTahu(Hrac komu, Hra hra) {
        komu.lizni();
        komu.lizni();
        komu.lizni();
    }
    
}
