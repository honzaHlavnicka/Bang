/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;

import cz.honza.bang.sdk.UIPrvek;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.karty.Eso;
import cz.honza.bang.karty.HratelnaKarta;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.UnoKarta;
import cz.honza.bang.karty.unoZmenaBarvy;

/**
 *
 * @author honza
 */
public class PravidlaUNO implements HerniPravidla{
    private final HraImp hra;

    public PravidlaUNO( HraImp hra) {
        this.hra = hra;
    }
    

    @Override
    public void poOdehrani(HracImp kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        if(kym.getKarty().isEmpty()){
            hra.skoncil(kym);
            hra.vyhral(kym);
        }
    }

    @Override
    public void dosliZivoty(HracImp komu) {
        //Nezájem, nic jako životy UNO nemá
    }

    @Override
    public boolean hracChceUkoncitTah(HracImp kdo) {
        return false; //Hráč nemůže jen tak říct, že přeskakuje tah
    }

    @Override
    public boolean hracChceLiznout(HracImp kdo) {
        if(kdo.jeNaTahu()){
            kdo.lizni();
            kdo.konecTahu();
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void pripravBalicek(BalicekImp<Karta> balicek){
        for (int i = 0; i < 10; i++) {
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
        balicek.vratNahoru(new Eso(hra, balicek));
        balicek.vratNahoru(new unoZmenaBarvy(hra, balicek));
        balicek.vratNahoru(new Eso(hra, balicek));
        balicek.vratNahoru(new unoZmenaBarvy(hra, balicek));
        balicek.vratNahoru(new Eso(hra, balicek));
        balicek.vratNahoru(new unoZmenaBarvy(hra, balicek));
        balicek.vratNahoru(new Eso(hra, balicek));
        balicek.vratNahoru(new unoZmenaBarvy(hra, balicek));
        balicek.vratNahoru(new Eso(hra, balicek));
        balicek.vratNahoru(new unoZmenaBarvy(hra, balicek));
        
        balicek.zamichej();
        
    }
    
    @Override
    public UIPrvek[] getViditelnePrvky() {
        return new UIPrvek[]{
            UIPrvek.ODHAZOVACI_BALICEK,
            UIPrvek.DOBIRACI_BALICEK,};
    }
    
    @Override
    public void poSpusteniHry() {

    }

    @Override
    public void pripravitHrace(HracImp hrac) {
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
    }
    
    
}