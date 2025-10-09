/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

/**
 *
 * @author honza
 */

import cz.honza.bang.karty.Karta;
import java.util.*;


public class Balicek<T> {
    private Deque<T> karty = new ArrayDeque<>();
    private boolean jeOtoceny = false;

    /**
     * Vytvoří balíček a naplní ho kartami.
     * @param karty objekty, které se mají do balíčku nandat.
     */
    public Balicek(Collection<T> karty) {
        this.karty.addAll(karty);
    }

    /**
     * Vytvoří prázdný balíček.
     */
    public Balicek() {
        
    }

    // zamíchání

    /**
     * Zamíchá balíček.
     */
    public void zamichej() {
        List<T> list = new ArrayList<>(karty);
        Collections.shuffle(list);
        karty.clear();
        karty.addAll(list);
    }

    /**
     * Lízne jednu kartu, odstraní ji z balíčku. Pokud je balíček prázdný, tak vrátí null.
     * @return líznutá karta
     */
    public T lizni() {
        if(jeOtoceny){
            return karty.pollLast(); // vrátí null, pokud je prázdný
        }else{
            return karty.pollFirst(); // vrátí null, pokud je prázdný
        }
        
    }

    // líznutí N karet

    /**
     * Lízne <code>n</code> karet a odstraní je z balíčku. Pokud nejde líznout více karet, tak část kolekce bude null.
     * @param n počet karet k líznutí
     * @return kolekce líznutých karet seřazená tak, že karta, která se vytáhla jako první je první v kolekci
     */
    public List<T> lizni(int n) {
        List<T> tah = new ArrayList<>();
        for (int i = 0; i < n && !karty.isEmpty(); i++) {
            tah.add(lizni());
        }
        return tah;
    }


    /**
     * Vrátí vrchních <code>n</code> karet a <b>ne</b>odstraní je z balíčku.
     * @param n
     * @return Kolekce. Karta, která je v balíčku nahoře, je v kolekci 1., karta která je pod ní je druhá apod.
     */
    
    public List<T> nahledni(int n) {
        List<T> nahled = new ArrayList<>();
        Iterator<T> it = jeOtoceny ? karty.descendingIterator() : karty.iterator();
        for (int i = 0; i < n && it.hasNext(); i++) {
            nahled.add(it.next());
        }
        return nahled;
    }

    // 

    /**
     * vrácení karty na spodek balíčku
     * @param karta (nebo objekt), který se má vrátit dolů.
     */
    public void vratNaSpodek(T karta) {
        if (jeOtoceny) {
            karty.addFirst(karta);
        } else {
            karty.addLast(karta);
        }
    }

    /**
     * vrácení karty na vršek balíčku
     *
     * @param karta (nebo objekt), který se má vrátit nahoru.
     */
    public void vratNahoru(T karta) {
        if(jeOtoceny){
            karty.addLast(karta);
        }else{
            karty.addFirst(karta);
        }
        
    }

    /**
     * Kontrola, zda balíček obsahuje nějaký prvek. <code>true</code> = prázdný.
     * @return zda je prázdný
     */
    public boolean jePrazdny() {
        return karty.isEmpty();
    }

    /**
     * Vrací počet prvků v balíčku.
     * @return velikost balíčku
     */
    public int pocet() {
        return karty.size();
    }
    
    /**
     *  Otočí balíček tak, že karta, která byla doposud nahoře bude dole a karta, ktrá byla dole bude nahoře.
     *  Není problém používat často, protože se v paměti neprohazuje, jenom se bere z druhé strany.
     */
    public void otoc(){
        jeOtoceny = !jeOtoceny;
    }
    
    /**
     * Vrací balíček jako Deque. Není to jeho kopie, ale přímí odkaz, tudíž jeho změna přepisuje balíček.
     * <b>Pozor!</b> Balíček může být otočený.
     * @return
     * @deprecated 
     */
    @Deprecated
    public Deque<T> toDeque(){
        return karty;
    }
    
}

