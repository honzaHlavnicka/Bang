/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.sdk.Hrac;

/**
 *
 * @author honza
 */
public class Tah {
    public final Hrac hrac;
    public final boolean jednorazovy;
    public boolean docasneZruseny;

    public Tah(Hrac hrac, boolean jednorazovy) {
        this.hrac = hrac;
        this.jednorazovy = jednorazovy;
        this.docasneZruseny = false;
    }
    
}
