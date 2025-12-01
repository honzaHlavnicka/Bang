/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Spravuje pořadí hráčů a jejich tahy.
 * Umožňuje přeskakování hráčů, násobení tahů a vyřazování ze hry.
 * Obsahuje lazy cache pro pořadí aktivních hráčů.
 *
 * @author honza
 */
public class SpravceTahuImp implements cz.honza.bang.sdk.SpravceTahu{
    private HracImp naTahu;
    private final Deque<Tah> frontaTahu;

    // Lazy cache pořadí hráčů
    private List<HracImp> hrajiciHraciCache;
    private boolean poradiAktualni = false;

    private int nasobicTahu = 1;
    private int kolikatyTah = 1;
    
    private boolean zmenenSmer = false;

    /**
     * Třída pro správu tahů. Podporuje dynamicky měnit směr, přidávat dočasné tahy i násobit tahy.
     * @param hraci
     */
    public SpravceTahuImp(List<HracImp> hraci) {
        this.frontaTahu = new ArrayDeque<>();
        for (HracImp hrac : hraci) {
            frontaTahu.addLast(new Tah(hrac, false));
        }
        this.poradiAktualni = false;
   }
    
    /**
     * Vrátí kolekci hráčů, kteří jsou zapojeni ve hře v pořadí, ve kterém se vykonávají jejich tahy.
     * Ignoruje jednorázové tahy.
     * Respektuje směr, tzn. hráči vždy budou hrát směrem od 0 do List.size().

     * @return kolekce hráčů seřazená podle pořadí hraní.
     */
    public List<HracImp> getHrajiciHraci() {
        if (!poradiAktualni) {
            hrajiciHraciCache = new ArrayList<>();
            for (Tah t : frontaTahu) {
                if (!t.docasneZruseny && !t.jednorazovy) {
                    hrajiciHraciCache.add(t.hrac);
                }
            }
            if(zmenenSmer){
                hrajiciHraciCache = hrajiciHraciCache.reversed();
            }
            poradiAktualni = true;
        }
        return List.copyOf(hrajiciHraciCache);
    }

     /**
     * Spustí tah dalšího hráče. Další hráč nebude upozorněn, pouze se nastaví
     * interně ve správci tahů.
     * 
     * Pokud už žádní hráči, co by mohli mít tah, nehrajou, zůstane zvolen stejný hráč, který byl doposud.
     *
     * @return hráč, který je na tahu. 
     */
    public HracImp dalsiHrac() {
        if(getHrajiciHraci().isEmpty()){
            return naTahu;
        }
        if (kolikatyTah >= nasobicTahu) {
            Tah tah;
            if(!zmenenSmer){
                tah = frontaTahu.pollFirst();
            }else{
                tah = frontaTahu.pollLast();
            }
            if (tah == null) {
                throw new IllegalStateException("Ve frontě tahů není žádný hráč.");
            }

            if (!tah.jednorazovy) {
                if(zmenenSmer){
                    frontaTahu.addFirst(tah);
                }else{
                    frontaTahu.addLast(tah);
                }
            }

            if (!tah.docasneZruseny) {
                naTahu = tah.hrac;
                kolikatyTah = 1;
                System.out.println("Začíná tah: " + tah.hrac);
                return naTahu;
            } else {
                // přeskočí vyřazeného hráče
                return dalsiHrac();
                //TODO: udelat limit poctu hracu treba 30, aby nemohlo nastata preteceni zasobniku
            }
        }

        kolikatyTah++;
        return naTahu;
    }

    /**
     * Najde dalšího hráče se zadanou rolí.
     */
    public HracImp dalsiHracPodleRole(Role role) {
       HracImp hrac;
        do {
            hrac = dalsiHrac();
        } while (hrac.getRole() != role);
        naTahu = hrac;
        return hrac;
    }

 
    
    public void dalsiHracSUpozornenim() {
        if (naTahu != null) {
            naTahu.konecTahu();
        }
    }

    /**
     * Další hráč bude přeskočen. Stávající hráč hraje dál, neukončí to jeho tah.
     * @return hráč, který byl přeskočen.
     */
    public HracImp eso(){
        Tah tah = frontaTahu.pollFirst();
        if (tah != null) {
            frontaTahu.addLast(tah);
            poradiAktualni = false;
            return tah.hrac;
        }
        return null;
   }

    /**
     * mění vlastnost násoení tahu. Hráč bude mít místo jednoho tahu k dispozici <code>kolik</code>.
     * @param kolik kolikrát za sebou bude hrát stejný hráč
     */
    public void setNasobicTahu(int kolik){
        nasobicTahu = kolik;
    }
    
    public HracImp getNaTahu(){
        return naTahu;
    }
    
    /**
     * Vyřadí hráče z koloběhu tahů.
     * Jeho pořadí ve kterém byl se nezapomene, nic ze nezmění kromě toho, že se jeho tah bude pokaždé přeskakovat.
     * @param koho
     */
    public void vyraditHrace(HracImp koho){
        for (Tah tah : frontaTahu) {
            if(tah.hrac.equals(koho)){
                tah.docasneZruseny = true;
            }
        }
        poradiAktualni = false;
    }
    
    /**
     * Vratí vyřazeného hráče do koloběhu tahů.
     * Pokud je parametr <code>koho</code> hráč, kterého už SpravceTahu zná a má pořadí,
     * tak ho přestane přeskakovat. Hráč, který nikdy zařazený nebyl přidán nebude.
     * @param koho
     * @see pridatHrace
     */
    public void vratitHrace(HracImp koho){
        for (Tah tah : frontaTahu) {
            if (tah.hrac.equals(koho)) {
                tah.docasneZruseny = false;
            }
        }
        poradiAktualni = false;
    }

    /**
     * Přidá nového hráče do koloběhu tahů. 
     * Pouze pro hráče, který nikdy zařazen nebyl. Pro hráče co již někdy zařazen byl použít <code>vratitHrace</code>.
     * Hráč bude přidán na poslení místo, tzn před hráče co aktuálně hraje.
     * @param koho
     */
    public void pridatHrace(HracImp koho) {
        frontaTahu.addLast(new Tah(koho, false));
        poradiAktualni = false;
    }
    
    /**
     * Změní směr hraní
     */
    public void zmenaSmeru(){
        if(zmenenSmer){
            zmenenSmer = false;
        }else{
            zmenenSmer = true;
        }
    }
}

