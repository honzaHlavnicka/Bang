/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import cz.honza.bang.karty.Efekt;
import cz.honza.bang.karty.Karta;
import cz.honza.bang.postavy.Postava;
import cz.honza.bang.karty.HratelnaKarta;
import cz.honza.bang.karty.VylozitelnaKarta;
import cz.honza.bang.net.Chyba;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author honza
 */
public class Hrac {
    private int zivoty;
    private int  maximumZivotu;
    private Postava postava = Postava.TESTOVACI;
    //private KartyVRuce karty;
    private List<Karta> karty = new ArrayList<>();
    private List<Karta> vylozeneKarty = new ArrayList<>();
    private List<Efekt> efekty = new ArrayList<>();
    private Role role;
    private boolean pripravenyKeHre;
    private String jmeno;
    private Postava[] postavyNaVyber;
    private Hra hra;
    private final int id;
    private static int nextId = 0;
    

    
    public Hrac(Hra hra){
        this.hra = hra;
        pripravenyKeHre = false;
        jmeno = "nepojmenovaný hráč";
        id = nextId;
        nextId++;
    }
    
    /**
     * Připraví se ke hře, dobere si karty. Mělo by se spustit pře začátkem hry.
     * @param role
     */
    public void pripravKeHre(Role role ) {
        if(role != Role.SERIF){
            this.maximumZivotu = postava.maximumZivotu;
        }else{
            this.maximumZivotu = postava.maximumZivotu + 1;
        }
        
        hra.getKomunikator().posli(this, "role:"+role.name());
        
        this.role = role;
        zivoty = maximumZivotu;
        
        for (int i = 0; i < maximumZivotu; i++) {
            Karta karta = hra.getBalicek().lizni();
            karty.add(karta);
            hra.getKomunikator().posli(this, karta.toJSONold());
            hra.getKomunikator().posliVsem("zmenaPoctuKaret:"+id+","+karty.size(),this);
        }
                
        pripravenyKeHre = true;
        
        
        
    }
    
    /**
     * Nechá hráče vybrat z postav.
     * @param p1 postava na výběr
     * @param p2 postava na výběr
     */
    public void vyberZPostav(Postava p1, Postava p2){
        postavyNaVyber = new Postava[]{p1,p2};
        
        //vytvoří json ve formátu vyberPostavu[{"jmeno":"jmeno","popis":"popis"},..]
        StringBuilder sb = new StringBuilder("vyberPostavu:[{\"jmeno\":\"");
        sb.append(p1.jmeno);
        sb.append("\",\"obrazek\":\"");
        sb.append(p1.name());
        sb.append("\",\"popis\":\"");
        sb.append(p1.popis);
        sb.append("\",\"zivoty\":\"");
        sb.append(p1.maximumZivotu);
        sb.append("\"},{\"jmeno\":\"");
        sb.append(p2.jmeno);
        sb.append("\",\"obrazek\":\"");
        sb.append(p2.name());
        sb.append("\",\"popis\":\"");
        sb.append(p2.popis);
        sb.append("\",\"zivoty\":\"");
        sb.append(p2.maximumZivotu);
        sb.append("\"}]");

        hra.getKomunikator().posli(this,sb.toString());
    }
     
    /**
     * Odebere hráči život a upozorní na to ostatní. Obsahuje kontrolu smrti i
     * přebytku životů.
     *
     * @return odebral se život úspěšně
     */
    public boolean odeberZivot() {
        if (zivoty < 0) {//TODO: kdyz ma moc zivotu, nez kolik muze mit
            hra.getHerniPravidla().dosliZivoty(this);
            return false;
        } else {
            zivoty--;
            hra.getKomunikator().posliVsem("pocetZivotu:" + id + "," + zivoty);
            return true;
        }

    }

    /**
     * Přidá hráči život a upozorní na to ostatní. Obsahuje kontrolu smrti i
     * přebytku životů.
     *
     * @return přidal se život úspěšně.
     */
    public boolean pridejZivot() {//TODO: kdyz ma moc zivotu, nez kolik muze mit
        if (zivoty >= maximumZivotu) {
            return false; 
        } else {
            zivoty++;
            hra.getKomunikator().posliVsem("pocetZivotu:" + id + "," + zivoty);
            return true;
        }
        
    }
    
    
    public int getZivoty() {
        return zivoty;
    }

