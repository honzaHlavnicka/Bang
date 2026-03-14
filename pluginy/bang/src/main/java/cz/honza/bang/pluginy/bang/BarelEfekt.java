/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import java.util.Random;

/**
 *
 * @author honza
 */
public class BarelEfekt implements Efekt {

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public void prirazeni(Hrac komu) {
    }

    
    //TODO: naprogramovat tento efekt :)

    @Override
    public void poZtrateZivota(Hra hra, Hrac hrac) {
        Random r = new Random();
        if(r.nextInt(3) == 0){
            // Záchrana barelem;
            hrac.pridejZivot();
            hra.getKomunikator().posli(hrac,"TODO: byl jsi zachráněn barelem");
        }else{
            // Barel k ničemu
      
        }
    }
    
    
}
