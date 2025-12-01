/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */

import java.util.*;


public interface Balicek<T> {
    /**
     * Zamíchá balíček.
     */
    public void zamichej();

    /**
     * Lízne jednu kartu, odstraní ji z balíčku. Pokud je balíček prázdný, tak vrátí null.
     * @return líznutá karta
     */
    public T lizni();

    /**
     * Lízne <code>n</code> karet a odstraní je z balíčku. Pokud nejde líznout více karet, tak část kolekce bude null.
     * @param n počet karet k líznutí
     * @return kolekce líznutých karet seřazená tak, že karta, která se vytáhla jako první je první v kolekci
     */
    public List<T> lizni(int n);


    /**
     * Vrátí vrchních <code>n</code> karet a <b>ne</b>odstraní je z balíčku.
     * @param n
     * @return Kolekce. Karta, která je v balíčku nahoře, je v kolekci 1., karta která je pod ní je druhá apod.
     */
    
    public List<T> nahledni(int n);
    
    /**
     * Vrátí, ale nesmaže horní kartu z balíčku.
     * @return 
     */
    public T nahledni();

    // 

    /**
     * vrácení karty na spodek balíčku
     * @param karta (nebo objekt), který se má vrátit dolů.
     */
    public void vratNaSpodek(T karta);

    /**
     * vrácení karty na vršek balíčku
     *
     * @param karta (nebo objekt), který se má vrátit nahoru.
     */
    public void vratNahoru(T karta);

    /**
     * Kontrola, zda balíček obsahuje nějaký prvek. <code>true</code> = prázdný.
     * @return zda je prázdný
     */
    public boolean jePrazdny();

    /**
     * Vrací počet prvků v balíčku.
     * @return velikost balíčku
     */
    public int pocet();
    
    /**
     *  Otočí balíček tak, že karta, která byla doposud nahoře bude dole a karta, ktrá byla dole bude nahoře.
     *  Není problém používat často, protože se v paměti neprohazuje, jenom se bere z druhé strany.
     */
    public void otoc();

    
}

