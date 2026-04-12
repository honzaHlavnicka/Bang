/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.kvarteto;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;

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
