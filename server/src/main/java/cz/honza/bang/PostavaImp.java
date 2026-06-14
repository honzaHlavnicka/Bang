/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.sdk.PovolenePluginu;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Postava;

/**
 *
 * @author honza
 */
public enum PostavaImp implements Postava{
    TESTOVACI("TESTOVACÍ","pouze pro test",4)
    ;

    String jmeno;
    int maximumZivotu;
    String popis;

    private PostavaImp(String jmeno, String popis,int maximumZivotu) {
        this.jmeno = jmeno;
        this.maximumZivotu = maximumZivotu;
        this.popis = popis;
    }
    
    @Override
    @PovolenePluginu
    public String getJmeno() {
        return jmeno;
        }

    @Override
    @PovolenePluginu
    public String getPopis() {
        return popis;
    }

    @Override
    @PovolenePluginu
    public int getMaximumZivotu() {
        return maximumZivotu;
    }

    @Override
    @PovolenePluginu
    public void pridaniPostavy(Hrac komu) {
    }

    @Override
    @PovolenePluginu
    public void odebraniPostavy(Hrac komu) {
    }

    


    
}
