/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cz.honza.bang;

/**
 *
 * @author jan.hlavnicka.s
 */
public interface HerniPravidla {
    public void poOdehrani(Hrac kym);
    public void dosliZivoty(Hrac komu);
    public boolean hracChceUkoncitTah(Hrac kdo);
    public boolean hracChceLiznout(Hrac kdo);
}
