/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.pluginy.bang.PravidlaBangu;
import cz.honza.bang.pluginy.bang.zbrane.Zbran;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import java.util.List;

/**
 *
 * @author honza
 */
public class DvojitaRana extends Karta implements HratelnaKarta{

    public DvojitaRana(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "dvojitarana";
    }

    @Override
    public String getJmeno() {
        return "Dvojitá rána";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        
        if (!kym.getKarty().stream().anyMatch(k -> k instanceof Bang)) {
            return false;
        }
        
        if(((PravidlaBangu) hra.getHerniPravidla()).pokusZahratKartuDoLimituKaretBang(kym)){
            return false;
        }
        
        List<Karta> bangy = kym.getKarty().stream().filter((k)->k instanceof Bang).toList();
        hra.getKomunikator().pozadejOKarty(kym, bangy , "Jaký Bang použiješ?", 1, 1, true).thenAccept((String id)->{
            try{
                int intId = Integer.parseInt(id);
                Karta bang = bangy.stream().filter((k)->k.getId() == intId).findFirst().orElseThrow();
                
                int vzdalenostKamDosahnePodleZbrane = kym.getEfekty().stream().filter(e -> e instanceof Zbran).findAny().map(e -> ((Zbran) e).getVzdalenost()).orElse(1);
                List<Hrac> hraciNaVyber = kym.vzdalenostPod(vzdalenostKamDosahnePodleZbrane, true);
                
                
                kym.getKarty().remove(bang);
                hra.getOdhazovaciBalicek().vratNahoru(bang);
                hra.getKomunikator().posliOdebraniKarty(kym, bang);
                hra.getKomunikator().posliZmenuPoctuKaret(kym);
                
                hra.getKomunikator().pozadejOHrace(kym, hraciNaVyber, "Na koho střílíš?", 1, 1, true).thenAccept((idHrace)-> {
                    try{
                        int intIdHrace = Integer.parseInt(idHrace);
                        
                        Hrac naKoho = hra.getHrac(intIdHrace);
                        
                        ((PravidlaBangu) hra.getHerniPravidla()).vyvolejAkciBang(kym, naKoho, ((Hrac naKoho2, Boolean uspech)->{
                            if(uspech){
                                naKoho.odeberZivot();
                            }
                        }));
                        
                        
                        
                    }catch(Exception ex){
                        hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                    }
                });
                
            }catch(Exception ex){
                hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                return;
            }
        });
        
        return true;

    }
    
    
    
}
