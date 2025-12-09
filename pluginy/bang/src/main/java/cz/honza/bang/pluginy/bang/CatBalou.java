/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;


import cz.honza.bang.sdk.zastupnaKarta;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import java.util.Random;
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
                        int idKartyCislo = Integer.parseInt(idKarty);
                        if(zastupnaKarta.getNahodna().getId() == idKartyCislo){
                            Random rand = new Random();
                            Karta nahodnaKarta = naKoho.getKarty().remove(rand.nextInt(naKoho.getKarty().size()));     
                            hra.getKomunikator().posliVsem("spalit:"+kym.getId()+"|"+nahodnaKarta.toJSON());
                        }else{
                            for (Karta karta : naKoho.getVylozeneKarty()) {
                                if(karta.getId() == idKartyCislo){
                                    naKoho.getVylozeneKarty().remove(karta); //todo: kdyz mezitim karta se spali jinak, tak bude vizualne 2x
                                    hra.getKomunikator().posliVsem("spalit:" + naKoho.getId() + "|" + karta.toJSON());
                                }
                            }
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
        kartyNaVyber.put(new JSONObject(zastupnaKarta.getNahodna().toJSON()));
        System.out.println("Připravuju karty na výběr: "+naKoho.getVylozeneKarty().size());
        for (Karta karta : naKoho.getVylozeneKarty()) {
            System.out.println(karta.getJmeno());
            kartyNaVyber.put(new JSONObject(karta.toJSON()));
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
