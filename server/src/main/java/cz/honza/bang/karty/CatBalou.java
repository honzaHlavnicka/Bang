/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.karty;

import cz.honza.bang.BalicekImp;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author honza
 */
public class CatBalou extends Karta implements HratelnaKarta{

    public CatBalou(HraImp hra, BalicekImp<Karta> balicek) {
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
    public boolean odehrat(HracImp kym) {
        hra.getKomunikator().pozadejOdpoved( "vyberHrace:" + pripravJSONvyberuHrace(kym), kym)
            .thenAccept(odpoved -> {
                
                System.out.println("Hráč odpověděl: " + odpoved);
                HracImp naKoho = hra.getHrac(Integer.parseInt(odpoved)); //TODO: možná nějaká exception kontrola, DRY:Bang
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
    
    private String pripravJSONvyberuKarty(HracImp naKoho){
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
    
    private String pripravJSONvyberuHrace(HracImp hracCoOdehral) { //TODO: DRY: Bang
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", "Vyber komu sebereš kartu!");
        JSONArray hraciNaVyber = new JSONArray();
        for (HracImp hrac : hra.getHraci()) {
            hraciNaVyber.put(hrac.getId());
        }
        json.put("hraci", hraciNaVyber);
        return json.toString();
    }
}
