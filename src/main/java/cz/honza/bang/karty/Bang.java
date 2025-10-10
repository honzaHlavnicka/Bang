/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.Balicek;
import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author honza
 */
public class Bang extends Karta implements HratelnaKarta{

    public Bang(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    
    

    
    @Override
    public boolean odehrat(Hrac kym){
        
        hra.getKomunikator().pozadejOdpoved( "vyberHrace:" + pripravJSONvyberuHrace(kym), kym)
            .thenAccept(odpoved -> {

                System.out.println("Hráč odpověděl: " + odpoved);
                Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved));
                
                //TODO: tady by mela probehnout nejaka kontrola barelu a podobne.
                //TODO: tady by jsme se meli zeptat druheho hrace, zda nema vedle.
                
                naKoho.odeberZivot();
                
                hra.getSpravceTahu().dalsiHracSUpozornenim();
        });
        
        return true;
 
    }
    
    /**
     * Pomocná metoda, která vytvoří JSON všech hráčů, na které jde zautocit.
     * @param hracCoOdehral
     * @return json pro klienta.
     */
    private String pripravJSONvyberuHrace(Hrac hracCoOdehral){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", "Vyber koho chceš zastřelit!");
        JSONArray hraciNaVyber = new JSONArray();
        for (Hrac hrac : hra.getHraci()) {
            if (!hrac.equals(hracCoOdehral))//TODO: zkontrolovat zda je nadosah
            {
                hraciNaVyber.put(hrac.getId());
            }
        }
        json.put("hraci", hraciNaVyber);
        return json.toString();

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
