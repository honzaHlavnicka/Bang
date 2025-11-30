/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

/**
 *
 * @author honza
 */
public class ZbranovyEfekt implements Efekt{
    private int vzdalenost;

    public ZbranovyEfekt(int vzdalenost) {
        this.vzdalenost = vzdalenost;
    }
    
    public int getVdalenost(){
        return vzdalenost;
    }
    
}
