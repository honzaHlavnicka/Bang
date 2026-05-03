package cz.matous.milostny_dopis;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;

public class MilostnyDopisPlugin implements HerniPlugin {

    @Override
    public String getJmeno() {
        return "Milostný dopis";
    }

    @Override
    public String getPopis() {
        return "Karetní hra pro 2–6 hráčů. Doručte svůj dopis princezně! Edice 2019, 21 karet.";
    }

    @Override
    public String getURLPravidel() {
        return "https://www.asmodee.cz/product/milostny-dopis/";
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        return new MilostnyDopisPravidla(hra);
    }
}