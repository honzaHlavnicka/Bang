/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.karty.BangNaVsechny;
import cz.honza.bang.karty.Eso;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.karty.Pivo;
import cz.honza.bang.karty.UnoKarta;
import cz.honza.bang.karty.WellsFarkgo;
import cz.honza.bang.net.KomunikatorHry;
import cz.honza.bang.postavy.Postava;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.java_websocket.WebSocket;


/**
 *
 * @author honza
 * 
 */
public class Hra {
    private ProstrediHry prostredi;
    private List<Hrac> hraci = new ArrayList<>();
    private boolean zahajena = false;
            //FIX: neco lepsiho nes seznam
    private final Stack<Postava>  balicekPostav;
    private KomunikatorHry komunikator;
    private Balicek<Karta> balicek = new Balicek<Karta>();
    private Balicek<Karta> odhazovaciBalicek = new Balicek();
    private SpravceTahu spravceTahu;
    private HerniPravidla herniPravidla;
    

    public Balicek<Karta> getOdhazovaciBalicek() {
        return odhazovaciBalicek;
    }

    public SpravceTahu getSpravceTahu() {
        return spravceTahu;
    }
    
   

    
    
    
    public Hra(KomunikatorHry komunikator){
        this.komunikator = komunikator;
        this.herniPravidla = new PravidlaUNO(this); //TODO: herní pravidla by se měla vzít odněkud zvbenku.
        //herniPravidla.setHra(this);
        
        //vytvoří a zamíchá balíček postav
        balicekPostav = new Stack<>();
        balicekPostav.addAll(Arrays.asList(Postava.values()));
        Collections.shuffle(balicekPostav);
        
        //vytvoří a zamíchá hrací balíček.
        pripravBalicek();
    }

    /**
     * Vytvoří hráče. Po zavolání této metody by se měla zavolat metoda hracVytvoren()
     * @return nový hráč
     */
    public Hrac novyHrac(){
        //v této metodě se nesmí volat nic, co by způsobovalo, že by se něco posílalo klientovi. Misto toho použij metodu hracVytvoren, která se spouští hned poté.
        Hrac hrac = new Hrac(this);
        hraci.add(hrac);
        return hrac;
    }
    
    /**
     * Připravý hráče poté, co už je spojen se serverm. měla by se volat hned po novyHrac()
     * @param hrac hráč, který by se měl připravit
     */
    public void hracVytvoren(Hrac hrac){
        //metoda co se spouští po vytvoření hráče a zařazení ho do komunikátoru

        
        if(balicekPostav.size() < 2){//pokud už nezbide postava, tak to tam nejakou soupne. nemelo by se to stat kvuli maximalnimu poctu hracu, ten ale nemusí bít dodren.
            balicekPostav.add(Postava.TESTOVACI);
            balicekPostav.add(Postava.TESTOVACI);
        }
        hrac.vyberZPostav(balicekPostav.pop(),balicekPostav.pop());//nechá hráče vybrat ze dvou postav
    }

    public ProstrediHry getProstredi() {
        return prostredi;
    }

    public List<Hrac> getHraci() {
        return hraci;
    }
    
    public KomunikatorHry getKomunikator() {
        return komunikator;
    }

    public boolean isZahajena() {
        return zahajena;
    }

    public HerniPravidla getHerniPravidla() {
        return herniPravidla;
    }

    
    /**
     * Spustí hru.
     * @param zahajena pokud true, tak zahájí hru.
     */
    public void setZahajena(boolean zahajena) {
        
        
        if(!this.zahajena && zahajena){
            this.zahajena = zahajena;
            //zahájení hry:
            Role[] role = Role.poleRoli(hraci.size());
            
            for (int i = 0; i < role.length; i++) {
                hraci.get(i).pripravKeHre(role[i]);                
            }
            
            komunikator.posliVsem("hraZacala");
            spravceTahu = new SpravceTahu(hraci);
            spravceTahu.dalsiHracPodleRole(Role.SERIF).tah();
            System.out.println("zahájen tah v setzahajena");
            
            //TODO: dodelat
            
            
        }
    }

    public Balicek<Karta> getBalicek() {
        return balicek;
    }
    
    /**
     * Pošle všechny informace o hře, které má právo hráč vědět.
     * @param conn
     * @param hrac 
     */
    public void nactiHru(WebSocket conn, Hrac hrac){
        conn.send("noveIdHrace:" + hrac.getId());

        
        for (Karta karta : hrac.getKarty()) {
            conn.send(karta.toJSONold());
        }
        
        
        StringBuilder sb = new StringBuilder("hraci:[");
        boolean jePrvni = true;
        for (Hrac hrac1 : hraci) {

            if(!jePrvni){
                sb.append(',');
            }
            jePrvni = false;
            sb.append(hrac1.toJSON());
        }
        sb.append(']');
        conn.send(sb.toString());
        
        if(zahajena){
          conn.send("role:" + hrac.getRole().name());
        }
    }

    /**
     * Vyřadí hráče z herní smičky. Nezávisle na tom jestli vyhrál nebo prohrál, ale už nebude hrát.
     * @param kdo
     */
    public void skoncil(Hrac kdo){
        //TODO: vyřadit ze správce tahu, informovat klienta/y
    }
    
    /**
     * Zařídí problematiku výhry, ale nevyřadí hráče z hrací smyčky. 
     * @param kdo
     */
    public void vyhral(Hrac kdo){
        komunikator.posliVsem("vyhral:" + kdo.getId());
        //TODO: zapsat do tabulky výsledků, vytvořit tabulku výsledků
    }
    
    /**
     * naplní balíček kartami hry.
     */
    private void pripravBalicek(){
        balicek.vratNahoru(new cz.honza.bang.karty.Bang(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Bang(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Bang(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Dostavnik(this, balicek));
        /*balicek.vratNahoru(new cz.honza.bang.karty.Barel(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Barel(this, balicek));
        balicek.vratNahoru(new cz.honza.bang.karty.Barel(this, balicek));*/
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new WellsFarkgo(this,balicek));
        balicek.vratNahoru(new Pivo(this,balicek));
        balicek.vratNahoru(new Pivo(this,balicek));
        balicek.vratNahoru(new Pivo(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new BangNaVsechny(this,balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        balicek.vratNahoru(new Eso(this, balicek));
        
        
        for (int i = 0; i < 10; i++) {
           balicek.vratNahoru(new UnoKarta(i,"red",this,balicek));
        }
        for (int i = 0; i < 10; i++) {
           balicek.vratNahoru(new UnoKarta(i,"green",this,balicek));
        }
        for (int i = 0; i < 10; i++) {
           balicek.vratNahoru(new UnoKarta(i,"blue",this,balicek));
        }
        for (int i = 0; i < 10; i++) {
           balicek.vratNahoru(new UnoKarta(i,"yellow",this,balicek));
        }
        
        balicek.zamichej();
        
    }
   
}
