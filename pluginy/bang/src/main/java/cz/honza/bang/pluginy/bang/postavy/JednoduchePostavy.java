/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public enum JednoduchePostavy implements Postava{
    WILLY_THE_KID("Willy the Kid","Může zahrát neomezeně mnoho bangů.",4),
    SLAB_THE_KILLER("Slab the Killer","Proti jeho kartě Bang! jsou potřeba dvě vedle",4),
    VULTURE_SAM("Vulture Sam","Kdykoliv je nějaký hráč vyřazen ze hry, vezme si všechny jeho karty",4),
    SUZY_LAFAYTTE("Suzy Lafayette","Jakmile nemá žádnou kartu v ruce, může si líznout",4);
    
    String jmeno;
    int maximumZivotu;
    String popis;

    private JednoduchePostavy (String jmeno, String popis, int maximumZivotu) {
        this.jmeno = jmeno;
        this.maximumZivotu = maximumZivotu;
        this.popis = popis;
    }

    @Override
    public String getJmeno() {
        return jmeno;
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public int getMaximumZivotu() {
        return maximumZivotu;
    }

    @Override
    public void pridaniPostavy(Hrac komu) {
    }

    @Override
    public void odebraniPostavy(Hrac komu) {
    }

}


