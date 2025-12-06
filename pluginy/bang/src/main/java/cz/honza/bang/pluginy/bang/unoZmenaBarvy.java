/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;

/**
 *
 * @author honza
 */
public class unoZmenaBarvy extends UnoKarta {
    private String podleniBarva = "neni";
    
    
    public unoZmenaBarvy(Hra hra, Balicek<Karta> balicek) {
        super(-1, "", hra, balicek);
    }
    
    @Override
    public boolean odehrat(Hrac kym){
        podleniBarva = "neni";
        hra.getKomunikator().pozadejOdpoved(
                "vyberAkci:{\"id\":data-id,\"akce\":["
                + "{\"id\":0,\"nazev\":\"Červené\"},"
                + "{\"id\":1,\"nazev\":\"Modré\"},"
                + "{\"id\":2,\"nazev\":\"Zelené\"},"
                + "{\"id\":3,\"nazev\":\"Žluté\"}"
                + "]}",
                kym
        ).thenAccept(odpoved -> {
            System.out.println("Hráč odpověděl: " + odpoved);
            switch(odpoved){
                case "0":
                    podleniBarva = "red";
                    break;
                case "1":
                    podleniBarva = "blue";
                    break;
                case "2":
                    podleniBarva = "green";
                    break;
                case "3":
                    podleniBarva = "yellow";
                    System.out.println("zlutá");
                    break;
            }
            hra.getKomunikator().posliVsem("rychleOznameni:" + podleniBarva, kym);
            
        });//toto nmůže blokovat thred!
        //TODO: udelat, aby neslo hrat, nez se slib splní
        return true;
    }
    
    @Override
    public String getBarva() {
        return podleniBarva;
    }

    @Override
    public String getObrazek() {
        
        return "uno/menic";
    }

    @Override
    public String getJmeno() {
        return "Měnič";
    }
    
}
