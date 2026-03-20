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
import cz.honza.bang.sdk.ZastupnaKarta;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author honza
 */
public class Duel extends Karta implements HratelnaKarta{

    public Duel(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "duel";
    }

    @Override
    public String getJmeno() {
        return "Duel";
    }

    @Override
    public boolean odehrat(Hrac kym) {
        hra.getKomunikator().posliStavovuZpravu(kym.getJmeno() + " právě vybírá hráče do duelu.");
        hra.getKomunikator().pozadejOHrace(kym, hra.getHrajiciHraci().stream().filter(h->!h.equals(kym)).toList(), "Vyber koho chceš vyzvat na duel", 1, 1, true)
                .thenAccept(id->{
                    try{
                        Hrac naKoho = hra.getHrac(Integer.parseInt(id));
                        duelNa(naKoho, kym);
                        
                    }catch(NumberFormatException ex){
                        hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                    }
                    
                });
        return true;
    }
    
    private void duelNa(Hrac naKoho, Hrac odKoho){
        hra.getKomunikator().posliStavovuZpravu(naKoho.getJmeno() + " bud ztratí život v duelu, nebo ho předá na " + odKoho.getJmeno());

        List<Karta> karty = new ArrayList<>(2);
        karty.add(ZastupnaKarta.getZivot());
        naKoho.getKarty().stream().filter(k -> k instanceof Bang).allMatch(k -> karty.add(k));
        hra.getKomunikator().pozadejOKarty(naKoho, karty, "Vyber o co přijdeš v duelu!", 1, 1, false).thenAccept(id -> {
            int idKarty;
            try {
                idKarty = Integer.parseInt(id);
            } catch (NumberFormatException ex) {
                hra.getKomunikator().posliChybu(naKoho, Chyba.CHYBA_PROTOKOLU);
                duelNa(naKoho,odKoho); // Aby podstčením špatné hodnoty se nešlo zbavit duelu
                return;
            }
            if (idKarty == ZastupnaKarta.getZivot().getId()) {
                naKoho.odeberZivot();
                hra.getKomunikator().posliRychleOznameni("Duel vyhrál " + odKoho.getJmeno(),naKoho);
                hra.getKomunikator().posliStavovuZpravu("");
                // Duel tímto skončil
            } else {
                for (Karta karta : naKoho.getKarty()) {
                    if (karta.getId() == idKarty) {
                        naKoho.getKarty().remove(karta);
                        hra.getOdhazovaciBalicek().vratNahoru(karta);
                        hra.getKomunikator().posliSpaleniKarty(naKoho, karta);
                        hra.getKomunikator().posliZmenuPoctuKaret(naKoho);
                        
                        hra.getKomunikator().posliRychleOznameni("Duel pokračuje",naKoho);
                        
                        // Duel pokračuje, útok se obrací
                        duelNa(odKoho,naKoho);
                    }
                }
            }
        });
    }
    
}
