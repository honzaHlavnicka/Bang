/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.pluginy.bang.PravidlaBangu;
import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.zbrane.Zbran;
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
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
        
        
        boolean maVolcanic = kym.getEfekty().stream()
                .filter(e -> e instanceof Zbran)
                .anyMatch(e -> ((Zbran) e).umoznujeBangBezLimitu());

        if (!maVolcanic && kym.getPostava() != JednoduchePostavy.WILLY_THE_KID) {
           if(((PravidlaBangu) hra.getHerniPravidla()).UzZahralBang()){
               return false; //Nemůže zahrát 2 bangy
           }
           ((PravidlaBangu) hra.getHerniPravidla()).setUzZahralBang(true);
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

    private void poUtoku(Hrac kym, Hrac naKoho) { // Neodstranovat nepoužitý parametr! Je potřeba pro budoucí účeli a tato funkce se postupně propašovává až do pravidelbangu do vyresvedle

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
