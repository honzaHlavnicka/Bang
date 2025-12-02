/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.Role;
import cz.honza.bang.pluginy.bang.Bang;
import cz.honza.bang.pluginy.bang.BangNaVsechny;
import cz.honza.bang.pluginy.bang.Barel;
import cz.honza.bang.pluginy.bang.Dostavnik;
import cz.honza.bang.pluginy.bang.Pivo;
import cz.honza.bang.pluginy.bang.WellsFarkgo;
import cz.honza.bang.sdk.Karta;
import java.util.List;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaBangu implements HerniPravidla{
    private final HraImp hra;

    public PravidlaBangu(HraImp hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(HracImp kym) {
        return; 
    }

    @Override
    public void dosliZivoty(HracImp komu) {
        hra.getSpravceTahu().vyraditHrace(komu);
        
        if(komu.getRole() == Role.SERIF){
            //banditi nebo odpadlík vyhráli
        }
        if(komu.getRole() == Role.BANDITA){
            //TODO: spočítat bandity: možná šerif vyhrál
        }
        if(komu.getRole() == Role.POMOCNIK){
            //mozna vyhral ospadlik
        }
        
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
    public boolean hracChceUkoncitTah(HracImp kdo) {
        if(kdo.jeNaTahu()){
            kdo.konecTahu();
        }
        return true;
    }

    @Override
    public boolean hracChceLiznout(HracImp kdo) {
        return false; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }

    @Override
    public void pripravBalicek(BalicekImp<Karta> balicek) {
        for (int i = 0; i < 10; i++) {        
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new BangNaVsechny(hra, balicek));
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Dostavnik(hra, balicek));
            balicek.vratNahoru(new WellsFarkgo(hra, balicek));
            balicek.vratNahoru(new Pivo(hra, balicek));
        }
        balicek.zamichej();
    }

    @Override
    public void zacalTah(HracImp komu) {
        komu.lizni();
        komu.lizni();
    }

    @Override
    public void skoncilTah(HracImp komu) {
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
    public void pripravitHrace(HracImp hrac) {
        if (hrac.getRole() != Role.SERIF) {
            hrac.setMaximumZivotu(hrac.getPostava().maximumZivotu);
        } else {
            hrac.setMaximumZivotu(hrac.getPostava().maximumZivotu + 1);
        }
        hrac.setZivoty(hrac.getMaximumZivotu());
        
        for (int i = 0; i < hrac.getMaximumZivotu(); i++) {
            hrac.lizni();
        }
    }
    
}
