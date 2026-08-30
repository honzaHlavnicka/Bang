/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.pluginy.bang.PravidlaBangu;
import cz.honzaa.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honzaa.bang.pluginy.bang.zbrane.Zbran;
import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Chyba;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.HratelnaKarta;
import cz.honzaa.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Tomahawk extends Karta implements HratelnaKarta{

    public Tomahawk(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public boolean odehrat(Hrac kym) {
       
        if (!((PravidlaBangu) hra.getHerniPravidla()).pokusZahratKartuDoLimituKaretBang(kym)) {
            return false;
        }

        hra.getKomunikator().pozadejOHrace(kym, kym.vzdalenostPod(2), "Na Koho?", 1, 1, true).thenAccept((String idHrace)->{
            try {
                Hrac naKoho = hra.getHrac(Integer.parseInt(idHrace));
                ((PravidlaBangu) hra.getHerniPravidla()).vyvolejAkciBang(kym, naKoho, this::poUtoku);
            } catch (NumberFormatException ex) {
                hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
            }
        
        });

        return false;
        
    }
    
    private void poUtoku(Hrac naKoho, Boolean uspech) {
    }

    @Override
    public String getObrazek() {
        return "tomahawk";
    }

    @Override
    public String getJmeno() {
        return "Tomahawk";
    }
    
}
