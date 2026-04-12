/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.kvarteto;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.SpravceTahu;
import java.util.List;

/**
 *
 * @author honza
 */
public class PravidlaKvarteta implements HerniPravidla{
    private Hra hra;
    public PravidlaKvarteta(Hra hra) {
        this.hra = hra;
    }

    @Override
    public void poSpusteniHry() {
        List<Hrac> hraci = hra.getHrajiciHraci();
        int aktualniHrac = 0;
        
        while(!hra.getBalicek().jePrazdny()){
            hraci.get(aktualniHrac % hraci.size()).lizni();
            aktualniHrac ++;
        }
        
    }
    
    

    @Override
    public void pripravitHrace(Hrac hrac) {
        
    }

    @Override
    public void poOdehrani(Hrac kym) {
        
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return false;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return false;
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                balicek.vratNahoru(new KartaKvarteta(i, j, hra, balicek));
            }
            
        }
        
        balicek.zamichej();
    }

    @Override
    public void zacalTah(Hrac komu) {
        // Tady bude logika hry
        
        hra.getKomunikator().pozadejOHrace(komu, hra.getHrajiciHraci(), "vyber hráče", 1, 1, false)
                .thenAccept(idHrace->{
                    try{
                        vyberKartu(komu, hra.getHrac(Integer.parseInt(idHrace)));
                    }catch(NumberFormatException ex){
                        //TODO
                    }
                });
    }
    
    
    private void vyberKartu(Hrac kym, Hrac odKoho){
        hra.getKomunikator().pozadejOText(kym, "Jakou kartu? Formát \"<cislo sady>:<cislo karty>\"", "5:1", "Má ji?", true)
                .thenAccept(textCoZadal -> {
                    String[] data = textCoZadal.trim().toLowerCase().split(textCoZadal, 2);
                    hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " chce " + textCoZadal + " od hráče " + odKoho.getJmeno());
                    if(data.length == 2){
                        try{
                            int hodnota = Integer.parseInt(data[0]);
                            int barva   = Integer.parseInt(data[1]);
                            
                            boolean nalezenaShoda = false;
                            
                            for (Karta karta : kym.getKarty()) {
                                if(karta instanceof KartaKvarteta){
                                    KartaKvarteta k = (KartaKvarteta) karta;
                                    if(k.getBarva() == barva && k.getHodnota() == hodnota){
                                        nalezenaShoda = true;
                                        hra.getKomunikator().posliRychleOznameniVsem("Uhodnuto!", null);
                                        odKoho.getKarty().remove(karta);
                                        kym.getKarty().add(karta);
                                        hra.getKomunikator().posliNovouKartu(kym, karta);
                                        hra.getKomunikator().posliSpaleniKarty(odKoho, karta);
                                        break;
                                    }
                                        
                                }
                            }
                            
                            if(nalezenaShoda){
                                hra.getSpravceTahu().dalsiHracPodlePodminky(h->h.equals(kym));
                                return;
                            }else{
                                hra.getKomunikator().posliRychleOznameniVsem("Vedle!", null);
                                hra.getSpravceTahu().dalsiHracPodlePodminky(h->h.equals(odKoho));
                            }
                        }catch(NumberFormatException ex){
                            //todo
                            vyberKartu(kym, odKoho);
                        }
                    }else{
                        //TODO
                        vyberKartu(kym, odKoho);
                    }
                });
    }

    @Override
    public void spustitPrvniTah(SpravceTahu spravceTahu) {
        HerniPravidla.super.spustitPrvniTah(spravceTahu); 
    }
    
    
    
    
}