    public int getMaximumZivotu() {
        return maximumZivotu;
    }

    public Role getRole() {
        return role;
    }

    public Postava getPostava() {
        return postava;
    }    

    public List<Karta> getKarty() {
        return karty;
    }

    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }
    
    public boolean jeNaTahu(){
        return hra.getSpravceTahu().getNaTahu().equals(this);
    }
    /**
     * Nastaví hráčovu postavu, neinformuje o tom nikoho.
     * @param jmeno name() postavy.
     */
    public void setPostava(String jmeno){
        for (Postava postava : postavyNaVyber) {
            if(postava.name().equals(jmeno)){
                this.postava = postava;
                return;
            }
        }
        hra.getKomunikator().posiChybu(this, Chyba.POSTAVA_NENI_NA_VYBER);
    }

    /**
     *  Upozorní hráče na změnu zahájení tahu. Nemělo by se volat jinde, než ve společnosti správce tahů, jinak by mohli být klienti zmatení.
     */
    public void zahajitTah() {
        System.out.println("zahájen tah v tah");
        hra.getKomunikator().posli(this, "tvujTahZacal");
        hra.getKomunikator().posliVsem("tahZacal:"+id,this);
        hra.getHerniPravidla().zacalTah(this);
    }
    
    /**
     * Spustit, pokud hráč odehraje kartu. najde kartu, dá jí na odhazovací balíček a provede její efekt.
     * @param id id karty
     */
    public void odehranaKarta(String id) {
        int idKarty = Integer.parseInt(id);
        if(!hra.getSpravceTahu().getNaTahu().equals(this)){
            hra.getKomunikator().posiChybu(this, Chyba.NEJSI_NA_TAHU);
            return;
        }
        for (Karta karta  : karty) {
            if(karta.getId() == idKarty){
                if (karta instanceof HratelnaKarta hratelna) {
                    if(hratelna.odehrat(this)){ //provede efekt karty, karta zkontroluje jestli je hratelna v tomto kontextu.
 
                        hra.getOdhazovaciBalicek().vratNahoru(karta);
                        karty.remove(karta);
                        
                        hra.getKomunikator().posliVsem("odehrat:" + this.id + '|' + karta.toJSON());
                        hra.getKomunikator().posliVsem("novyPocetKaret:" + this.id + "," + karty.size(), this);
                        //FIX: předpokládá, že v karta.toJSON() není znak |, ale co když je?
                        hra.getHerniPravidla().poOdehrani(this);
                        return;
                    }else{
                        hra.getKomunikator().posiChybu(this, Chyba.KARTA_NEJDE_ZAHRAT);
                        return;
                    }

                } else {
                    hra.getKomunikator().posiChybu(this, Chyba.KARTA_NENI_HRATELNA);
                    return;
                }
                
            }
        }
        hra.getKomunikator().posiChybu(this, Chyba.KARTA_NEEXISTUJE);
    }
    
    /**
     * Pokud je hráč na tahu, tak spálí kartu, tzn. přesune ji na vyhazovací baliček, ale neprovede její efekt.
     * Dává možnost kartě na spálení reagovat.
     * @param id karty
     */
    public void spalitKartu(String id){
        int idKarty = Integer.parseInt(id);
        if (!hra.getSpravceTahu().getNaTahu().equals(this)) {
            hra.getKomunikator().posiChybu(this, Chyba.NEJSI_NA_TAHU);
            return;
        }
        for (Karta karta : karty) {
            if(karta.getId() == idKarty){
                if (hra.getHerniPravidla().muzeSpalit(karta)){
                    karty.remove(karta);
                    hra.getOdhazovaciBalicek().vratNahoru(karta);
                    //TODO: informovat hráče
                }else{
                    hra.getKomunikator().posiChybu(this, Chyba.KARTA_NEJDE_SPALIT);
                }
                return;
            }
        }
        
        for (Karta karta : vylozeneKarty) { //TODO: DRY
            if (karta.getId() == idKarty) {
                if (hra.getHerniPravidla().muzeSpalit(karta)) {
                    ((VylozitelnaKarta) karta).spalitVylozenou();
                    vylozeneKarty.remove(karta);
                    hra.getOdhazovaciBalicek().vratNahoru(karta);

                    hra.getKomunikator().posliVsem("spalenaVylozena:" + karta.getId() + "," + this.id);
                } else {
                    hra.getKomunikator().posiChybu(this, Chyba.KARTA_NEJDE_SPALIT);
                }
                return;
            }
        }
        hra.getKomunikator().posiChybu(this, Chyba.KARTA_NEEXISTUJE);
    }
    
    public void vylozitKartu(String id, String idHrace){ //TODO: asi by mohlo brát objekty a prevodni metoda by mela byt jina
        int idKarty = Integer.parseInt(id);
        int idPredKoho = Integer.parseInt(idHrace);      //TODO: try/chatch -> Chyba
        if (!hra.getSpravceTahu().getNaTahu().equals(this)) {
            hra.getKomunikator().posiChybu(this, Chyba.NEJSI_NA_TAHU);
            return;
        }
        for (Karta karta : karty) {
            if (karta.getId() == idKarty) {
                if(karta instanceof VylozitelnaKarta vylozena){
                     if(vylozena.vylozit(this, hra.getHrac(idPredKoho))){
                         efekty.add(vylozena.getEfekt());
                     }else{
                         hra.getKomunikator().posiChybu(this, Chyba.KARTU_NEJDE_VYLOZIT);
                     }
                }else{
                    hra.getKomunikator().posiChybu(this, Chyba.NENI_VYLOZITELNA);
                    return;
                }
                return;
            }
        }
    }
    
    /**
     * Přidá hráči karu z balíčku hry. Pošle o tom upozornění všem hráčům, nekontroluje zda hráč může lízat.
     */
    public void lizni(){
        Karta karta = hra.getBalicek().lizni();
        if(karta == null){
            hra.prohodBalicky();
            karta = hra.getBalicek().lizni();
            //TODO: co kdyz jsou oba balicky prazdyn
        }
        karty.add(karta);
        System.out.println("lizani si");
        hra.getKomunikator().posli(this,karta.toJSONold());
        hra.getKomunikator().posliVsem("setPocetKaret:" + id +  ',' + karty.size(),this);//TODO: opravit na klientovi
    }
    
    /**
     * Lízne si kartu, ale pouze pokud hráč má právo na to si lí
     */
    public void lizniKontrolovane(){
        if(hra.getSpravceTahu().getNaTahu().equals(this)){
            lizni();
            //TODO: kontrola od pravidel hry.
            konecTahu();
            
        }else{
            hra.getKomunikator().posiChybu(this, Chyba.NEJSI_NA_TAHU);
        }
    }

    /**
     * Provede akce před koncem tahu a ukončí tah. Upozorní na to všechny.
     * Měl by volat pouze správce tahu, nebo pokud se ví, že je tento hráč vážně na tahu.
     */
    public void konecTahu() {
        hra.getSpravceTahu().dalsiHrac().zahajitTah();
        hra.getHerniPravidla().skoncilTah(this);
    }

    public List<Karta> getVylozeneKarty() {
        return vylozeneKarty;
    }

    public List<Efekt> getEfekty() {
        return efekty;
    }
    
    
    /**
     * vrátí všechny veřejné informace o hráči ve formátu JSON
     * @return json ve formátu: {"jmeno":jmeno,"zivoty",pocetZivotu,"pocetKaret":pocetKaret,"postava":postava.name(),"maximumZivotu",maximumZivotu}
     */
    public String toJSON(){
        StringBuilder sb = new StringBuilder("{\"id\":");
        sb.append(id);
        sb.append(", \"jmeno\":\"");
        sb.append(jmeno);
        sb.append("\",\"zivoty\":");
        sb.append(zivoty);
        sb.append(",\"pocetKaret\":");
        sb.append(karty.size());
        sb.append(",\"postava\":\"");
        sb.append(postava.name());
        sb.append("\",\"maximumZivotu\":");
        sb.append(maximumZivotu);
        sb.append('}');
        return sb.toString();
    }
}