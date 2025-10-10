/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang;

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
        //TODO: prohra
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        if(kdo.jeNaTahu()){
            hra.getSpravceTahu();
        }
        return true;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return false; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }
    
}
