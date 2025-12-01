/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.pravidla.SpravceHernichPravidel;
import cz.honza.bang.sdk.HerniPravidla;

import cz.honza.bang.net.KomunikatorHryImp;
import cz.honza.bang.postavy.Postava;
import cz.honza.bang.pravidla.UIPrvek;
import cz.honza.bang.sdk.Karta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.java_websocket.WebSocket;
import org.json.JSONArray;


/**
 * Třída samotné hry.
 * @author honza
 * 
 */
public class HraImp implements cz.honza.bang.sdk.Hra{
    /**
     * Hráči ve hře. Pořadí určuje pořadí hráčů. Nemělo by se měnit po zahájení hry respektive vytvoření správce tahu.
     */
    private List<HracImp> hraci = new ArrayList<>();
    private boolean zahajena = false;
            //FIX: neco lepsiho nes seznam
    private final Stack<Postava>  balicekPostav;
    private KomunikatorHryImp komunikator;
    private BalicekImp<Karta> balicek = new BalicekImp<Karta>();
    private BalicekImp<Karta> odhazovaciBalicek = new BalicekImp();
    private SpravceTahuImp spravceTahu;
    private HerniPravidla herniPravidla;
    private String obrazekZadniStrany;
    

    public BalicekImp<Karta> getOdhazovaciBalicek() {
        return odhazovaciBalicek;
    }

    public SpravceTahuImp getSpravceTahu() {
        return spravceTahu;
    }
      
    
    private HraImp(KomunikatorHryImp komunikator){
        this.komunikator = komunikator;
        balicekPostav = new Stack<>();
        
    }
    
    /**
     * Vytvoří a vrátí novou instanci hry
     * @param typHry
     * @param komunikator 
     * @param id id hry.
     * @return new Hra();
     */
    public static HraImp vytvor(KomunikatorHryImp komunikator,int typHry){
        HraImp hra = new HraImp(komunikator);
        hra.herniPravidla = SpravceHernichPravidel.vytvorHerniPravidla(typHry, hra);
        hra.herniPravidla.pripravBalicek(hra.balicek);
        hra.herniPravidla.pripravBalicekPostav(hra.balicekPostav);
        return hra;
    }

    /**
     * Vytvoří hráče. Po zavolání této metody by se měla zavolat metoda hracVytvoren()
     * @return nový hráč
     */
    public HracImp novyHrac(){
        //v této metodě se nesmí volat nic, co by způsobovalo, že by se něco posílalo klientovi. Misto toho použij metodu hracVytvoren, která se spouští hned poté.
        HracImp hrac = new HracImp(this);
        hraci.add(hrac);
        return hrac;
    }
    
    /**
     * Připravý hráče poté, co už je spojen se serverm. měla by se volat hned po novyHrac()
     * @param hrac hráč, který by se měl připravit
     */
    public void hracVytvoren(HracImp hrac){
        //metoda co se spouští po vytvoření hráče a zařazení ho do komunikátoru

        
        if(balicekPostav.size() < 2){//pokud už nezbide postava, tak to tam nejakou soupne. nemelo by se to stat kvuli maximalnimu poctu hracu, ten ale nemusí bít dodren.
            balicekPostav.add(Postava.TESTOVACI);
            balicekPostav.add(Postava.TESTOVACI);
        }
        hrac.vyberZPostav(balicekPostav.pop(),balicekPostav.pop());//nechá hráče vybrat ze dvou postav
    }

    /**
     *
     * @return Kopii seznamu všech hráčů
     */
    @Override
    public List<cz.honza.bang.sdk.Hrac> getHraci() {
        List<cz.honza.bang.sdk.Hrac> l = new ArrayList<>(hraci);
        return l;
    }
    
    /**
     * Vrátí hráče podle jeho id.
     * @param id id hráče
     * @return Hrac nebo null
     */
    @Override
    public HracImp getHrac(int id){
        for (cz.honza.bang.sdk.Hrac hrac : hraci) {
            if(hrac.getId() == id){
                return (HracImp) hrac;
            }
        }
        return null;
    }
    
    @Override
    public KomunikatorHryImp getKomunikator() {
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
            herniPravidla.poSpusteniHry();
            
            komunikator.posliVsem("hraZacala");
            spravceTahu = new SpravceTahuImp(hraci);
            spravceTahu.dalsiHracPodleRole(Role.SERIF).zahajitTah();
            System.out.println("zahájen tah v setzahajena");
                        
            
        }
    }
    public BalicekImp<Karta> getBalicek() {
        return balicek;
    }
    
    /**
     * Pošle všechny informace o hře, které má právo hráč vědět.
     * @param conn
     * @param hrac 
     */
    public void nactiHru(WebSocket conn, HracImp hrac){
        conn.send("noveIdHrace:" + hrac.getId());

        
        for (Karta karta : hrac.getKarty()) {
            conn.send(karta.toJSONold());
        }
        
        
        StringBuilder sb = new StringBuilder("hraci:[");
        boolean jePrvni = true;
        for (HracImp hrac1 : hraci) {
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
        
        JSONArray povoleneUIJSON = new JSONArray();
        UIPrvek[] povoleneUI = herniPravidla.getViditelnePrvky();
        for (int i = 0; i < povoleneUI.length; i++) {
            povoleneUIJSON.put(i, povoleneUI[i].name());
        }
        conn.send("povoleneUI:" + povoleneUIJSON.toString());
    }

    /**
     * Vyřadí hráče z herní smičky. Nezávisle na tom jestli vyhrál nebo prohrál, ale už nebude hrát.
     * @param kdo
     */
    public void skoncil(HracImp kdo){
        spravceTahu.vyraditHrace(kdo);
        komunikator.posliVsem("hracSkoncil:" + kdo.getId());
    }
    
    /**
     * Zařídí problematiku výhry, ale nevyřadí hráče z hrací smyčky. 
     * @param kdo
     */
    public void vyhral(HracImp kdo){
        komunikator.posliVsem("vyhral:" + kdo.getId());
        //TODO: zapsat do tabulky výsledků, vytvořit tabulku výsledků
    }
    
    /**
     * Prohodí odhazovací a lízací balíčky. Novým lízacím balíčkem bude odhazovací balíček v opačném pořadí.
     */
    public void prohodBalicky(){
        odhazovaciBalicek.otoc();
        BalicekImp novyOdhazovaciBalicek = balicek;
        balicek = odhazovaciBalicek;
        odhazovaciBalicek = novyOdhazovaciBalicek;   
    }
    @Deprecated
    public int vzdalenostHracu(HracImp zPohledu, HracImp komu) throws IllegalArgumentException {
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
