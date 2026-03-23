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
import java.util.List;





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
        boolean maVolcanic = kym.getEfekty().stream()
                .filter(e -> e instanceof Zbran)
                .anyMatch(e -> ((Zbran) e).umoznujeBangBezLimitu());

        if (!maVolcanic && kym.getPostava() != JednoduchePostavy.WILLY_THE_KID) {

            int pocetKeSpaleni = kym.getKarty().size() - kym.getMaximumZivotu();

            // Pokud nemáme pravidlo o omezeném počtu, nebo hráč nepřekročil limit, rovnou posouváme tah.
            if (!((PravidlaBangu) hra.getHerniPravidla()).jeOmezenyPocetKaret() || pocetKeSpaleni <= 0) {
                hra.getSpravceTahu().dalsiHracSUpozornenim();
            } else {
                hra.getKomunikator().pozadejOKarty(kym, kym.getKarty(), "Na konec tahu musíš spálit karty.", pocetKeSpaleni, kym.getKarty().size(), false)
                        .thenAccept((String ids) -> {

                            if (ids == null) {
                                ids = "";
                            }
                            String[] idArray = ids.trim().isEmpty() ? new String[0] : ids.split(",");

                            int uspesneSpaleno = 0;
                            boolean nalezenaChyba = false;

                            List<String> platnaIds = new java.util.ArrayList<>();
                            for (var karta : kym.getKarty()) {
                                platnaIds.add(String.valueOf(karta.getId()));
                            }

                            for (String id : idArray) {
                                String cisteId = id.trim();
                                if (platnaIds.contains(cisteId)) {
                                    kym.spalitKartu(cisteId);
                                    platnaIds.remove(cisteId);
                                    uspesneSpaleno++;
                                } else {
                                    nalezenaChyba = true;
                                }
                            }

                            if (nalezenaChyba || uspesneSpaleno < pocetKeSpaleni) {
                                hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);
                                System.out.println("Chyba protože, uspesneSpaleno:" + uspesneSpaleno + ", pocetKeSpaleni:" + pocetKeSpaleni + ", nalezenaChyba:" + nalezenaChyba);
                            }

                            // pokud hráč zmanipuloval dialog nebo poslal nesmysly
                            while (uspesneSpaleno < pocetKeSpaleni && !kym.getKarty().isEmpty()) {
                                String idKeSpaleni = String.valueOf(kym.getKarty().get(0).getId());
                                kym.spalitKartu(idKeSpaleni);
                                uspesneSpaleno++;
                            }

                            hra.getSpravceTahu().dalsiHracSUpozornenim();

                        }).exceptionally(t -> {
                    hra.getKomunikator().posliChybu(kym, Chyba.CHYBA_PROTOKOLU);

                    hra.getSpravceTahu().dalsiHracSUpozornenim();
                    return null;
                });
            }
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
