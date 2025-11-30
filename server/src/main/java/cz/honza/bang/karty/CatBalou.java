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
public class CatBalou extends Karta implements HratelnaKarta{

    public CatBalou(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "catBalou";
    }

    @Override
    public String getJmeno() {
        return "Cat Balou";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        hra.getKomunikator().pozadejOdpoved( "vyberHrace:" + pripravJSONvyberuHrace(kym), kym)
            .thenAccept(odpoved -> {
                
                System.out.println("Hráč odpověděl: " + odpoved);
                Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved)); //TODO: možná nějaká exception kontrola, DRY:Bang
                hra.getKomunikator().pozadejOdpoved("vyberKartu:" + pripravJSONvyberuKarty(naKoho), kym)
                    .thenAccept(idKarty -> {
                        if(zastupnaKarta.getNahodna().getId() == Integer.parseInt(idKarty)){
                            //sebrat náhodou kartu
                        }else{
                            //najít kartu a spálit jí
                        }
                });
        });
        return true;
    }
    
    private String pripravJSONvyberuKarty(Hrac naKoho){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", "Vyber jakou kartu!");
        JSONArray kartyNaVyber = new JSONArray();
        kartyNaVyber.put(zastupnaKarta.getNahodna());
        for (Karta karta : naKoho.getVylozeneKarty()) {
            kartyNaVyber.put(karta.toJSON());
        }
        json.put("karty", kartyNaVyber);
        return json.toString();
    }
    
    private String pripravJSONvyberuHrace(Hrac hracCoOdehral) { //TODO: DRY: Bang
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", "Vyber komu sebereš kartu!");
        JSONArray hraciNaVyber = new JSONArray();
        for (Hrac hrac : hra.getHraci()) {
            hraciNaVyber.put(hrac.getId());
        }
        json.put("hraci", hraciNaVyber);
        return json.toString();
    }
}
