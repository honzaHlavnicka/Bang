/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.uno;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;

/**
 *
 * @author honza
 */
public class UNOPlugin implements HerniPlugin {

    @Override
    public String getJmeno() {
        return "UNO!";
    }

    @Override
    public String getPopis() {
        return "Slavná základní karetní hra , která funguje na podobném principu jako české Prší. Vaším hlavním cílem je co nejrychleji se zbavit všech karet v ruce.";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new PravidlaUNO(hra);
    }

    @Override
    public String getURLPravidel() {
        return "";
    }

}
