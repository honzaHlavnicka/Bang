/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;



/**
 * Třída reprezentující jednoho hráče. Pro plugin obsahuje spoustu užitečných metod
 * 
 * <p><b>NEočekává se implementace od autora pluginu</b>, ale měl by používat její dostupné metody.
 * @author honza
 */
public interface Hrac {
     
    /**
     * Mělo by se spustit před začátkem hry. Přiřadí hráči roli a připravý ho ke hře.
     * @param role
     */
    public void priraditRoliNaZacatkuHry(Role role);
    
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
    
    /**
     * Vrátí počet životů
     */
    public int getZivoty();
    
    /**
     * Ntvrdo nastaví počet životů. Pošle oznámení hráčům, ale nekontroluje žádné podmínky (smrt nebo přebytek)
     * @param zivoty
     */
    public void setZivoty(int zivoty);

    /**
     * Kolik nejvíce může hráč získat životů
     * @see #setMaximumZivotu(int) 
     */
    public int getMaximumZivotu();

    public Role getRole();

    public Postava getPostava();
    
    /**
     * Vrátí upravitelný seznam karet
     */
    public List<Karta> getKarty();
    
    /**
     * Jedinečné id hráče
     * @return Jedinečné id hráče
     */
    public int getId();
    
    /**
     * Jméno, které si hráč zvolil
     * @return 
     */
    public String getJmeno();
    
    /**
     * Přenastaví jméno a pošle ho hráčům
     * @param jmeno 
     */
    public void setJmeno(String jmeno);
    
    public boolean jeNaTahu();
    
    public void setMaximumZivotu(int maximumZivotu);
    
    /**
     * Nastaví novou postavu a pošle ji hráčům. Zavolá na postavě přiřazení a na staré postavě odebrání.
     * @param postava novvá postava
     * @return stará postava
     */
    public Postava setPostava(Postava postava);
    
    /**
     * Nastaví roly, řekne to hráči.
     * @param role 
     */
    public void setRole(Role role);

    public Hra getHra();
    
    /**
     * Jeho počet životů je >0
     * @return je živý
     */
    public boolean jeZivy();
    
    /**
     * Nastaví hráčovu postavu podle jejího jména z postav na výběr uvnitř hráče.
     * 
     * @param jmeno name() postavy.
     * @see #setPostava(cz.honza.bang.sdk.Postava) 
     */
    public void setPostava(String jmeno);

    
    /**
     * Spustit, pokud hráč odehraje kartu. Najde kartu, dá jí na odhazovací balíček a provede její efekt.
     * Mělo by se volat, pokud je to akce, kterou hráč hctěl udělat a ještě jde zvrátit.
     * @param id id karty
     */
    public void odehranaKarta(String id);
    
    /**
     * Pokud je hráč na tahu, tak spálí kartu, tzn. přesune ji na vyhazovací baliček, ale neprovede její efekt.
     * Dává možnost kartě na spálení reagovat.
     * @param id karty
     */
    public void spalitKartu(String id);
    
    /**
     * Vyloží kartu před hráče. Kontroluje všechny podmínky od pravidel a podobně.
     * @param id id karty
     * @param idHrace před koho ji položí
     */
    public void vylozitKartu(String id, String idHrace);
    
    /**
     * Přidá hráči karu z balíčku hry. Pošle o tom upozornění všem hráčům,
     * nekontroluje zda hráč může lízat.
     */
    public void lizni();
    
    /**
     * Lízne si kartu, ale pouze pokud hráč má právo na to si líznout.
     * Mělo by se volat pokud hráč zažádá o líznutí.
     */
    public void lizniKontrolovane();
    
    /**
     * Přidá efekt. Zařídí i jeho inicializaci.
     * @param efekt 
     */
    public void pridejEfekt(Efekt efekt);
    
    /**
     * Vrátí fyickou vzdálenost k dalšímu hráči. Nebere v potaz žádné efekty. Funguje
     * zpětně. Pokud hráči sedí vedle sebe, tak je vzdálenst 1.
     * @param komu vůči komu se vzdálenost počítá
     * @return vzdálenost k požadovnému hráči
     * @throws IllegalArgumentException
     * @see #vzdalenostPod(int)
     * @see #vzdalenostK(cz.honza.bang.Hrac) 
     */
    public int fyzickaVzdalenostK(Hrac komu)throws IllegalArgumentException;
    
    /**
     * Spočítá vzdálenost z pohledu tohoto hráče
     * k {@code komu}. Bere v potaz Efekty.
     * @param komu
     * @return vzdálenost
     * @see #vzdalenostPod(int) 
     * @see #vzdalenostPod(int, boolean) 
     */
    public int vzdalenostK(Hrac komu);
    
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

    /**
     * Vrátí originál seznamu vyložených karet
     * @return 
     */
    public List<Karta> getVylozeneKarty() ;
    
    /**
     * Vrátí originál seznamu efektů.
     * @return 
     */
    public List<Efekt> getEfekty();
    
    
    /**
     * Vrátí všechny veřejné informace o hráči ve formátu JSON
     * @return json ve formátu: {"jmeno":jmeno,"zivoty",pocetZivotu,"pocetKaret":pocetKaret,"postava":postava.name(),"maximumZivotu",maximumZivotu}
     */
    public String toJSON();

    /**
     * Upozorní hráče na změnu zahájení tahu. Nemělo by se volat jinde, než ve
     * společnosti správce tahů, jinak by mohli být klienti zmatení.
     */
    public void zahajitTah();
    
    /**
     * Součet všech efektů a jejich getBonusOdstupu
     * @return kolik se bude přřičítat
     * @see Efekt#getBonusOdstupu()
     */
    int getCelkovyBonusOdstupu();
    
    /**
     * Součet všech efektů a jejich getBonusOdstupu
     *
     * @return kolik se bude přřičítat
     * @see Efekt#getBonusOdstupu()
     */
    int getCelkovyBonusDosahu();
    
    
    /**
     * Odebere bez kontrol s upozorněním kartu, která je mezi vyloženými. Obstará i efekty.
     * @param karta, která se má odebrat
     */
    public void odeberVylozenouKartu(VylozitelnaKarta karta);

    /**
     * Přidá hráči kartu mezi vyložené, vezme si její efekt a provvede k tomu všechny procedury. Upozorní o tom hráče.
     * @param karta na přidání
     * @param kym pro klienta
     */
    public void pridejVylozenouKartu(VylozitelnaKarta karta, Hrac kym);
}