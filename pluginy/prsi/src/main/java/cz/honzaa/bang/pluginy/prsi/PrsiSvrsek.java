/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.prsi;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import java.util.ArrayList;

/**
 *
 * @author honza
 */
public class PrsiSvrsek extends PrsiKarta{
    private PrsiBarva poslendniBarva;
    private boolean cekaNaBarvu = false;
    
    public PrsiSvrsek(Hra hra, Balicek<Karta> balicek, PrsiBarva b, PrsiHodnota h) {
        super(hra, balicek, b, h);
        poslendniBarva = null;
    }

    public boolean isCekaNaBarvu() {
        return cekaNaBarvu;
    }

    @Override
    public boolean odehrat(Hrac kym) {
        poslendniBarva = null;
        cekaNaBarvu = true;
            // Zobrazit stavovou zprávu že hráč vybírá barvu
            hra.getKomunikator().posliStavovouZpravu("$prsi.status_choosing_color:{\"name\":\"" + Karta.escapeJson(kym.getJmeno()) + "\"}");
            
            
            ArrayList<String> moznosti = new ArrayList<>(4);
            moznosti.add("$prsi.color_kule");
            moznosti.add("$prsi.color_zelene");
            moznosti.add("$prsi.color_cervene");
            moznosti.add("$prsi.color_zaludy");
            hra.getKomunikator().pozadejOVyberMoznosti(kym, moznosti, "$prsi.select_color", false).thenAccept(odpoved -> {
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
                cekaNaBarvu = false;
                // Zobrazit informaci o vybrané barvě
                hra.getKomunikator().posliStavovouZpravu("$prsi.status_chosen_color:{\"name\":\"" + Karta.escapeJson(kym.getJmeno()) + "\",\"color\":\"$" + poslendniBarva.getTranslationKey() + "\"}");
                // Dodatečná zpráva pro plugin - zvláštní oznámení
                hra.getKomunikator().posliRychleOznameniVsem("$" + poslendniBarva.getTranslationKey(), kym);
                
                // Nyní předáme tah dalšímu hráči
                hra.getSpravceTahu().dalsiHracSUpozornenim();

            });//toto nemůže blokovat thred!
            return true;
        } 

    @Override
    public PrsiBarva getBarva() {
        return poslendniBarva != null ? poslendniBarva : super.getBarva();
    }
    
    }
    
    
