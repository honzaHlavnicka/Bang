/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.honza.bang.pluginy.bang;

import cz.honza.bang.pluginy.bang.karty.Hledi;
import cz.honza.bang.pluginy.bang.karty.Panika;
import cz.honza.bang.pluginy.bang.karty.Pivo;
import cz.honza.bang.pluginy.bang.karty.CatBalou;
import cz.honza.bang.pluginy.bang.karty.Bang;
import cz.honza.bang.pluginy.bang.karty.Mustang;
import cz.honza.bang.pluginy.bang.karty.WellsFarkgo;
import cz.honza.bang.pluginy.bang.karty.Dostavnik;
import cz.honza.bang.pluginy.bang.karty.BangNaVsechny;
import cz.honza.bang.pluginy.bang.karty.Barel;
import cz.honza.bang.pluginy.bang.karty.Duel;
import cz.honza.bang.pluginy.bang.karty.Hokynarstvi;
import cz.honza.bang.pluginy.bang.karty.Indiani;
import cz.honza.bang.pluginy.bang.karty.Salon;
import cz.honza.bang.pluginy.bang.karty.Vedle;
import cz.honza.bang.pluginy.bang.postavy.JednoduchePostavy;
import cz.honza.bang.pluginy.bang.postavy.PaulRegret;
import cz.honza.bang.pluginy.bang.postavy.RoseDoolan;
import cz.honza.bang.pluginy.bang.zbrane.Remington;
import cz.honza.bang.pluginy.bang.zbrane.RevCarabine;
import cz.honza.bang.pluginy.bang.zbrane.Schofield;
import cz.honza.bang.pluginy.bang.zbrane.Volcanic;
import cz.honza.bang.pluginy.bang.zbrane.Winchester;
import cz.honza.bang.sdk.HerniPravidla;


import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.SpravceTahu;
import cz.honza.bang.sdk.VylozitelnaKarta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jan.hlavnicka.s
 */
public class PravidlaBangu implements HerniPravidla{
    private final Hra hra;

    public PravidlaBangu(Hra hra) {
        this.hra = hra;
    }
    
