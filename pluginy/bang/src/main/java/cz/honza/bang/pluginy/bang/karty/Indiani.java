/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.ZastupnaKarta;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author honza
 */
public class Indiani extends Karta implements HratelnaKarta{

    @Override
    public String getObrazek() {
        return "indiani";
    }

    @Override
    public String getJmeno() {
        return "Indiáni";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        for (Hrac hrac : hra.getHraci()) {
            if(!hrac.equals(kym)){
                List<Karta> karty = new ArrayList<>(2);
                karty.add(ZastupnaKarta.getNahodna());
                hrac.getKarty().stream().filter(k->k instanceof Bang).anyMatch(k->karty.add(k));
                hra.getKomunikator().pozadejOKarty(hrac, karty, "Vyber o co přijdeš!", 1, 1).thenAccept(id->{
                    if(id.equals(ZastupnaKarta.get))
                });
            }
        }
    }
    
}
