/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.uno;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
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
        
        List<String> moznosti = List.of("Červené", "Modré", "Zelené", "Žluté");
        
        hra.getKomunikator().pozadejOVyberMoznosti(kym, moznosti, "Vyber barvu", false)
                .thenAccept(odpoved -> {
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
            hra.getKomunikator().posliRychleOznameniVsem(podleniBarva, kym);
            
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
