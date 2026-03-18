/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;

/**
 *
 * @author honza
 */
import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.UIPrvek;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import java.util.ArrayList;
import java.util.List;



public class PravidlaPrsi implements HerniPravidla{
    private final Hra hra;
    private int pocetKaretNaLiznuti;
    private List<Hrac> poradiVyher = new ArrayList<>();  // Pořadí končících hráčů

    public PravidlaPrsi(Hra hra) {
        this.hra = hra;
        pocetKaretNaLiznuti = 0;
    }
    

    @Override
    public void poOdehrani(Hrac kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        if(kym.getKarty().isEmpty()){
            // Hráč skončil - přidej ho do pořadí
            poradiVyher.add(kym);
            
            int pocetZbyvajicichHracu = hra.getHraci().size() - poradiVyher.size();
            
            // Pokud zbyl jen jeden hráč, tak hra skončila
            if(pocetZbyvajicichHracu <= 1){
                ukoncitHru();
            }
        }
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        //Nezájem, nic jako životy prší nemá
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        return false; //Hráč nemůže jen tak říct, že přeskakuje tah
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        System.out.println("někdo chce lízat. pocetKaretNaLiznuti="+pocetKaretNaLiznuti);
        if(kdo.jeNaTahu()){
            if(pocetKaretNaLiznuti != 0){
                for (int i = 0; i < pocetKaretNaLiznuti; i++) {
                    kdo.lizni();
                }
                pocetKaretNaLiznuti = 0;
            }else{
                kdo.lizni();
            }
            kdo.konecTahu();
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void pripravBalicek(Balicek<Karta> balicek){     
        for(PrsiBarva barva : PrsiBarva.values()){
            balicek.vratNahoru(new PrsiSedmicka(hra, balicek, barva,this));            
            balicek.vratNahoru(new PrsiEso(hra, balicek, barva, PrsiHodnota.ESO));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.OSMA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.DEVITKA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.DESITKA));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.KRAL));
            balicek.vratNahoru(new PrsiKarta(hra, balicek, barva, PrsiHodnota.SPODEK));
            balicek.vratNahoru(new PrsiSvrsek(hra, balicek, barva, PrsiHodnota.SVRSEK));
        }
        balicek.zamichej();
        
    }

    @Override
    public UIPrvek[] getViditelnePrvky() {
        return new UIPrvek[] {
            UIPrvek.ODHAZOVACI_BALICEK,
            UIPrvek.DOBIRACI_BALICEK,
        };
    }
    
    public void zahranaSedmicka(boolean cervena){
        if(cervena){
            pocetKaretNaLiznuti += 4;
        }else{
            pocetKaretNaLiznuti += 2;
        }
    }

    @Override
    public void poSpusteniHry() {/*
        Karta vrchni = hra.getBalicek().lizni();
        hra.getOdhazovaciBalicek().vratNahoru(vrchni);
        ((HratelnaKarta) vrchni).odehrat(hra.getSpravceTahu().getNaTahu());
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        hra.getKomunikator().posliVsem("odehrat:-1|" + vrchni.toJSON());*/

        PrsiKarta karta = (PrsiKarta) hra.otocVrchniKartu();
        while (karta.getHodnota() == PrsiHodnota.SVRSEK || karta == null) {
            karta = (PrsiKarta) hra.otocVrchniKartu();
        }
    }

    private enum StavSedmicky{
        ZADNY, ZAHRANA
    }

    @Override
    public boolean muzeZahrat(Karta co, Hrac kdo) {
        
        //Pokud se má lízat na sedmičku, tak se nemůže hrát nic jiného.
        if(pocetKaretNaLiznuti <= 0){
            return true;
        }else if(co instanceof PrsiSedmicka){
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void pripravitHrace(Hrac hrac){
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
        hrac.lizni();
    }
    
    /**
     * Ukončí hru a pošle výsledky hráčům.
     * Vytvoří 2D pole kde každá řada je jedno umístění a obsahuje hráče na tom místě.  //TODO: zkontrolovat
     */
    private void ukoncitHru(){
        List<Hrac> zbyvajici = new ArrayList<>();
        for(Hrac hrac : hra.getHraci()){
            if(!poradiVyher.contains(hrac)){
                zbyvajici.add(hrac);
            }
        }
        
        Hrac[][] vysledky = new Hrac[poradiVyher.size() + zbyvajici.size()][];
        
        for(int i = 0; i < poradiVyher.size(); i++){
            vysledky[i] = new Hrac[]{poradiVyher.get(i)};
        }
        
        if(zbyvajici.size() > 0){
            vysledky[poradiVyher.size()] = new Hrac[zbyvajici.size()];
            for(int i = 0; i < zbyvajici.size(); i++){
                vysledky[poradiVyher.size()][i] = zbyvajici.get(i);
            }
        }
        
        hra.getKomunikator().posliVysledky(vysledky);
        hra.getKomunikator().posliKonecHry();
    }
    
}

