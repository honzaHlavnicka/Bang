/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author honza
 */
public class SpravceTahu {
    private Hrac naTahu;
    private Deque<Tah> poradiHracu;
    private int nasobicTahu = 1;
    private int kolikatyTah = 1;

    public SpravceTahu(List<Hrac> hraci) {
        this.poradiHracu = new ArrayDeque<>();
        for (Hrac hrac : hraci) {
            poradiHracu.addLast(new Tah(hrac,false));   
        }
    }
    
    /**
     * Stejné jako dalsiHrac(), ale přeskočí všechny hráče, kteří nemají roli <code> role </code>. Přeskočení hráči se přidají na konec, stejně jako kdyby už hráli.
     * @param role například Role.SERIF, role kterou má mít následující hráč.
     * @return hráč který je na tahu po spuštění metody.
     */
    public Hrac dalsiHracPodleRole(Role role){
        Hrac hrac;
        do {
            hrac = dalsiHrac();
        } while (hrac.getRole() != role);
        naTahu = hrac;
        return hrac;
    }
    
    /**
     * Spustí tah dalšího hráče. Další hráč nebude upozorněn, pouze se nastaví interně ve správci tahů.
     * @return hráč, který je na tahu
     */
    public Hrac dalsiHrac(){
        if(kolikatyTah >= nasobicTahu){
            Tah tah = poradiHracu.getFirst();
            
            
            poradiHracu.removeFirst();
            
            if(!tah.jednorazovy){
                poradiHracu.addLast(tah);
            }
            
            naTahu = tah.hrac;
            if(!tah.docasneZruseny){
                kolikatyTah = 1;
                System.out.println("zahájen tah v spravcitahu: " + tah);
                return tah.hrac;
            }else{
                return dalsiHrac();
                //TODO: udelat limit poctu hracu treba 30, aby nemohlo nastata preteceni zasobniku
                //TODO: udeat, aby kdyz zadny hrac neexistuje nenastalo owerflůowError
            }
            
            
        }
        
        kolikatyTah++;
        return naTahu;
    }
    
    public void dalsiHracSUpozornenim(){
        naTahu.konecTahu();
    }
    
    /**
     * další hráč bude přeskočen. Stávající hráč hraje dál, neukončí to jeho tah.
     * @return hráč, který byl přeskočen.
     */
    public Hrac eso(){
        Tah tah = poradiHracu.pollFirst();
        poradiHracu.addLast(tah);
        return tah.hrac;
    }
    
    /**
     * mění vlastnost násoení tahu. Hráč bude mít místo jednoho tahu k dispozici x.
     * @param kolik kolikrát za sebou bude hrát stejný hráč
     */
    public void setNasobicTahu(int kolik){
        nasobicTahu = kolik;
    }
    
    public Hrac getNaTahu(){
        return naTahu;
    }
    
    /**
     * Vyřadí hráče z koloběhu tahů.
     * Jeho pořadí ve kterém byl se nezapomene, nic ze nezmění kromě toho, že se jeho tah bude pokaždé přeskakovat.
     * @param koho
     */
    public void vyraditHrace(Hrac koho){
        for (Tah tah : poradiHracu) {
            if(tah.hrac.equals(koho)){
                tah.docasneZruseny = true;
            }
        }
    }
    
    /**
     * Vratí vyřazeného hráče do koloběhu tahů.
     * Pokud je parametr <code>koho</code> hráč, kterého už SpravceTahu zná a má pořadí,
     * tak ho přestane přeskakovat. Hráč, který nikdy zařazený nebyl přidán nebude.
     * @param koho
     * @see pridatHrace
     */
    public void vratitHrace(Hrac koho){
        for (Tah tah : poradiHracu) {
            if (tah.hrac.equals(koho)) {
                tah.docasneZruseny = false;
            }
        }
    }
    
    /**
     * Přidá nového hráče do koloběhu tahů. 
     * Pouze pro hráče, který nikdy zařazen nebyl. Pro hráče co již někdy zařazen byl použít <code>vratitHrace</code>.
     * Hráč bude přidán na poslení místo, tzn před hráče co aktuálně hraje.
     * @param koho
     */
    public void pridatHrace(Hrac koho){
        Tah novyTah = new Tah(koho, false);
        poradiHracu.addLast(novyTah);
    }

}
