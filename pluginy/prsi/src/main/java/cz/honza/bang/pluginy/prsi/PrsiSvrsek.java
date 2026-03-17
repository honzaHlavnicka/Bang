/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import java.util.ArrayList;

/**
 *
 * @author honza
 */
public class PrsiSvrsek extends PrsiKarta{
    private PrsiBarva poslendniBarva;
    public PrsiSvrsek(Hra hra, Balicek<Karta> balicek, PrsiBarva b, PrsiHodnota h) {
        super(hra, balicek, b, h);
        poslendniBarva = null;
    }

    @Override
    public boolean odehrat(Hrac kym) {
        poslendniBarva = null;
            // Zobrazit stavovou zprávu že hráč vybírá barvu
            hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " vybírá barvu...");
            
            
            ArrayList moznosti = new ArrayList(4);
            moznosti.add("Kule");
            moznosti.add("Zelené");
            moznosti.add("Červené");
            moznosti.add("Žaludy");
            hra.getKomunikator().pozadejOVyberMoznosti(kym, moznosti, "Na co chceš změnit?").thenAccept(odpoved -> {
                System.out.println("Hráč odpověděl: " + odpoved);
                switch ( (String) odpoved) {
                    case "0":
                        poslendniBarva = PrsiBarva.KULE;
                        break;
                    case "1":
                        poslendniBarva = PrsiBarva.ZELENE;
                        break;
                    case "2":
                        poslendniBarva = PrsiBarva.CERVENE;
                        break;
                    case "3":
                        poslendniBarva = PrsiBarva.ZALUDY;
                        break;
                    default:
                        poslendniBarva = PrsiBarva.CERVENE; // Výchozí barva při chybě
                        break;
                }
                // Zobrazit informaci o vybrané barvě
                hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " si vybral barvu: " + poslendniBarva.getNazev());
                // Dodatečná zpráva pro plugin - zvláštní oznámení
                hra.getKomunikator().posliRychleOznameni(String.valueOf(poslendniBarva.getNazev()), kym);

            });//toto nemůže blokovat thred!
            return true;
        } 

    @Override
    public PrsiBarva getBarva() {
        return poslendniBarva;
    }
    
    }
    
    
