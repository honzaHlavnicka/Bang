/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;


import java.util.List;

/**
 * Spravuje pořadí hráčů a jejich tahy.
 * Umožňuje přeskakování hráčů, násobení tahů a vyřazování ze hry.
 * Obsahuje lazy cache pro pořadí aktivních hráčů.
 *
 * @author honza
 */
public interface SpravceTahu {

    
    /**
     * Vrátí kolekci hráčů, kteří jsou zapojeni ve hře v pořadí, ve kterém se vykonávají jejich tahy.
     * Ignoruje jednorázové tahy.
     * Respektuje směr, tzn. hráči vždy budou hrát směrem od 0 do List.size().

     * @return kolekce hráčů seřazená podle pořadí hraní.
     */
    public List<Hrac> getHrajiciHraci();

     /**
     * Spustí tah dalšího hráče. Další hráč nebude upozorněn, pouze se nastaví
     * interně ve správci tahů.
     * 
     * Pokud už žádní hráči, co by mohli mít tah, nehrajou, zůstane zvolen stejný hráč, který byl doposud.
     *
     * @return hráč, který je na tahu. 
     */
    public Hrac dalsiHrac();
    /**
     * Najde dalšího hráče se zadanou rolí.
     */
    public Hrac dalsiHracPodleRole(Role role);

 
    
    public void dalsiHracSUpozornenim();

    /**
     * Další hráč bude přeskočen. Stávající hráč hraje dál, neukončí to jeho tah.
     * @return hráč, který byl přeskočen.
     */
    public Hrac eso();

    /**
     * mění vlastnost násoení tahu. Hráč bude mít místo jednoho tahu k dispozici <code>kolik</code>.
     * @param kolik kolikrát za sebou bude hrát stejný hráč
     */
    public void setNasobicTahu(int kolik);
    
    public Hrac getNaTahu();
    
    /**
     * Vyřadí hráče z koloběhu tahů.
     * Jeho pořadí ve kterém byl se nezapomene, nic ze nezmění kromě toho, že se jeho tah bude pokaždé přeskakovat.
     * @param koho
     */
    public void vyraditHrace(Hrac koho);
    
    /**
     * Vratí vyřazeného hráče do koloběhu tahů.
     * Pokud je parametr <code>koho</code> hráč, kterého už SpravceTahu zná a má pořadí,
     * tak ho přestane přeskakovat. Hráč, který nikdy zařazený nebyl přidán nebude.
     * @param koho
     * @see pridatHrace
     */
    public void vratitHrace(Hrac koho);

    /**
     * Přidá nového hráče do koloběhu tahů. 
     * Pouze pro hráče, který nikdy zařazen nebyl. Pro hráče co již někdy zařazen byl použít <code>vratitHrace</code>.
     * Hráč bude přidán na poslení místo, tzn před hráče co aktuálně hraje.
     * @param koho
     */
    public void pridatHrace(Hrac koho);
    
    /**
     * Změní směr hraní
     */
    public void zmenaSmeru();
}

