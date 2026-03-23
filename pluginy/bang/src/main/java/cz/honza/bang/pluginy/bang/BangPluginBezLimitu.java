/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;

/**
 *
 * @author honza
 */
public class BangPluginBezLimitu implements HerniPlugin{

    @Override
    public String getJmeno() {
        return "Bang! –⁠⁠⁠⁠⁠⁠ neomezené karty, pivo vždy";
    }

    @Override
    public String getPopis() {
        return "Verze Bangu, ve které můžete mít na konci tahu libovolný počet karet a pivo jde použít i když hrají jenom dva hráči.";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new PravidlaBangu(hra, true);
    }

    @Override
    public String getURLPravidel() {
        return "https://albi.cz/data/files/products/24327/1603711687-bang-pravidla-zakladni-hry.pdf";
    }
;
    
}
