/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.uno;


import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import java.util.List;

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
        
        List<String> moznosti = List.of("$uno.color_red", "$uno.color_blue", "$uno.color_green", "$uno.color_yellow");
        
        hra.getKomunikator().pozadejOVyberMoznosti(kym, moznosti, "$uno.select_color", false)
                .thenAccept(odpoved -> {
            System.out.println("Hráč odpověděl: " + odpoved);
            String oznameniKey = "$uno.color_red";
            switch(odpoved){
                case "0":
                    podleniBarva = "red";
                    oznameniKey = "$uno.color_red";
                    break;
                case "1":
                    podleniBarva = "blue";
                    oznameniKey = "$uno.color_blue";
                    break;
                case "2":
                    podleniBarva = "green";
                    oznameniKey = "$uno.color_green";
                    break;
                case "3":
                    podleniBarva = "yellow";
                    oznameniKey = "$uno.color_yellow";
                    System.out.println("zlutá");
                    break;
            }
            hra.getKomunikator().posliRychleOznameniVsem(oznameniKey, kym);
            
        });
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
        return "$uno.wild";
    }
    
}
