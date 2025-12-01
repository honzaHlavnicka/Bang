/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;



/**
 * Třída samotné hry.
 *
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

    public List<Hrac> getHraci();

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

    public Balicek<Karta> getBalicek();


    /**
     * Vyřadí hráče z herní smičky. Nezávisle na tom jestli vyhrál nebo prohrál,
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


    public Karta sejmiKartu();
    
    public SpravceTahu getSpravceTahu();
    public Balicek<Karta> getOdhazovaciBalicek();
}
