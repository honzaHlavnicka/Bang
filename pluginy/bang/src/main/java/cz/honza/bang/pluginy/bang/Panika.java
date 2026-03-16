/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.zastupnaKarta;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author honza
 */
public class Panika extends CatBalou implements HratelnaKarta{

    public Panika(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "panika";
    }

    @Override
    public String getJmeno() {
        return "Panika";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        // Zobrazit stavovou zprávu že hráč vybírá cíl
        hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " vybírá cíl útoku...");

        hra.getKomunikator().pozadejOdpoved("vyberHrace:" + pripravJSONvyberuHrace(kym), kym)
                .thenAccept(odpoved -> {

                    System.out.println("Hráč odpověděl: " + odpoved);
                    Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved)); //TODO: možná nějaká exception kontrola, DRY:Bang, DRY: catBalou

                    // Zobrazit stavovou zprávu že hráč vybírá kartu
                    hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " vybírá kartu od " + naKoho.getJmeno() + "...");

                    hra.getKomunikator().pozadejOdpoved("vyberKartu:" + pripravJSONvyberuKarty(naKoho), kym)
                            .thenAccept(idKarty -> {
                                int idKartyCislo = Integer.parseInt(idKarty);
                                if (zastupnaKarta.getNahodna().getId() == idKartyCislo) {
                                    Random rand = new Random();
                                    Karta nahodnaKarta = naKoho.getKarty().remove(rand.nextInt(naKoho.getKarty().size())); 
                                    kym.getKarty().add(nahodnaKarta);
                                    hra.getKomunikator().posliZmenuPoctuKaret(naKoho);
                                    hra.getKomunikator().posliNovouKartu(kym, nahodnaKarta);
                                    hra.getKomunikator().posliSpaleniKarty(naKoho, nahodnaKarta);
                                } else {
                                    for (Karta karta : naKoho.getVylozeneKarty()) {
                                        if (karta.getId() == idKartyCislo) {
                                            naKoho.getVylozeneKarty().remove(karta);
                                            kym.getKarty().add(karta);
                                            hra.getKomunikator().posliNovouKartu(kym, karta);
                                            hra.getKomunikator().posliZmenuPoctuKaret(naKoho);
                                            hra.getKomunikator().posliSpaleniVylozenéKarty(karta, naKoho);
                                        }
                                    }
                                }
                            });
                });
        return true;
    }
    
    
    @Override
    String pripravJSONvyberuHrace(Hrac hracCoOdehral) {
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", "Vyber komu sebereš kartu! (A budeš si ji moc nechat)");
        JSONArray hraciNaVyber = new JSONArray();
        
        for (Hrac hrac : hracCoOdehral.vzdalenostPod(1)) {  // Neplatí na ní zbraně, ale efekty platí. (Vzdálenost max 1, ale s hledím to je 2 apod.)
            hraciNaVyber.put(hrac.getId());
        }
        
        json.put("hraci", hraciNaVyber);
        return json.toString();
    }
}
