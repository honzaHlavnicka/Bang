/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang;

import cz.honza.bang.karty.Bang;
import cz.honza.bang.karty.BangNaVsechny;
import cz.honza.bang.karty.Barel;
import cz.honza.bang.karty.Dostavnik;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.Pivo;
import cz.honza.bang.karty.WellsFarkgo;

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
        return; //TODO: nemělo by se něco stát?
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        hra.getSpravceTahu().vyraditHrace(komu);
        
        //TODO: prohra
        
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
        }
        balicek.zamichej();
    }
    
}
