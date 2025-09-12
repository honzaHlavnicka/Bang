/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
import cz.honza.bang.Role;
import cz.honza.bang.postavy.Postava;

/**
 *
 * @author honza
 */
public class Bang extends Karta implements HratelnaKarta{

    public Bang(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    
    

    
    @Override
    public void odehrat(Hrac kym){
        //TODO: získej hráče
 
    }
    
    @Override
    public String getObrazek(){
        return "bang";
    }
    
    @Override
    public String getJmeno() {
        return "BANG!";
    }
}
