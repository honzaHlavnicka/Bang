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

    // naplnění balíčku
    public Balicek(Collection<T> karty) {
        this.karty.addAll(karty);
    }

    public Balicek() {
        
    }

    // zamíchání
    public void zamichej() {
        List<T> list = new ArrayList<>(karty);
        Collections.shuffle(list);
        karty.clear();
        karty.addAll(list);
    }

    // líznutí jedné karty
    public T lizni() {
        return karty.pollFirst(); // vrátí null, pokud je prázdný
    }

    // líznutí N karet
    public List<T> lizni(int n) {
        List<T> tah = new ArrayList<>();
        for (int i = 0; i < n && !karty.isEmpty(); i++) {
            tah.add(karty.pollFirst());
        }
        return tah;
    }

    // nahlédnutí na vrchní N karet (neodebere je)
    public List<T> nahledni(int n) {
        List<T> nahled = new ArrayList<>();
        Iterator<T> it = karty.iterator();
        for (int i = 0; i < n && it.hasNext(); i++) {
            nahled.add(it.next());
        }
        return nahled;
    }

    // vrácení karty na spodek balíčku
    public void vratNaSpodek(T karta) {
        karty.addLast(karta);
    }

    // vrácení karty nahoru
    public void vratNahoru(T karta) {
        karty.addFirst(karta);
    }

    // je balíček prázdný?
    public boolean jePrazdny() {
        return karty.isEmpty();
    }

    // kolik karet zbývá
    public int pocet() {
        return karty.size();
    }
    
    public Deque<T> toDeque(){
        return karty;
    }
    
}

