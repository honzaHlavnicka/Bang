/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
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

    public Indiani(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

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
        for (Hrac hrac : hra.getHrajiciHraci()) {
            if(!hrac.equals(kym)){
                List<Karta> karty = new ArrayList<>(2);
                karty.add(ZastupnaKarta.getZivot());
                hrac.getKarty().stream().filter(k->k instanceof Bang).allMatch(k->karty.add(k));
                hra.getKomunikator().pozadejOKarty(hrac, karty, "Vyber o co přijdeš kvůli Indiánům!", 1, 1,false).thenAccept(id->{
                    int idKarty;
                    try{
                        idKarty = Integer.parseInt(id);
                    }catch(NumberFormatException ex){
                        hra.getKomunikator().posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
                        return;
                    }
                    if(idKarty == ZastupnaKarta.getZivot().getId()){
                        hrac.odeberZivot();
                    }else{
                        for (Karta karta : hrac.getKarty()) {
                            if(karta.getId() == idKarty){
                                hrac.getKarty().remove(karta);
                                hra.getKomunikator().posliSpaleniKarty(hrac, karta);
                                hra.getKomunikator().posliZmenuPoctuKaret(hrac);
                            }
                        }
                    }
                });
            }
        }
        return true;
    }
    
}
