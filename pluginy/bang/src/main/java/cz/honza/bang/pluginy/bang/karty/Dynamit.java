/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.bang.karty;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.KomunikatorHry;
import cz.honza.bang.sdk.VylozitelnaKarta;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author honza
 */
public class Dynamit extends Karta implements VylozitelnaKarta, Efekt{

    public Dynamit(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "dynamit";
    }

    @Override
    public String getJmeno() {
        return "dynamit";
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        return (predKoho.equals(kym));
    }

    @Override
    public Efekt getEfekt() {
        return this;
    }

    @Override
    public void spalitVylozenou() {
    }

    @Override
    public void naZacatekTahu(Hra hra, Hrac hrac) {
        Random r = new Random();
        int cislo = r.nextInt(13);
        System.out.println("Dynamit vygeneroval: " + cislo);

        List<KomunikatorHry.MoznostKolaStesti> moznosti = new ArrayList<>(0);
        for (int i = 0; i < 11; i++) {
            moznosti.add(new KomunikatorHry.MoznostKolaStesti("", "#d6b058", i, 1));
        }
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("Velký", "#fc6f03", 11, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("výbuch", "#fc6f03", 12, 1));
       
        hra.getKomunikator().posliKoloStesti(cislo, "Vybouchne " + hrac.getJmeno() + "?", moznosti);
        hra.otocVrchniKartu();
        
        final Karta toto = this;
        final VylozitelnaKarta totoVylozitlne = this;
        final Efekt totoKarta = this;
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(cislo == 11 || cislo == 12){
                    hrac.odeberZivot();
                    hrac.odeberZivot();
                    hrac.odeberZivot();
                    hrac.odeberVylozenouKartu(totoVylozitlne);
                    hra.getOdhazovaciBalicek().vratNahoru(toto);
                    hra.getKomunikator().posliOdebraniKarty(hrac, toto);
                }else{
                    hrac.odeberVylozenouKartu(totoVylozitlne);
                    List<Hrac> hraci = hra.getHrajiciHraci();
                    int dalsiIndex = (hraci.indexOf(hrac) + 1) % hraci.size();
                    Hrac dalsiHrac = hraci.get(dalsiIndex);
                    hra.getKomunikator().posliSpaleniVylozenéKarty(toto, hrac);
                    dalsiHrac.pridejVylozenouKartu(totoVylozitlne, hrac);   
                }
            }
        }, 10000);
    }

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public void prirazeni(Hrac komu) {
    }
    
}
