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

/**
 *
 * @author honza
 */
public class SidKetchum implements Postava{
    private Hra hra;
    private int idTlacitka;

    public SidKetchum(Hra hra) {
        this.hra = hra;
    }
    
    
    @Override
    public String getJmeno() {
        return "Sid Ketchum";
    }

    @Override
    public String name() {
        return "sidketchum";
    }

    @Override
    public String getPopis() {
        return "Za 2 odhozené karty si může vrátit jeden život.";
    }

    @Override
    public int getMaximumZivotu() {
        return 4;
    }


    @Override
    public void pridaniPostavy(Hrac komu) {
        idTlacitka = hra.getKomunikator().pridejUIButton(komu, 0, "Odhodit dvě karty za život", false, () -> {
            if(!komu.jeNaTahu()){
                return;
            }
            hra.getKomunikator().pozadejOKarty(komu, komu.getKarty(), "Vyber 2 karty, za které dostaneš život", 2, 2, true)
                    .thenAccept(idcka -> {
                        try {
                            String[] poleId = idcka.split(",");
                            if (poleId.length != 2) {
                                hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                                return; 
                            }
                            int idPrvniKarty = Integer.parseInt(poleId[0].trim());
                            int idDruheKarty = Integer.parseInt(poleId[1].trim());
                            
                            Karta karta1 = null;
                            Karta karta2 = null;

                            for (Karta k : komu.getKarty()) {
                                if (k.getId() == idPrvniKarty && karta1 == null) {
                                    karta1 = k;
                                }else if (k.getId() == idDruheKarty && karta2 == null) {
                                    karta2 = k;
                                }
                            }

                            if (karta1 == null || karta2 == null) {
                                hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                                return; 
                            }
                            
                            komu.getKarty().remove(karta1);
                            komu.getKarty().remove(karta2);
                            hra.getOdhazovaciBalicek().vratNahoru(karta1);
                            hra.getOdhazovaciBalicek().vratNahoru(karta2);
                            
                            hra.getKomunikator().posliZmenuPoctuKaret(komu);
                            hra.getKomunikator().posliOdebraniKarty(komu, karta2);
                            hra.getKomunikator().posliOdebraniKarty(komu, karta1);
                            
                            komu.pridejZivot();
                            
                        }catch(NumberFormatException ec){
                            hra.getKomunikator().posliChybu(komu, Chyba.CHYBA_PROTOKOLU);
                        }
                    }).exceptionally((t) -> {
                        t.printStackTrace();
                        return null;
                    });
        });
    }

    @Override
    public void odebraniPostavy(Hrac komu) {
        hra.getKomunikator().smazatUI(komu, idTlacitka);
    }
}
