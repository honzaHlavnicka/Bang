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
public class BanggPlugin implements HerniPlugin{

    @Override
    public String getJmeno() {
        return "Bang!";
    }

    @Override
    public String getPopis() {
        return "Populární karetní hra na motivy Divokého západu. Každý hráč získá svou tajnou roli – šerif s pomocníky loví bandity, banditi chtějí dostat šerifa a odpadlík hraje sám za sebe.";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new PravidlaBangu(hra);
    }

    @Override
    public String getURLPravidel() {
        return "https://albi.cz/data/files/products/24327/1603711687-bang-pravidla-zakladni-hry.pdf";
    };
    
}
