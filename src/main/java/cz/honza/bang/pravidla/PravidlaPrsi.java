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
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.PrsiBarva;
import cz.honza.bang.karty.PrsiEso;
import cz.honza.bang.karty.PrsiHodnota;
import cz.honza.bang.karty.PrsiKarta;
import cz.honza.bang.karty.PrsiSedmicka;
import cz.honza.bang.karty.PrsiSvrsek;


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
        for(PrsiBarva barva : PrsiBarva.values()){
            balicek.vratNahoru(new PrsiSedmicka(hra, balicek, barva));
            balicek.vratNahoru(new PrsiEso(hra, balicek, barva, PrsiHodnota.ESO));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.OSMA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.DEVITKA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.DESITKA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.KRAL));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.SPODEK));
            balicek.vratNahoru(new PrsiSvrsek(hra, balicek, barva, PrsiHodnota.SVRSEK));
        }
        balicek.zamichej();
    }

    @Override
    public UIPrvek[] getViditelnePrvky() {
        return new UIPrvek[] {
            UIPrvek.ODHAZOVACI_BALICEK,
            UIPrvek.DOBIRACI_BALICEK,
        };
    }
    
    
}
