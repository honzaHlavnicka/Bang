/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author honza
 */
public class Hokynarstvi extends Karta implements HratelnaKarta{

    public Hokynarstvi(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "hokynarstvi";
    }

    @Override
    public String getJmeno() {
        return "Hokynářství";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        List<Karta> karty = new ArrayList<>(hra.getHrajiciHraci().size());
        for (int i = 0; i < hra.getHrajiciHraci().size(); i++) {
            karty.add(hra.getBalicek().lizni());
        }
        
        nechatVybrat(karty, hra.getHrajiciHraci(), hra.getHrajiciHraci().size() - 1);
        
        
        return true;
    }
    
    /**
     * Nechá hráče vybrat mezi seznamem karet.
     * @param karty jaké karty rozdat
     * @param hrajiciHraci mezi koho
     * @param uKohoZacit u jaké položky v seznamu začít
     */
    private void nechatVybrat(List<Karta> karty, List<Hrac> hrajiciHraci, int uKohoZacit){
        // TODO: je tu potřeba ontrola uKohoZacit a .size() ?
        Hrac hrac = hrajiciHraci.get(uKohoZacit);
        hra.getKomunikator().posliStavovuZpravu(hrac.getJmeno() + " vybírá kartu od hokynářství");
        
        hra.getKomunikator().pozadejOKarty(hrac, karty, "Jakou kartu si chceě nechat?", 1, 1, false)
                .thenAccept(id->{
                    int idKarty;
                    try{
                        idKarty = Integer.parseInt(id);
                    }catch(NumberFormatException ex){
                        hra.getKomunikator().posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
                        
                        nechatVybrat(karty, hrajiciHraci, uKohoZacit); //Druhý pokus
                        return;
                    }
                    
                    boolean kartaNalezena = false;
                    for (Karta karta : karty) {
                        if(karta.getId() == idKarty){
                            kartaNalezena = true;
                            hrac.getKarty().add(karta);
                            karty.remove(karta);
                            hra.getKomunikator().posliNovouKartu(hrac, karta);
                            hra.getKomunikator().posliZmenuPoctuKaret(hrac);
                            break;
                        }
                    }
                    
                    if (!kartaNalezena && !karty.isEmpty()) {
                        Karta vnucenaKarta = karty.remove(0); // Vezme a rovnou smaže první kartu
                        hrac.getKarty().add(vnucenaKarta);
                        hra.getKomunikator().posliNovouKartu(hrac, vnucenaKarta);
                        hra.getKomunikator().posliZmenuPoctuKaret(hrac);
                    }
                    
                    
                    int pointer = uKohoZacit; // Přejmenování, protože nejde upravovat proměná v then blocku
                    if (!karty.isEmpty()) {
                        if (pointer >= hrajiciHraci.size() - 1) {
                            pointer = 0;
                        } else {
                            pointer++;
                        }
                        nechatVybrat(karty, hrajiciHraci, pointer);
                    } else {
                        // Všichni si vybrali.
                        hra.getKomunikator().posliStavovuZpravu("");
                    }
                    
                    
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
            });
    }
    
}
