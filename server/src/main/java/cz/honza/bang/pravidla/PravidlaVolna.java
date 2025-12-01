/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;



import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.karty.BangNaVsechny;
import cz.honza.bang.karty.Barel;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.Pivo;
import cz.honza.bang.karty.WellsFarkgo;
import cz.honza.bang.karty.Dostavnik;
import cz.honza.bang.karty.Bang;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaVolna implements HerniPravidla{
    private final HraImp hra;

    public PravidlaVolna(HraImp hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(HracImp kym) {
        return; 
    }

    @Override
    public void dosliZivoty(HracImp komu) {
        
    }

    @Override
    public boolean hracChceUkoncitTah(HracImp kdo) {
       
        return true;
    }

    @Override
    public boolean hracChceLiznout(HracImp kdo) {
        return true; //Hráč si při bangu nesmí lízat kdy se mu zachce.
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
    }
    
    
    
}

