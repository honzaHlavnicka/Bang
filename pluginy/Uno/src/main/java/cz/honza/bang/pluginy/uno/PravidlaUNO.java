/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.uno;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.UIPrvek;
import java.util.ArrayList;
import java.util.List;




/**
 *
 * @author honza
 */
public class PravidlaUNO implements HerniPravidla{
    private final Hra hra;
    private List<Hrac> poradiVyher = new ArrayList<>();  // Pořadí končících hráčů

    public PravidlaUNO( Hra hra) {
        this.hra = hra;
    }
    

    @Override
    public void poOdehrani(Hrac kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        if(kym.getKarty().isEmpty()){
            hra.skoncil(kym);
            poradiVyher.add(kym);  // Přidej do pořadí
            
            int pocetZbyvajicichHracu = hra.getHraci().size() - poradiVyher.size();
            
            // Pokud zbyl jen jeden hráč, tak hra skončila
            if(pocetZbyvajicichHracu <= 1){
                ukoncitHru();
            } else {
                // Zatím jen oznám vítězství toho hráče
                hra.vyhral(kym);
            }
        }
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        //Nezájem, nic jako životy UNO nemá
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return false; //Hráč nemůže jen tak říct, že přeskakuje tah
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        if(kdo.jeNaTahu()){
            kdo.lizni();
            kdo.konecTahu();
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void pripravBalicek(Balicek<Karta> balicek){
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
    public void pripravitHrace(Hrac hrac) {
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
    }
    
    /**
     * Ukončí hru a pošle výsledky hráčům.
     * Vytvoří 2D pole kde každá řada je jedno umístění a obsahuje hráče na tom místě.
     */
    private void ukoncitHru(){
        // Zbývá jeden hráč - poslední (vítěz)
        List<Hrac> zbyvajici = new ArrayList<>();
        for(Hrac hrac : hra.getHraci()){
            if(!poradiVyher.contains(hrac)){
                zbyvajici.add(hrac);
            }
        }
        
        // Oznám vítězství posledního zbývajícího hráče
        for(Hrac hrac : zbyvajici){
            hra.vyhral(hrac);
        }
        
        // Vytvoř 2D pole výsledků: každé místo je jedno vnitřní pole
        Hrac[][] vysledky = new Hrac[poradiVyher.size() + zbyvajici.size()][];
        
        // První místa jsou v pořadí kdy skončili (0 index = 1. místo, atd.)
        for(int i = 0; i < poradiVyher.size(); i++){
            vysledky[i] = new Hrac[]{poradiVyher.get(i)};
        }
        
        // Poslední místo(a) jsou zbývající hráči (1. vítěz)
        if(zbyvajici.size() > 0){
            vysledky[poradiVyher.size()] = new Hrac[zbyvajici.size()];
            for(int i = 0; i < zbyvajici.size(); i++){
                vysledky[poradiVyher.size()][i] = zbyvajici.get(i);
            }
        }
        
        // Pošli výsledky
        hra.getKomunikator().posliVysledky(vysledky);
        
        // Oznám konec hry
        hra.getKomunikator().posliKonecHry();
    }
    
}