/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;



/**
 * Třída reprezentující jednoho hráče.
 * @author honza
 */
public interface Hrac {
     
    /**
     * Připraví se ke hře, dobere si karty. Mělo by se spustit pře začátkem hry.
     * @param role
     */
    public void pripravKeHre(Role role);
    
    /**
     * Nechá hráče vybrat z postav.
     * @param p1 postava na výběr
     * @param p2 postava na výběr
     */
    public void vyberZPostav(Postava p1, Postava p2);
     
    /**
     * Odebere hráči život a upozorní na to ostatní. Obsahuje kontrolu smrti i
     * přebytku životů.
     *
     * @return odebral se život úspěšně
     */
    public boolean odeberZivot();

    /**
     * Přidá hráči život a upozorní na to ostatní. Obsahuje kontrolu smrti i
     * přebytku životů.
     *
     * @return přidal se život úspěšně.
     */
    public boolean pridejZivot();
    
    public int getZivoty();
    
    public void setZivoty(int zivoty);

    public int getMaximumZivotu();

    public Role getRole();

    public Postava getPostava();

    public List<Karta> getKarty();

    public int getId();

    public String getJmeno();

    public void setJmeno(String jmeno);
    
    public boolean jeNaTahu();
    
    public void setMaximumZivotu(int maximumZivotu);

    public void setPostava(Postava postava);

    public void setRole(Role role);

    public boolean isPripravenyKeHre();

    public Hra getHra();
    
    /**
     * Nastaví hráčovu postavu, neinformuje o tom nikoho.
     * @param jmeno name() postavy.
     */
    public void setPostava(String jmeno);

    
    /**
     * Spustit, pokud hráč odehraje kartu. najde kartu, dá jí na odhazovací balíček a provede její efekt.
     * @param id id karty
     */
    public void odehranaKarta(String id);
    
    /**
     * Pokud je hráč na tahu, tak spálí kartu, tzn. přesune ji na vyhazovací baliček, ale neprovede její efekt.
     * Dává možnost kartě na spálení reagovat.
     * @param id karty
     */
    public void spalitKartu(String id);
    
    public void vylozitKartu(String id, String idHrace);
    
    /**
     * Přidá hráči karu z balíčku hry. Pošle o tom upozornění všem hráčům, nekontroluje zda hráč může lízat.
     */
    public void lizni();
    
    /**
     * Lízne si kartu, ale pouze pokud hráč má právo na to si líznout.
     * Mělo by se volat pokud hráč zažádá o líznutí.
     */
    public void lizniKontrolovane();
    
    public void pridejEfekt(Efekt efekt);
    
    /**
     * Vrátí fyickou vzdálenost k dalšímu hráči. Nebere v potaz žádné efekty. Funguje
     * zpětně. Pokud hráči sedí vedle sebe, tak je vzdálenst 1.
     * @param komu vůči komu se vzdálenost počítá
     * @return vzdálenost k požadovnému hráči
     * @throws IllegalArgumentException
     * @see #vzdalenostPod(int)
     * @see #vzdalenostKCista(cz.honza.bang.Hrac) 
     */
    public int fyzickaVzdalenostK(Hrac komu)throws IllegalArgumentException;
    
    public int vzdalenostKCista(Hrac komu);
    
    /**
     * Vrací List hráčů, jejichš vzdálenost je větší než <code>max</code>.
     * Mezi tyto hráče se nepočítá <code>this</code>.
     * @param max jaká maximální vzdálenost má být akceptovaná (včetně)
     * @param iZpetne má se prohledávat vzdálenost i nazpět, nebo pouze po směru hry.
     * @return List hráčů, kteří spn
     * @see #fyzickaVzdalenostK(cz.honza.bang.Hrac) 
     */
    public List<Hrac> vzdalenostPod(int max, boolean iZpetne);
    
    /**
     * Vrací List hráčů, jejichš vzdálenost je větší než <code>max</code>. Mezi
     * tyto hráče se nepočítá <code>this</code>.
     *
     * @param max jaká maximální vzdálenost má být akceptovaná (včetně)
     * @return List hráčů, kteří spn
     * @see #fyzickaVzdalenostK(cz.honza.bang.Hrac)
     */
    public List<Hrac> vzdalenostPod(int max);

    

    /**
     * Provede akce před koncem tahu a ukončí tah. Upozorní na to všechny.
     * Měl by volat pouze správce tahu, nebo pokud se ví, že je tento hráč vážně na tahu.
     */
    public void konecTahu();

    public List<Karta> getVylozeneKarty() ;

    public List<Efekt> getEfekty();
    
    
    /**
     * vrátí všechny veřejné informace o hráči ve formátu JSON
     * @return json ve formátu: {"jmeno":jmeno,"zivoty",pocetZivotu,"pocetKaret":pocetKaret,"postava":postava.name(),"maximumZivotu",maximumZivotu}
     */
    public String toJSON();

    /**
     * Upozorní hráče na změnu zahájení tahu. Nemělo by se volat jinde, než ve
     * společnosti správce tahů, jinak by mohli být klienti zmatení.
     */
    public void zahajitTah();
}