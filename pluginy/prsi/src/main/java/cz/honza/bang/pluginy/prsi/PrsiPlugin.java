/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;


/**
 *
 * @author honza
 */
public class PrsiPlugin implements HerniPlugin{

    @Override
    public String getJmeno() {
        return "Prší";
    }

    @Override
    public String getPopis() {
        return "Česká tradiční hra, ve které se snažíte zbavit všech karet, které můžete zahrát podle stejné barvy nebo hodnoty.";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new PravidlaPrsi(hra);
    }
    
}
