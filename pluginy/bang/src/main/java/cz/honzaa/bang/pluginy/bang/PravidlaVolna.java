/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang;



import cz.honzaa.bang.pluginy.bang.karty.Pivo;
import cz.honzaa.bang.pluginy.bang.karty.Bang;
import cz.honzaa.bang.pluginy.bang.karty.WellsFargo;
import cz.honzaa.bang.pluginy.bang.karty.Dostavnik;
import cz.honzaa.bang.pluginy.bang.karty.BangNaVsechny;
import cz.honzaa.bang.pluginy.bang.karty.Barel;
import cz.honzaa.bang.sdk.HerniPravidla;


import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaVolna implements HerniPravidla{
    private final Hra hra;

    public PravidlaVolna(Hra hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(Hrac kym) {
        return; 
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return true; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        for (int i = 0; i < 10; i++) {        
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new BangNaVsechny(hra, balicek));
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Dostavnik(hra, balicek));
            balicek.vratNahoru(new WellsFargo(hra, balicek));
            balicek.vratNahoru(new Pivo(hra, balicek));
        }
        balicek.zamichej();
    }

    @Override
    public void zacalTah(Hrac komu) {
        
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
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return true;
    }
    
    
    
}

