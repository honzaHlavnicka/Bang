/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang;


import cz.honza.bang.sdk.zastupnaKarta;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import java.util.ArrayList;
import java.util.List;
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
        // Zobrazit stavovou zprávu že hráč vybírá cíl
        hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " vybírá cíl útoku...");
        
        hra.getKomunikator().pozadejOHrace(kym, hra.getHraci(), "Vyber komu kartu spálíš", 1, 1) 
            .thenAccept(odpoved -> {
                System.out.println("Hráč odpověděl: " + odpoved);
                
                Hrac naKoho;
                try{
                    naKoho = hra.getHrac(Integer.parseInt(odpoved));
                }catch(NumberFormatException ex){
                    hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                    return; // Nemá cenu pokračovat bez vůle hráče
                }
                
                hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " vybírá kartu od " + naKoho.getJmeno() + "...");
                
                List<Karta> kartyNaVyber = new ArrayList<>();
                kartyNaVyber.addAll(naKoho.getVylozeneKarty());
                kartyNaVyber.add(zastupnaKarta.getNahodna());

                hra.getKomunikator().pozadejOKarty(kym, kartyNaVyber, "Jakou kartu mu spálíš?", 1, 1)
                    .thenAccept(idKarty -> {
                        int idKartyCislo = Integer.parseInt(idKarty);
                        if(zastupnaKarta.getNahodna().getId() == idKartyCislo){
                            Random rand = new Random();
                            Karta nahodnaKarta = naKoho.getKarty().remove(rand.nextInt(naKoho.getKarty().size()));     
                            hra.getKomunikator().posliSpaleniKarty(naKoho, nahodnaKarta);
                        }else{
                            for (Karta karta : naKoho.getVylozeneKarty()) {
                                if(karta.getId() == idKartyCislo){
                                    naKoho.getVylozeneKarty().remove(karta); //todo: kdyz mezitim karta se spali jinak, tak bude vizualne 2x
                                    hra.getKomunikator().posliSpaleniVylozenéKarty(karta, naKoho);
                                }
                            }
                        }
                });
        });
        return true;
    }
    
    
}
