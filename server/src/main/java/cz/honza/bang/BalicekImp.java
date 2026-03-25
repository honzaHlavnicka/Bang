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

import java.util.*;
import java.util.function.Consumer;



public class BalicekImp<T> implements cz.honza.bang.sdk.Balicek<T>{
    private Deque<T> karty = new ArrayDeque<>();
    private boolean jeOtoceny = false;
    private Consumer<BalicekImp<T>> poUprave;

    /**
     * Vytvoří balíček a naplní ho kartami.
     * @param karty objekty, které se mají do balíčku nandat.
     */
    public BalicekImp(Collection<T> karty) {
        this.karty.addAll(karty);
    }

    /**
     * Vytvoří prázdný balíček.
     */
    public BalicekImp() {
        
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
        probehlaUprava();
    }
    
    /**
     * Privátní pomocná metoda pro vytažení jedné karty. Nevolá probehlaUprava().
     */
    private T lizniTajne() {
        if (jeOtoceny) {
            return karty.pollLast(); // vrátí null, pokud je prázdný
        } else {
            return karty.pollFirst(); // vrátí null, pokud je prázdný
        }
    }

    /**
     * Lízne jednu kartu, odstraní ji z balíčku. Pokud je balíček prázdný, tak vrátí null.
     * @return líznutá karta
     */
    public T lizni() {
        T liznuta = lizniTajne();
        probehlaUprava();
        return liznuta;
    }

    // líznutí N karet

    /**
     * Lízne <code>n</code> karet a odstraní je z balíčku. Pokud nejde líznout více karet, tak část kolekce bude null.
     * @param n počet karet k líznutí
     * @return kolekce líznutých karet seřazená tak, že karta, která se vytáhla jako první je první v kolekci
     */
    public List<T> lizni(int n) {
        List<T> tah = new ArrayList<>(n);
        for (int i = 0; i < n && !karty.isEmpty(); i++) {
            tah.add(lizniTajne());
        }
        probehlaUprava();
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
    
    /**
     * Vrátí, ale nesmaže horní kartu z balíčku. Pokud je prázdný, vrátí null
     * @return 
     */
    public T nahledni(){
        if(jeOtoceny){
            return karty.peekLast();
        }else{
            return karty.peek();
        }
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
        probehlaUprava();
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
        probehlaUprava();
        
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
        probehlaUprava();
    }
    
    /**
     * Natsaví událost, která se volá v případě změny balíčku, například změny vrchní karty.
     * @param akce
     */
    public void setPoUprave(Consumer<BalicekImp<T>> akce){
        poUprave = akce;
    }
    
    private void probehlaUprava(){
        if(poUprave != null){
            poUprave.accept(this);
        }
    }
    
    
    
    
    
}

