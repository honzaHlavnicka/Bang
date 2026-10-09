/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.bang.karty;

import cz.honzaa.bang.sdk.Balicek;
import cz.honzaa.bang.sdk.Chyba;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.HratelnaKarta;
import cz.honzaa.bang.sdk.Karta;
import cz.honzaa.bang.sdk.ZpravoveUtils;
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
            Karta k = hra.getBalicek().lizni();
            if (k == null && !hra.getOdhazovaciBalicek().jePrazdny()) {
                hra.prohodBalicky();
                k = hra.getBalicek().lizni();
            }
            if (k != null) {
                karty.add(k);
            }
        }
        
        if (karty.isEmpty()) {
            return true;
        }

        int startIndex = hra.getHrajiciHraci().indexOf(kym);
        if (startIndex < 0) {
            startIndex = 0;
        }
        nechatVybrat(karty, hra.getHrajiciHraci(), startIndex);
        System.out.println("index v poli: " + startIndex);
        
        return true;
    }
    
    /**
     * Nechá hráče vybrat mezi seznamem karet.
     * @param karty jaké karty rozdat
     * @param hrajiciHraci mezi koho
     * @param uKohoZacit u jaké položky v seznamu začít
     */
    private void nechatVybrat(List<Karta> karty, List<Hrac> hrajiciHraci, int uKohoZacit){
        if (hrajiciHraci == null || hrajiciHraci.isEmpty() || karty == null || karty.isEmpty()) {
            hra.getKomunikator().posliStavovouZpravu("");
            return;
        }
        final int indexHrace = (uKohoZacit < 0 || uKohoZacit >= hrajiciHraci.size()) ? 0 : uKohoZacit;
        Hrac hrac = hrajiciHraci.get(indexHrace);
        hra.getKomunikator().posliStavovouZpravu(ZpravoveUtils.lokalizuj("bang.status.general_store", "name", hrac.getJmeno()));
        
        hra.getKomunikator().pozadejOKarty(hrac, karty, "$bang.dialog.general_store_choice", 1, 1, false)
                .thenAccept(id->{
                    int idKarty;
                    try{
                        idKarty = Integer.parseInt(id);
                    }catch(NumberFormatException ex){
                        hra.getKomunikator().posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
                        
                        nechatVybrat(karty, hrajiciHraci, indexHrace); //Druhý pokus
                        return;
                    }
                    
                    boolean kartaNalezena = false;
                    for (Karta karta : karty) {
                        if(karta != null && karta.getId() == idKarty){
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
                        if (vnucenaKarta != null) {
                            hrac.getKarty().add(vnucenaKarta);
                            hra.getKomunikator().posliNovouKartu(hrac, vnucenaKarta);
                            hra.getKomunikator().posliZmenuPoctuKaret(hrac);
                        }
                    }
                    
                    
                    int pointer = indexHrace; // Přejmenování, protože nejde upravovat proměná v then blocku
                    if (!karty.isEmpty()) {
                        if (pointer >= hrajiciHraci.size() - 1) {
                            pointer = 0;
                        } else {
                            pointer++;
                        }
                        nechatVybrat(karty, hrajiciHraci, pointer);
                    } else {
                        // Všichni si vybrali.
                        hra.getKomunikator().posliStavovouZpravu("");
                    }
                    
                    
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
            });
    }
    
}
