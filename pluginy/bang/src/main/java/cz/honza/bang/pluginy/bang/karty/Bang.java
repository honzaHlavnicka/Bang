/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.pluginy.bang.BarelEfekt;
import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.zbrane.Zbran;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.ZastupnaKarta;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;




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
            Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved));
            
            boolean melBarel = naKoho.getEfekty().stream().anyMatch(e -> e instanceof BarelEfekt);

            if (melBarel) {
                boolean zachranen = ((BarelEfekt) naKoho.getEfekty().stream().filter(e -> e instanceof BarelEfekt).findFirst().get()).aktivovat(hra, naKoho);
                
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        vyresitVedleNeboZasah(kym, naKoho, zachranen);
                    }
                }, 5000); 

            } else {
                // Hráč nemá barel
                vyresitVedleNeboZasah(kym, naKoho, false);
            }
        })
        .exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        
    return true;
}

    private void poUtoku(Hrac kym) {
        boolean maVolcanic = kym.getEfekty().stream()
                .filter(e -> e instanceof Zbran)
                .anyMatch(e -> ((Zbran) e).umoznujeBangBezLimitu());

        if (!maVolcanic && kym.getPostava() != JednoduchePostavy.WILLY_THE_KID) {
            hra.getSpravceTahu().dalsiHracSUpozornenim();
        }
    }

    private void vyresitVedleNeboZasah(Hrac kym, Hrac naKoho, boolean zachranenBarelem) {
    if (!zachranenBarelem) {
        List<Karta> vedleNaKoho = naKoho.getKarty().stream()
                .filter(k -> k instanceof Vedle)
                .collect(java.util.stream.Collectors.toList());
        
        if (!vedleNaKoho.isEmpty()) {
            vedleNaKoho.add(ZastupnaKarta.getZivot());
            hra.getKomunikator().posliStavovuZpravu(naKoho.getJmeno() +" může ještě použít vedle na odražení útoku!");
            
            hra.getKomunikator().pozadejOKarty(naKoho, vedleNaKoho, "Vyber o co přijdeš. (Může za to " + kym.getJmeno() + " )", 1, 1, false)
                .thenAccept(id -> {
                    int idInt;
                    try {
                        idInt = Integer.parseInt(id);
                    } catch(NumberFormatException ex) {
                        naKoho.odeberZivot();
                        poUtoku(kym);
                        return;
                    }
                    
                    if (idInt == ZastupnaKarta.getZivot().getId()) {
                        hra.getKomunikator().posliRychleOznameni("Trefa!", null);
                        naKoho.odeberZivot();
                    } else {
                        for (Karta karta : vedleNaKoho) {
                            if(karta.getId() == idInt) {
                                naKoho.getKarty().remove(karta);
                                hra.getOdhazovaciBalicek().vratNahoru(karta);
                                hra.getKomunikator().posliOdebraniKarty(naKoho, karta);
                                hra.getKomunikator().posliZmenuPoctuKaret(naKoho);
                                hra.getKomunikator().posliRychleOznameni("Vedle!", null);
                                break; 
                            }
                        }
                    }
                    poUtoku(kym); 
                });
        } else {
            // Nemá ani barel, ani Vedle, přichází o život
            hra.getKomunikator().posliRychleOznameni("Trefa!", null);
            naKoho.odeberZivot();
            poUtoku(kym);
        }
    } else {
         // Byl zachráněn barelem
         poUtoku(kym);
    }
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
