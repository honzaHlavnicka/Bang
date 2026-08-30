/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.kvarteto;

import cz.honzaa.bang.sdk.HerniPlugin;
import cz.honzaa.bang.sdk.HerniPravidla;
import cz.honzaa.bang.sdk.Hra;

/**
 *
 * @author honza
 */
public class Plugin implements HerniPlugin {

    @Override
    public String getJmeno() {
        return "Kvarteto";
    }

    @Override
    public String getPopis() {
        return "Hra o paměti, ve které si hráči navzájem berou karty, aby je spojili do čtveřic.";
    }

    @Override
    public String getURLPravidel() {
        return "";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new PravidlaKvarteta(hra);
    }
    
}
