/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.pluginy.bang.PravidlaBangu;
import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.zbrane.Zbran;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

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
