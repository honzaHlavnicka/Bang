/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;

/**
 *
 * @author honza
 */
public class PrsiSvrsek extends PrsiKarta{
    private PrsiBarva poslendniBarva;
    public PrsiSvrsek(Hra hra, Balicek<Karta> balicek, PrsiBarva b, PrsiHodnota h) {
        super(hra, balicek, b, h);
    }

    @Override
    public boolean odehrat(Hrac kym) {
        poslendniBarva = null;
            hra.getKomunikator().pozadejOdpoved(
                    "vyberAkci:{\"id\":data-id,\"akce\":["
                    + "{\"id\":0,\"nazev\":\"Kule\"},"
                    + "{\"id\":1,\"nazev\":\"Zelené\"},"
                    + "{\"id\":2,\"nazev\":\"Červené\"},"
                    + "{\"id\":3,\"nazev\":\"Žaludy\"}"
                    + "]}",
                    kym
            ).thenAccept(odpoved -> {
                System.out.println("Hráč odpověděl: " + odpoved);
                switch (odpoved) {
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
                }
                hra.getKomunikator().posliVsem("popup:Barva změněna na " + poslendniBarva, kym);

            });//toto nmůže blokovat thred!
            //TODO: udelat, aby neslo hrat, nez se slib splní
            return true;
        } 
    }
    
    
