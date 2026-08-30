/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.pluginy.bang.PravidlaBangu;
import cz.honzaa.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honzaa.bang.pluginy.bang.zbrane.Zbran;
import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Chyba;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.HratelnaKarta;
import cz.honzaa.bang.sdk.Karta;




/**
 *
 * @author honza
 */
public class Bang extends Karta implements HratelnaKarta{

    public Bang(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }
    

    @Override
    public boolean odehrat(cz.honzaa.bang.sdk.Hrac kym){
        
        //ověření, zda hráč může kartu zahrát
        if(!((PravidlaBangu) hra.getHerniPravidla()).pokusZahratKartuDoLimituKaretBang(kym)){
            return false;
        }
        
        int vzdalenostKamDosahnePodleZbrane = kym.getEfekty().stream().filter(e -> e instanceof Zbran).findAny().map(e -> ((Zbran) e).getVzdalenost()).orElse(1);
        java.util.List<Hrac> hraciNaVyber = kym.vzdalenostPod(vzdalenostKamDosahnePodleZbrane, true);

        hra.getKomunikator().pozadejOHrace(kym, hraciNaVyber, "Vyber koho chceš zastřelit!", 1, 1, true)
            .thenAccept(odpoved -> {
                try{
                    Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved));
                    ((PravidlaBangu) hra.getHerniPravidla()).vyvolejAkciBang(kym, naKoho, this::poUtoku);
                }catch(NumberFormatException ex){
                    hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                }
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

        return true;
    }

    private void poUtoku(Hrac naKoho, Boolean uspech) { // Neodstranovat nepoužitý parametr! Je potřeba pro budoucí účely a tato funkce se postupně propašovává až do pravidelbangu do vyresvedle

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