    @Override
    public void poOdehrani(Hrac kym) {
        return; 
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        hra.getSpravceTahu().vyraditHrace(komu);

        long pocetZivychBanditu = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA && h.jeZivy()).count();
        long pocetZivychOdpadliku = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK && h.jeZivy()).count();
        long celkemZivych = hra.getHraci().stream().filter(Hrac::jeZivy).count();
        boolean zivySerif = hra.getHraci().stream().anyMatch(h -> h.getRole() == Role.SERIF && h.jeZivy());

        Hrac[][] poradi = null;

        if (!zivySerif) {
            // Šerif zemřel
            if (celkemZivych == 1 && pocetZivychOdpadliku == 1) {
                poradi = new Hrac[2][];
                poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
                poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() != Role.ODPADLIK).toArray(Hrac[]::new);
            } else {
                poradi = new Hrac[3][];
                poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA).toArray(Hrac[]::new);
                poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
                poradi[2] = hra.getHraci().stream().filter(h -> h.getRole() == Role.POMOCNIK || h.getRole() == Role.SERIF).toArray(Hrac[]::new);
            }
        } else if (pocetZivychBanditu == 0 && pocetZivychOdpadliku == 0) {
            // Šerif a pomocníci vyhráli
            poradi = new Hrac[3][];
            poradi[0] = hra.getHraci().stream().filter(h -> h.getRole() == Role.SERIF || h.getRole() == Role.POMOCNIK).toArray(Hrac[]::new);
            poradi[1] = hra.getHraci().stream().filter(h -> h.getRole() == Role.ODPADLIK).toArray(Hrac[]::new);
            poradi[2] = hra.getHraci().stream().filter(h -> h.getRole() == Role.BANDITA).toArray(Hrac[]::new);
        }

        if (poradi != null) {
            // OPRAVA: Vyčistíme pole od prázdných rolí (např. když chybí odpadlík nebo pomocník)
            Hrac[][] vycistenePoradi = Arrays.stream(poradi)
                    .filter(skupina -> skupina.length > 0)
                    .toArray(Hrac[][]::new);

            // Pošleme komunikátoru jen ty skupiny hráčů, které opravdu existují
            hra.getKomunikator().posliVysledky(vycistenePoradi);
            hra.getKomunikator().posliKonecHry();
        }



            
        List<Karta> karty = komu.getKarty();
        for (Karta karta : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(karta);
            hra.getKomunikator().posli(komu, "odehrat:" + komu.getId() + "," + karta.toJSON());
        }
        karty.clear();

        karty = komu.getVylozeneKarty();
        for (Karta karta : karty) {
            hra.getOdhazovaciBalicek().vratNahoru(karta);
            hra.getKomunikator().posli(komu,"spalit:"+komu.getId()+ "," + karta.toJSON()); 
       }
       karty.clear();     
        
        //TODO: poslat smrt, poslat karty
        
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        if(kdo.jeNaTahu()){
            kdo.konecTahu();
        }
        return true;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        return false; //Hráč si při bangu nesmí lízat kdy se mu zachce.
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        for (int i = 0; i < 10; i++) {        
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new Bang(hra, balicek));
            balicek.vratNahoru(new BangNaVsechny(hra, balicek));
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Barel(hra, balicek));
            balicek.vratNahoru(new Dostavnik(hra, balicek));
            balicek.vratNahoru(new WellsFarkgo(hra, balicek));
            balicek.vratNahoru(new Pivo(hra, balicek));
            balicek.vratNahoru(new CatBalou(hra, balicek));
            balicek.vratNahoru(new Schofield(hra, balicek));
            balicek.vratNahoru(new Volcanic(hra, balicek));
            balicek.vratNahoru(new Remington(hra, balicek));
            balicek.vratNahoru(new RevCarabine(hra, balicek));
            balicek.vratNahoru(new Winchester(hra, balicek));
            balicek.vratNahoru(new Hledi(hra, balicek));
            balicek.vratNahoru(new Mustang(hra, balicek));
            balicek.vratNahoru(new Panika(hra,balicek));
            balicek.vratNahoru(new Indiani(hra, balicek));
            balicek.vratNahoru(new Salon(hra,balicek));
            balicek.vratNahoru(new Duel(hra, balicek));
            balicek.vratNahoru(new Vedle(hra, balicek));
            balicek.vratNahoru(new Hokynarstvi(hra, balicek));
        }
        balicek.zamichej();
    }

    @Override
    public void zacalTah(Hrac komu) {
        komu.lizni();
        komu.lizni();
    }

    @Override
    public void skoncilTah(Hrac komu) {
        //zatím nic
    }

    @Override
    public boolean muzeSpalit(Karta co) {
        return true;
    }
    
    @Override
    public void poSpusteniHry() {
        List<Role> role = new ArrayList<>(Role.poleRoliBangu(hra.getHraci().size()));
        Collections.shuffle(role);
        
        for (int i = 0; i < role.size(); i++) {
            hra.getHraci().get(i).priraditRoliNaZacatkuHry(role.get(i));
        }
        
    }

    @Override
    public void pripravitHrace(Hrac hrac) {
        if (hrac.getRole() != Role.SERIF) {
            hrac.setMaximumZivotu(hrac.getPostava().getMaximumZivotu());
        } else {
            hrac.setMaximumZivotu(hrac.getPostava().getMaximumZivotu() + 1);
        }
        
        for (int i = 0; i < hrac.getMaximumZivotu(); i++) {
            hrac.lizni();
        }
        
        hrac.setZivoty(hrac.getMaximumZivotu());
    }

    @Override
    public boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co) {
        Karta kontrolovanaKarta = (Karta) co;
        return kdo.getVylozeneKarty().stream().noneMatch(karta -> karta.getJmeno().equals(kontrolovanaKarta.getJmeno()));
    }
    
    @Override
    public void pripravBalicekPostav(java.util.Stack<cz.honza.bang.sdk.Postava> balicekPostav){
        for (int i = 0; i < 10; i++) {
            balicekPostav.add(new RoseDoolan());
            balicekPostav.add(new PaulRegret());
            balicekPostav.add(JednoduchePostavy.WILLY_THE_KID);
        }
        
        Collections.shuffle(balicekPostav);
    }
    
    @Override
    public cz.honza.bang.sdk.UIPrvek[] getViditelnePrvky() {
        return cz.honza.bang.sdk.UIPrvek.values();
    }
    
    @Override
    public boolean muzeZahrat(Karta co, Hrac kdo) {
        return true; // V bangu lze hrát jakoukoli kartu, pokud jsou splněny specifické podmínky v samotné kartě
    }
    
    @Override
    public String getVychoziZadniObrazek() {
        return "bang";
    }
    
    
    
    public int vzdalenost(Hrac od, Hrac k){
        return 0;
    }

    @Override
    public void spustitPrvniTah(SpravceTahu spravceTahu) {
        spravceTahu.dalsiHracPodleRole(Role.SERIF);
    }
    
    

    
}
