package cz.honza.bang.pluginy.bang;

import cz.honza.bang.sdk.Efekt;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.KomunikatorHry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author honza
 */
public class BarelEfekt implements Efekt {

    @Override
    public void odebrani(Hrac odKoho) {
    }

    @Override
    public void prirazeni(Hrac komu) {
    }

    
    /**
     * Aktivuje barel a vrátí jeho výsledek.
     * @param hra 
     * @param hrac, kterého barelk chrání
     * @return má ho barel zachránit?
     */
    public boolean aktivovat(Hra hra, Hrac hrac){
        Random r = new Random();
        int cislo = r.nextInt(4);  // generuje v čísla {0,1,2, ... , n-1}, pokud n = 4, pak: {0,1,2,3}
        System.out.println("barel vygeneroval: " + cislo);

        List<KomunikatorHry.MoznostKolaStesti> moznosti = new ArrayList<>(0);
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("Zachráněn", "#58d680", 1, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("", "#d6b058", 2, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("", "#d6b058", 3, 1));
        moznosti.add(new KomunikatorHry.MoznostKolaStesti("", "#d6b058", 0, 1));
        hra.getKomunikator().posliKoloStesti(cislo, "Bude " + hrac.getJmeno() + " zachráněn barelem?", moznosti);
        hra.otocVrchniKartu();
        
        return cislo == 1;
    }
    
    
    
    
    
}
