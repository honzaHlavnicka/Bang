/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.concurrent.CompletableFuture;


/**
 *
 * @author honza
 */
public interface KomunikatorHry {

    /**
     * Pošle zprávu všekm hráčům ve hře.
     * @param co Zpráva, která se pošle všem hráčům
     */
    public void posliVsem(String co);
    
    
    
    /**
     * Pošle zprávu všem hráčům ve hře, kromě jednoho. Hodí se pro poslání podrobné informace jednomu hráči a méně podrobné informaci ostatním.
     * @param co Zpráva, která se pošle všem hráčům, kromě jednoho
     * @param komuNe Hráč, který zprávu neobdrží
     */
    public void posliVsem(String co,Hrac komuNe);
    
    public void posli(Hrac komu, String co);

    // ===== METODY AKCÍ =====

    
    /**
     * Pošle klientovi chybovou zprávu.
     * @param komu komu se má chyba doručit.
     * @param chyba chyba, která se posílá.
     */
    public void posliChybu(Hrac komu,Chyba chyba);
    
    /**
     * Pošle stavovou zprávu všem hráčům. Zpráva se zobrazí v centru obrazovky.
     * @param zprava Text zprávy, která se bude zobrazovat (např. "Hráč vybírá barvu...")
     */
    public void posliStavovuZpravu(String zprava);
    
    
    /**
     * Pošle všem hráčům informaci o změně počtu karet v ruce.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuPoctuKaret(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o změně počtu životů.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuPoctuZivotu(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o zahájení tahu.
     * @param hrac Hráč, jehož tah začal
     */
    public void posliZahajeniTahu(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o změně jména hráče.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuJmena(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o připojení nového hráče.
     * @param hrac Nově připojený hráč
     */
    public void posliNovehoHrace(Hrac hrac);
    
    
    /**
     * Pošle všem hráčům informaci o zahájení hry.
     */
    public void posliZahajeniHry();
    
    /**
     * Pošle všem hráčům informaci o skončení hráče v hře.
     * @param hrac Hráč, který skončil
     */
    public void posliSkonceniHrace(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o vítězství hráče.
     * @param hrac Hráč, který vyhrál
     */
    public void posliVitezstvi(Hrac hrac);
    
    
    /**
     * Pošle všem hráčům informaci o odehrání karty.
     * @param hrac Hráč, který kartu odehral
     * @param karta Odehraná karta
     */
    public void posliOdebraniKarty(Hrac hrac, Karta karta);
    
    /**
     * Pošle všem hráčům informaci o spálení karty.
     * @param hrac Hráč, jehož karta byla spálena
     * @param karta Spálená karta
     */
    public void posliSpaleniKarty(Hrac hrac, Karta karta);
    
    /**
     * Pošle všem hráčům informaci o spálení vyložené karty.
     * @param karta Spálená karta
     * @param odkud Hráč, od kterého byla karta spálena
     */
    public void posliSpaleniVylozenéKarty(Karta karta, Hrac odkud);
    
    /**
     * Pošle všem hráčům informaci o vyložení karty.
     * @param hrac Hráč, který kartu vyložil
     * @param predKoho Hráč, kterému se karta vykladá (nebo nula/prázdné jméno?)
     * @param karta Vyložená karta
     */
    public void posliVylozeniKarty(Hrac hrac, Hrac predKoho, Karta karta);
    
    /**
     * Pošle hráčům rychlé oznámení (plugin-specifická zpráva).
     * Používá se v pluginech (např. Prší - zmena barvy, Uno - zmena barvy).
     * @param oznameni Obsah oznámení (např. název barvy)
     * @param vyjimka Hráč, který zprávu neobdrží (obvykle ten, co ji vyvolal)
     */
    public void posliRychleOznameni(String oznameni, Hrac vyjimka);

    /**
     * Všem hráčům pošle zprávu, že hra skončila.
     */
    public void posliKonecHry();

    /**
     * Pošle všem hráčům výsledkovou tabulku. Není to konec hry.
     * @param vysledky pole, jehož každá položka je jedno umístění (index 0 = místo 1...) a v jednom umístění se může naházet více hráčů spolu.
     */
    public void posliVysledky(Hrac[][] vysledky);

    public CompletableFuture<String> pozadejOdpoved(String otazka,Hrac komu);
       

    public int getIdHry();
    public int pocetHracu();

    public Hrac getAdmin();

    public void setAdmin(Hrac admin);
    
    
    
}
