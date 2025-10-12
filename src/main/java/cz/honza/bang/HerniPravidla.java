/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cz.honza.bang;

import cz.honza.bang.karty.Karta;

/**
 *
 * @author jan.hlavnicka.s
 */
public interface HerniPravidla {    

    /**
     * Volat poté, co hráč odehraje kartu.
     * @param kym
     */
    public void poOdehrani(Hrac kym);
    
    /**
     * Volat, když má někdo 0 životů.
     * @param komu
     */
    public void dosliZivoty(Hrac komu);

    /**
     * Volat, pokud hráč CHCE ukončit tah.
     * Nemělo by se volat v kartách a podobně
     * @param kdo
     * @return
     */
    public boolean hracChceUkoncitTah(Hrac kdo);

    /**
     * Volat pokud hráč klikne na lízací balíček nebo řekne, že chce lízat.
     * Nemělo by se volat v kartách a podobně.
     * @param kdo
     * @return true - bylo mu zíznuto.
     * @return false - nebylo mu zíznuto
     */
    public boolean hracChceLiznout(Hrac kdo);
    
    /**
     * Nplní balíček kartami, popřípadě zamíchá.
     * @param balicek
     */
    public void pripravBalicek(Balicek<Karta> balicek);
    
}
