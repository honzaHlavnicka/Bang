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
import cz.honza.bang.net.KomunikatorHry;
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
    
    
    public void pripravKeHre(Role role ) {
        if(role != Role.SERIF){
            this.maximumZivotu = postava.maximumZivotu;
        }else{
            this.maximumZivotu = postava.maximumZivotu + 1;
        }
        
        hra.getKomunikator().posli(this, "role:"+role.name());
        
        this.role = role;
        zivoty = maximumZivotu;
        
        for (int i = 0; i < 4; i++) {
            Karta karta = hra.getBalicek().lizni();
            karty.add(karta);
            hra.getKomunikator().posli(this, karta.toJSONold());
            hra.getKomunikator().posliVsem("zmenaPoctuKaret:"+id+","+karty.size(),this);
        }
        
        
        //FIX: tolik karet kolik ma zivotu
        
        pripravenyKeHre = true;
        
        
        
    }
    public void vyberZPostav(Postava p1, Postava p2){
        postavyNaVyber = new Postava[]{p1,p2};
        
        //vytvoří json ve formátu vyberPostavu[{"jmeno":"jmeno","popis":"popis"},..]
        StringBuilder sb = new StringBuilder("vyberPostavu:[{\"jmeno\":\"");
        sb.append(p1.jmeno);
        sb.append("\",\"popis\":\"");
        sb.append(p1.popis);
        sb.append("\",\"zivoty\":\"");
        sb.append(p1.maximumZivotu);
        sb.append("\"},{\"jmeno\":\"");
        sb.append(p2.jmeno);
        sb.append("\",\"popis\":\"");
        sb.append(p2.popis);
        sb.append("\",\"zivoty\":\"");
        sb.append(p2.maximumZivotu);
        sb.append("\"}]");

        hra.getKomunikator().posli(this,sb.toString());
    }
    
    public int odeberZivot(){
        if(zivoty <= 0){
            //TODO: prohra
        }else{
            zivoty--;
            //TODO: poslat upozornení přes socket
        }
        return zivoty;
        
    }
    
    public int pridejZivot(){
        if(zivoty >= maximumZivotu){
            
        }else{
            zivoty ++;
            //TODO: poslat upozornení přes socket
        }
        return zivoty;
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
    
    public void setPostava(String jmeno){
        if(pripravenyKeHre){
            hra.getKomunikator().posli(this, "error{\"error\":\"postava už je nastavená\"}");
        }
        for (Postava postava : postavyNaVyber) {
            if(postava.jmeno.equals(jmeno)){
                this.postava = postava;
                return;
            }
        }
        hra.getKomunikator().posli(this, "error{\"error\":\"postava není na výběr\"}");
    }

    public void tah() {
        System.out.println("zahájen tah v tah");
        hra.getKomunikator().posli(this, "tahZacal");
    }
    
    /**
     * Spustit, pokud hráč odehraje kartu. najde kartu, dá jí na odhazovací balíček a provede její efekt.
     * @param id id karty
     */
    public void odehranaKarta(String id) {
        int idKarty = Integer.parseInt(id);
        for (Karta karta  : karty) {
            if(karta.getId() == idKarty){
                hra.getOdhazovaciBalicek().vratNahoru(karta);
                karty.remove(karta);
                if (karta instanceof HratelnaKarta hratelna) {
                    hratelna.odehrat(this);
                    hra.getKomunikator().posliVsem("odehrat:"+id+'|' + karta.toJSON(),this);
                    //FIX: předpokládá, že v karta.toJSON() není znak |, ale co když je?
                } else {
                    hra.getKomunikator().posli(this, "error{\"error\":\"tuto kartu nelze zahrát\"}");
                }
                break;
            }
        }
    }
    
    /**
     * Přidá hráči karu z balíčku hry. Pošle o tom upozornění všem hráčům.
     */
    public void lizni(){
        Karta karta = hra.getBalicek().lizni();
        karty.add(karta);
        System.out.println("lizani si");
        hra.getKomunikator().posli(this,karta.toJSONold());
        hra.getKomunikator().posliVsem("setPocetKaret:" + id +  ',' + karty.size(),this);
    }

    /**
     *Mělo by se zavolat pro ukončení tahu. Provede akce před ukončením tahu a nechá ukončit tah správcem tahu.
     */
    public void konecTahu() {
        if(hra.getSpravceTahu().getNaTahu().equals(this)){
            hra.getSpravceTahu().dalsiHrac().tah();
        }
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