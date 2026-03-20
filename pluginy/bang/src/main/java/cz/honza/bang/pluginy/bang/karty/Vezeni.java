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

/**
 *
 * @author honza
 */
public class Vezeni extends Karta implements VylozitelnaKarta, Efekt{

    public Vezeni(Hra hra, Balicek<Karta> balicek) {
        super(hra, balicek);
    }

    @Override
    public String getObrazek() {
        return "vezeni";
    }

    @Override
    public String getJmeno() {
        return "Vězení";
    }

    @Override
    public boolean vylozit(Hrac predKoho, Hrac kym) {
        return (!predKoho.equals(kym));
    }

    @Override
    public Efekt getEfekt() {
        return this;
        
    }

    @Override
    public void spalitVylozenou() {
        //nesmí se
    }

    @Override
    public void naZacatekTahu(Hra hra, Hrac hrac) {
        Random r = new Random();
        int cislo = r.nextInt(4);  // generuje v čísla {0,1,2, ... , n-1}, pokud n = 4, pak: {0,1,2,3}
        System.out.println("vězení vygenerovalo: " + cislo);

        List<KomunikatorHry.MoznostKolaStesti> moznosti = new ArrayList<>(0);
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("", "#d6b058", 1, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("Vězení", "##fc6f03", 2, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("Vězení", "##fc6f03", 3, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("Vězení", "##fc6f03", 0, 1));
        hra.getKomunikator().posliKoloStesti(cislo, "Zůstane " + hrac.getJmeno() + " ve vězení?", moznosti);
        hra.otocVrchniKartu();
        
        if(cislo == 1){
            hrac.odeberVylozenouKartu(this);
        }else{
            hra.getSpravceTahu().dalsiHracSUpozornenim();
            hrac.odeberVylozenouKartu(this);
        }
    }

    @Override
    public void odebrani(Hrac odKoho) {
        // nic
    }

    @Override
    public void prirazeni(Hrac komu) {
        // nic
    }
    
}
