/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.HerniPravidla;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.VylozitelnaKarta;
import java.util.List;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaBangu implements HerniPravidla{
    private final Hra hra;

    public PravidlaBangu(Hra hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(Hrac kym) {
        return; 
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        hra.getSpravceTahu().vyraditHrace(komu);
        /*
        if(komu.getRole() == Role.SERIF){
            //banditi nebo odpadlík vyhráli
        }
        if(komu.getRole() == Role.BANDITA){
            //TODO: spočítat bandity: možná šerif vyhrál
        }
        if(komu.getRole() == Role.POMOCNIK){
            //mozna vyhral ospadlik
        }*/
        
        //TODO: prohra
        
        List<Karta> karty = komu.getKarty();
        for (Karta karta : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(karta);
            hra.getKomunikator().posli(komu, "odehrat:" + komu.getId() + "," + karta.toJSON());
        }
        karty.clear();
        //TODO:DRY
        karty = komu.getVylozeneKarty();
        for (Karta karta : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(karta);
            hra.getKomunikator().posli(komu,"odehrat:"+komu.getId()+ "," + karta.toJSON()); //TODO: to asi nebude odehrat, bude to spalit, ale ještě namám protokol.
       }
       karty.clear();     
        
        //TODO: poslat smrt, poslat karty
        
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        if(kdo.jeNaTahu()){
            kdo.konecTahu();
        }
        return true;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return false; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        for (int i = 0; i < 10; i++) {        
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new BangNaVsechny(hra, balicek));
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Dostavnik(hra, balicek));
            balicek.vratNahoru(new WellsFarkgo(hra, balicek));
            balicek.vratNahoru(new Pivo(hra, balicek));
            balicek.vratNahoru(new CatBalou(hra, balicek));
        }
        balicek.zamichej();
    }

    @Override
    public void zacalTah(Hrac komu) {
        komu.lizni();
        komu.lizni();
    }

    @Override
    public void skoncilTah(Hrac komu) {
        //zatím nic
    }

    @Override
    public boolean muzeSpalit(Karta co) {
        return true;
    }
    
    @Override
    public void poSpusteniHry() {
    }

    @Override
    public void pripravitHrace(Hrac hrac) {
        /*if (hrac.getRole() != Role.SERIF) {
            hrac.setMaximumZivotu(hrac.getPostava().maximumZivotu);
        } else {
            hrac.setMaximumZivotu(hrac.getPostava().maximumZivotu + 1);
        }*/
        hrac.setZivoty(hrac.getMaximumZivotu());
        
        for (int i = 0; i < hrac.getMaximumZivotu(); i++) {
            hrac.lizni();
        }
    }

    @Override
    public  boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co){
        return !kdo.getVylozeneKarty().contains(co);
    }

    
}
