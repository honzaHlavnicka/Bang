/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.pravidla.SpravceHernichPravidel;
import cz.honza.bang.sdk.HerniPravidla;

import cz.honza.bang.net.KomunikatorHryImp;
import cz.honza.bang.sdk.UIPrvek;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.SpravceTahu;
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
    private final Stack<cz.honza.bang.sdk.Postava>  balicekPostav;
    private KomunikatorHryImp komunikator;
    private BalicekImp<Karta> balicek = new BalicekImp<Karta>();
    private BalicekImp<Karta> odhazovaciBalicek = new BalicekImp();
    private SpravceTahuImp spravceTahu;
    private HerniPravidla herniPravidla;
    private String obrazekZadniStrany;
    

    @Override
    public BalicekImp<Karta> getOdhazovaciBalicek() {
        return odhazovaciBalicek;
    }
    
    @Override
    public SpravceTahu getSpravceTahu() {
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
    @Override
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
    @Override
    public void hracVytvoren(Hrac hrac){
        //metoda co se spouští po vytvoření hráče a zařazení ho do komunikátoru

        
        if(balicekPostav.size() < 2){//pokud už nezbyde postava, tak to tam nejakou soupne. nemelo by se to stat kvuli maximalnimu poctu hracu, ten ale nemusí být dodren.
            balicekPostav.add(PostavaImp.TESTOVACI);
            balicekPostav.add(PostavaImp.TESTOVACI);
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

    @Override
    public boolean isZahajena() {
        return zahajena;
    }

    @Override
    public HerniPravidla getHerniPravidla() {
        return herniPravidla;
    }

    
    /**
     * Spustí hru.
     * @param zahajena pokud true, tak zahájí hru.
     */
    @Override
    public void setZahajena(boolean zahajena) {
        
        if(!this.zahajena && zahajena){
            this.zahajena = zahajena;
            //zahájení hry:

            herniPravidla.poSpusteniHry();
            
            komunikator.posliZahajeniHry();
            spravceTahu = new SpravceTahuImp(hraci);
            
            for (Hrac hrac : hraci) {
                herniPravidla.pripravitHrace(hrac);
            }
            
            System.out.println("zahájen tah v setzahajena");
            

            
            herniPravidla.spustitPrvniTah(spravceTahu);
            
            for (HracImp hrac : hraci) {
                hrac.poZahajeniHry();
            }
                        
            
        }
    }
    @Override
    public BalicekImp<Karta> getBalicek() {
        return balicek;
    }
    
    /**
     * Pošle všechny informace o hře, které má právo hráč vědět.
     * Volá se při počátečním připojení a při reconnectu.
     * @param conn
     * @param hrac 
     */
    public void nactiHru(WebSocket conn, HracImp hrac){
        // Základní info o hráči
        conn.send("noveIdHrace:" + hrac.getId());

        // Karty v ruce hráče
        for (Karta karta : hrac.getKarty()) {
            conn.send(karta.toJSONold());
        }
        
        // Seznam všech hráčů s jejich základními informacemi
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
        
        // Vyložené karty všech hráčů
        for (HracImp hrac1 : hraci) {
            for (Karta vylozena : hrac1.getVylozeneKarty()) {
                conn.send("vylozeni:" + hrac1.getId() + "," + hrac1.getId() + "," + vylozena.toJSON());
            }
        }
        
        if(zahajena){
          // Role hráče
          conn.send("role:" + hrac.getRole().name());
          
          // Kdo je na tahu
          if (spravceTahu != null && spravceTahu.getNaTahu() != null) {
              conn.send("tahZacal:" + spravceTahu.getNaTahu().getId());
          }
        }
        
        // UI prvky dostupné pro hráče
        JSONArray povoleneUIJSON = new JSONArray();
        UIPrvek[] povoleneUI = herniPravidla.getViditelnePrvky();
        for (int i = 0; i < povoleneUI.length; i++) {
            povoleneUIJSON.put(i, povoleneUI[i].name());
        }
        conn.send("povoleneUI:" + povoleneUIJSON.toString());
        
        // Počty životů všech hráčů (znovu pro jistotu)
        for (HracImp hrac1 : hraci) {
            conn.send("pocetZivotu:" + hrac1.getId() + "," + hrac1.getZivoty());
        }
        
        // Všechna aktivní UI tlačítka pro hráče
        komunikator.posliVsechnaUITlacitka(hrac);

        komunikator.posliZmenuJmena(hrac);
    }

    /**
     * Vyřadí hráče z herní smičky. Nezávisle na tom jestli vyhrál nebo prohrál, ale už nebude hrát.
     * @param kdo
     */
    @Override
    public void skoncil(Hrac kdo){
        spravceTahu.vyraditHrace(kdo);
        komunikator.posliSkonceniHrace(kdo);
    }
    
    /**
     * Nechá hráčům ukázat, že hráč vyhrál, ale nevyřadí hráče z hrací smyčky. 
     * Pokud chce plugin řešit svoje pořadí a pokročilý konec, měl by si držžet tabulku sám a použít Komunikator.posliPoradi() a Komunikator.posliKonec().
     * @param kdo
     */
    @Override
    public void vyhral(Hrac kdo){
        komunikator.posliVitezstvi(kdo);
    }
    
    /**
     * Prohodí odhazovací a lízací balíčky. Novým lízacím balíčkem bude odhazovací balíček v opačném pořadí.
     */
    @Override
    public void prohodBalicky(){
        odhazovaciBalicek.otoc();
        BalicekImp<Karta> novyOdhazovaciBalicek = balicek;
        balicek = odhazovaciBalicek;
        odhazovaciBalicek = novyOdhazovaciBalicek;   
    }


    
    @Override 
    public Karta otocVrchniKartu(){
        Karta karta = balicek.lizni();
        odhazovaciBalicek.vratNahoru(karta);
        // Toto je speciální případ - otočení vrchní karty (není odehrání hráčem)
        komunikator.posliVsem("odehrat:-1" + '|' + karta.toJSON());
        return karta;
    }

}
