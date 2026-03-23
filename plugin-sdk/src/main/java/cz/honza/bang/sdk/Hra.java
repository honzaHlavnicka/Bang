/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;



/**
 * Třída samotné hry. Vnitřní logika serveru. 
 * 
 * NEočekává se od pluginu, aby třídu implemetoval
 * <p><b>NEočekává se implementace od autora pluginu</b>, ale měl by používat její dostupné metody.
 * @author honza
 *
 */
public interface Hra {


    /**
     * Vytvoří hráče. Po zavolání této metody by se měla zavolat metoda
     * hracVytvoren()
     *
     * @return nový hráč
     */
    public Hrac novyHrac();

    /**
     * Připravý hráče poté, co už je spojen se serverm. měla by se volat hned po
     * novyHrac()
     *
     * @param hrac hráč, který by se měl připravit
     */
    public void hracVytvoren(Hrac hrac);

    /**
     * Vrátí kopii seznamu všech hráčů, včetně těch vyřazených
     * @return seznam hráčů
     * @see #getHrajiciHraci() 
     */
    public List<Hrac> getHraci();
    
    /**
     * Vrátí seznam všech hrajících hráčů. 
     * Je to poze volání stejnojmené metody na spravce tahu.
     * @return kolekce hráčů seřazená podle pořadí hraní.
     * @see Hrac.getHrajicHraci()
     */
    public List<Hrac> getHrajiciHraci();

    /**
     * Vrátí hráče podle jeho id.
     *
     * @param id id hráče
     * @return Hrac nebo null
     */
    public Hrac getHrac(int id);

    public KomunikatorHry getKomunikator();

    public boolean isZahajena();

    public HerniPravidla getHerniPravidla();
    


    /**
     * Spustí hru.
     *
     * @param zahajena pokud true, tak zahájí hru.
     */
    public void setZahajena(boolean zahajena);
    
    /**
     * Vrátí originál balíčku
     */
    public Balicek<Karta> getBalicek();


    /**
     * Vyřadí hráče z herní smyčky. Nezávisle na tom jestli vyhrál nebo prohrál,
     * ale už nebude hrát.
     *
     * @param kdo
     */
    public void skoncil(Hrac kdo);

    /**
     * Zařídí problematiku výhry, ale nevyřadí hráče z hrací smyčky.
     *
     * @param kdo
     */
    public void vyhral(Hrac kdo);

    /**
     * Prohodí odhazovací a lízací balíčky. Novým lízacím balíčkem bude
     * odhazovací balíček v opačném pořadí.
     */
    public void prohodBalicky();
    
    public SpravceTahu getSpravceTahu();
    
    /**
     * Vrátí originál odhazovacího balíčku
     */
    public Balicek<Karta> getOdhazovaciBalicek();

    /**
     * Vezme vrchní kartu z dobíracího balíčku a dá ji do odhazovacího. Informuje o tom hráče.
     * Může se hodit například při zahájení prší nebo una.
     * @return karta, která byla otočena.
     */
    public Karta otocVrchniKartu();

}
