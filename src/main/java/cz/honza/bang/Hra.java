/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.pravidla.SpravceHernichPravidel;
import cz.honza.bang.pravidla.HerniPravidla;
import cz.honza.bang.karty.Karta;
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
    /**
     * Hráči ve hře. Pořadí určuje pořadí hráčů. Nemělo by se měnit po zahájení hry respektive vytvoření správce tahu.
     */
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
    
   
   
    
    private Hra(KomunikatorHry komunikator){
        this.komunikator = komunikator;
        
        //vytvoří a zamíchá balíček postav //TODO:nemělo by být v pravidlech?
        balicekPostav = new Stack<>();
        balicekPostav.addAll(Arrays.asList(Postava.values()));
        Collections.shuffle(balicekPostav);
        
    }
    
    /**
     * Vytvoří a vrátí novou instanci hry
     * @param typHry
     * @param komunikator 
     * @param id id hry.
     * @return new Hra();
     */
    public static Hra vytvor(KomunikatorHry komunikator,int typHry){
        Hra hra = new Hra(komunikator);
        hra.herniPravidla = SpravceHernichPravidel.vytvorHerniPravidla(typHry, hra);
        hra.herniPravidla.pripravBalicek(hra.balicek);
        return hra;
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

    public List<Hrac> getHraci() {
        return hraci;
    }
    
    /**
     * Vrátí hráče podle jeho id.
     * @param id id hráče
     * @return Hrac nebo null
     */
    public Hrac getHrac(int id){
        for (Hrac hrac : hraci) {
            if(hrac.getId() == id){
                return hrac;
            }
        }
        return null;
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
            
            List<Role> role =  new ArrayList<>(Role.poleRoliBangu(hraci.size()));
            Collections.shuffle(role);
            
            //todo: přesunout do pravidel
            
            for (int i = 0; i < role.size(); i++) {
                hraci.get(i).pripravKeHre(role.get(i));
            }   
            
            komunikator.posliVsem("hraZacala");
            spravceTahu = new SpravceTahu(hraci);
            spravceTahu.dalsiHracPodleRole(Role.SERIF).zahajitTah();
            System.out.println("zahájen tah v setzahajena");
                        
            
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
        spravceTahu.vyraditHrace(kdo);
        komunikator.posliVsem("hracSkoncil:" + kdo.getId());
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
     * Prohodí odhazovací a lízací balíčky. Novým lízacím balíčkem bude odhazovací balíček v opačném pořadí.
     */
    public void prohodBalicky(){
        odhazovaciBalicek.otoc();
        Balicek novyOdhazovaciBalicek = balicek;
        balicek = odhazovaciBalicek;
        odhazovaciBalicek = novyOdhazovaciBalicek;   
    }
    @Deprecated
    public int vzdalenostHracu(Hrac zPohledu, Hrac komu) throws IllegalArgumentException {
        //TODO: pouze hrací hráči, neměl by to dělat správce tahu? asi ne. měl by to dělat hráč a ST by měl vracet pole hrajících hráčů v pořadí.
        int velikost = hraci.size();
        int i1 = hraci.indexOf(zPohledu);
        int i2 = hraci.indexOf(komu);

        if (i1 == -1 || i2 == -1) {
            throw new IllegalArgumentException("Hráč nebyl nalezen v seznamu");
        }

        int rozdil = Math.abs(i1 - i2);
        int zpetnaVzdalenost = velikost - rozdil;
        
        int rozdilPodleMist = Math.min(rozdil, zpetnaVzdalenost);
        return rozdilPodleMist;
    }
    
   /* public List<Hrac> hraciNaDosahZbrane(Hrac zPohledu){
        List<Hrac> hraciNaDosah = new ArrayList<>();
        int i1 = hraci.indexOf(zPohledu);

        for (Hrac hrac : hraci) {
            if(hrac){
                
            }
        }
    }*/
    
    public Karta sejmiKartu(){
        Karta sejmuta = balicek.lizni();
        odhazovaciBalicek.vratNahoru(sejmuta);
        //TODO: informovat hráče.
        return sejmuta;
    }
}
