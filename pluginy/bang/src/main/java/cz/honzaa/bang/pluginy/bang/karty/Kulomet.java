/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.pluginy.bang.PravidlaBangu;
import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.HratelnaKarta;
import cz.honzaa.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class Kulomet extends Karta implements HratelnaKarta{

    public Kulomet(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "kulomet";
    }

    @Override
    public String getJmeno() {
        return "kulomet";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        PravidlaBangu pravidla = (PravidlaBangu) hra.getHerniPravidla();
        for (Hrac hrac : hra.getHrajiciHraci()) {
            if(!hrac.equals(kym)){
                pravidla.vyvolejAkciBang(kym, hrac, this::poUtoku);
            }
        }
        return true;
    }
    
    private void poUtoku(Hrac naKoho, Boolean uspech){}
    
}
