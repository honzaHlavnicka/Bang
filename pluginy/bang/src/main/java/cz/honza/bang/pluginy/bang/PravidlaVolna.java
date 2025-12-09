/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;



import cz.honza.bang.pluginy.bang.Bang;
import cz.honza.bang.pluginy.bang.BangNaVsechny;
import cz.honza.bang.pluginy.bang.Barel;
import cz.honza.bang.pluginy.bang.Dostavnik;
import cz.honza.bang.pluginy.bang.Pivo;
import cz.honza.bang.pluginy.bang.WellsFarkgo;
import cz.honza.bang.sdk.HerniPravidla;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

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
            balicek.vratNahoru(new WellsFarkgo(hra, balicek));
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

