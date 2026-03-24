/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.postavy;

import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.Postava;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author honza
 */
public class KitCarlson implements Postava, LizaciPostava{

    @Override
    public String getJmeno() {
        return "Kit Carleson";
    }

    @Override
    public String name() {
        return "kitCarlson" ;
    }

    @Override
    public String getPopis() {
        return "Při lízání si vybere 2 karty z vrchních 3 v balíčku"; 
    }
    @Override
    public int getMaximumZivotu() {
        return 4;
    }

    @Override
    public void lizniNaZacatkuTahu(Hrac komu, Hra hra) {
        List<Karta> vytazene = hra.getBalicek().lizni(3);

        hra.getKomunikator().pozadejOKarty(komu, vytazene, "Jaké dvě si lízneš?", 2, 2, false).thenAccept(ids -> {
            List<Karta> proHrace = new ArrayList<>();
            List<Karta> zpet = new ArrayList<>(vytazene); 

            try {
                if (ids != null && !ids.isBlank()) {
                    // set unikátních id
                    Set<Integer> parsedIds = Arrays.stream(ids.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toSet());

                    List<Karta> vybrane = vytazene.stream()
                            .filter(k -> parsedIds.contains(k.getId()))
                            .collect(Collectors.toList());

                    if (vybrane.size() == 2) {
                        proHrace.addAll(vybrane);
                        zpet.removeAll(vybrane); 
                    } else {
                        hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                    }
                }
            } catch (NumberFormatException e) {
                hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
            }

            //Pokud je proHrace prázdné (chyba, timeout, špatné ID), vnutí mu první 2
            if (proHrace.isEmpty()) {
                proHrace.addAll(vytazene.subList(0, Math.min(2, vytazene.size())));
                zpet.removeAll(proHrace);
            }

            for (Karta karta : proHrace) {
                komu.getKarty().add(karta);
                hra.getKomunikator().posliNovouKartu(komu, karta);
            }
            zpet.forEach(hra.getBalicek()::vratNahoru);
        });
    }

}
