/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.zbrane.Zbran;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;


/**
 *
 * @author honza
 */
public class Bang extends Karta implements HratelnaKarta{

    public Bang(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    
    @Override
    public boolean odehrat(cz.honza.bang.sdk.Hrac kym){
        
        int vzdalenostKamDosahnePodleZbrane = kym.getEfekty().stream().filter(e -> e instanceof Zbran).findAny().map(e -> ((Zbran) e).getVzdalenost()).orElse(1);
        java.util.List<Hrac> hraciNaVyber = kym.vzdalenostPod(vzdalenostKamDosahnePodleZbrane, true);
        
        hra.getKomunikator().pozadejOHrace(kym, hraciNaVyber, "Vyber koho chceš zastřelit!", 1, 1, true)
            .thenAccept(odpoved -> {

                System.out.println("Hráč odpověděl: " + odpoved);
                Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved));
                

                
                //TODO: tady by mela probehnout nejaka kontrola barelu a podobne.
                //TODO: tady by jsme se meli zeptat druheho hrace, zda nema vedle.
                
                naKoho.odeberZivot();
                
                
                if(!kym.getEfekty().stream().filter(e -> e instanceof Zbran).findAny().map(e -> ((Zbran) e).umoznujeBangBezLimitu()).orElse(false) && kym.getPostava() != JednoduchePostavy.WILLY_THE_KID){
                    hra.getSpravceTahu().dalsiHracSUpozornenim();
                }
                
        });
        
        return true;
 
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
