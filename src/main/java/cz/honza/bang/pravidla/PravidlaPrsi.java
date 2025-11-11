/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;

/**
 *
 * @author honza
 */
import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
import cz.honza.bang.karty.Eso;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.UnoKarta;
import cz.honza.bang.karty.unoZmenaBarvy;

public class PravidlaPrsi implements HerniPravidla{
    private final Hra hra;

    public PravidlaPrsi(Hra hra) {
        this.hra = hra;
    }
    

    @Override
    public void poOdehrani(Hrac kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        if(kym.getKarty().isEmpty()){
            hra.skoncil(kym);
            hra.vyhral(kym);
        }
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        //Nezájem, nic jako životy prší nemá
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return false; //Hráč nemůže jen tak říct, že přeskakuje tah
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        if(kdo.jeNaTahu()){
            kdo.lizni();
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void pripravBalicek(Balicek<Karta> balicek){
        throw new UnsupportedOperationException();
        /*for (int i = 0; i < 10; i++) {
            balicek.vratNahoru(new UnoKarta(i, "red", hra, balicek));
        }
        for (int i = 0; i < 10; i++) {
            balicek.vratNahoru(new UnoKarta(i, "green", hra, balicek));
        }
        for (int i = 0; i < 10; i++) {
            balicek.vratNahoru(new UnoKarta(i, "blue", hra, balicek));
        }
        for (int i = 0; i < 10; i++) {
            balicek.vratNahoru(new UnoKarta(i, "yellow", hra, balicek));
        }

        
        balicek.zamichej();*/
        
    }
}
