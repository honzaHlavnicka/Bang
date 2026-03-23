/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Třída určená k udržování komunikace s klienty po síti. Jde se k ní dostat pomocí {@link Hra#getKomunikator()}.
 * Události někdy posílá přímo engine, ale u některých akcí to nedělá. U metod, kde by šlo očekávat jinačí chováání je to napsáno v dokumentaci.
 * 
 * <p><b>NEočekává se implementace od autora pluginu</b>
 * 
 * @author honza
 */
public interface KomunikatorHry {

    /**
     * Pošle zprávu všem hráčům ve hře.
     * Většinou by se nemělo používat a místo toho používat metody, které si zprávu sestaví samy.
     * @param co Zpráva, která se pošle všem hráčům, ve formátu protokolu, viz <a href="https://github.com/honzaHlavnicka/Bang/tree/master/docs/protocol" >jeho dokumentace</a>
     * @see #posliVsem(java.lang.String, cz.honza.bang.sdk.Hrac) 
     */
    public void posliVsem(String co);
    
    
    
    /**
     * Pošle zprávu všem hráčům ve hře, kromě jednoho. Hodí se pro poslání podrobné informace jednomu hráči a méně podrobné informaci ostatním.
     * Většinou by se nemělo používat a místo toho používat metody, které si zprávu sestaví samy.
     * @param co Zpráva, která se pošle všem hráčům, kromě jednoho, ve formátu protokolu, viz <a href="https://github.com/honzaHlavnicka/Bang/tree/master/docs/protocol" >jeho dokumentace</a>
     * @param komuNe Hráč, který zprávu neobdrží
     * @see #posliVsem(java.lang.String) 
     */
    public void posliVsem(String co,Hrac komuNe);
    
    /**
     * Pošle zprávu konrétně jednomu hráči. Většinou by se nemělo používat a místo toho používat metody, které si zprávu sestaví samy.
     * @param komu kdo zprávu obdrží
     * @param co Zpráva co se mu pošle, ve formátu protokolu, viz <a href="https://github.com/honzaHlavnicka/Bang/tree/master/docs/protocol" >jeho dokumentace</a>
     */
    public void posli(Hrac komu, String co);

    // ===== METODY AKCÍ =====
    /**
     * Pošle všem hráčům informaci o změně postavy hráče. 
     * @param hrac Hráč, jehož postava se změnila
     */
    public void posliZmenuPostavy(Hrac hrac);

    
    /**
     * Pošle klientovi chybovou zprávu.
     * @param komu komu se má chyba doručit.
     * @param chyba chyba, která se posílá.
     */
    public void posliChybu(Hrac komu,Chyba chyba);
    
    /**
     * Pošle stavovou zprávu všem hráčům. Zpráva se zobrazí v centru obrazovky.
     * @param zprava Text zprávy, která se bude zobrazovat (např. "Hráč vybírá barvu...")
     */
    public void posliStavovuZpravu(String zprava);
    
    
    /**
     * Pošle všem hráčům informaci o změně počtu karet v ruce.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuPoctuKaret(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o změně počtu životů.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuPoctuZivotu(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o zahájení tahu.
     * @param hrac Hráč, jehož tah začal
     */
    public void posliZahajeniTahu(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o změně jména hráče.
     * @param hrac Hráč, kterého se změna týká
     */
    public void posliZmenuJmena(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o připojení nového hráče.
     * @param hrac Nově připojený hráč
     */
    public void posliNovehoHrace(Hrac hrac);
    
    
    /**
     * Pošle všem hráčům informaci o zahájení hry.
     * Plugin nemusí volat, server si to vyřeší sám.
     */
    public void posliZahajeniHry();
    
    /**
     * Pošle všem hráčům informaci o skončení hráče v hře.
     * 
     * @param hrac Hráč, který skončil
     * @see #posliVitezstvi(cz.honza.bang.sdk.Hrac) 
     */
    public void posliSkonceniHrace(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o vítězství hráče.
     * Klient ukáže zavíratelný dialogg s gratulací. Hodí se, pokud vám stačí takovéhle jednoduché ukázání, které 
     * neukončí hru. Pro pořádnou tabulku výsledků a ukončení hry použijte metody {@link #posliVysledky(cz.honza.bang.sdk.Hrac[][])} a {@link #posliKonecHry()}
     * 
     * @param hrac Hráč, který vyhrál
     * @see #posliVysledky(cz.honza.bang.sdk.Hrac[][]) 
     * @see #posliKonecHry() 
     */
    public void posliVitezstvi(Hrac hrac);
    
    
    /**
     * Pošle všem hráčům informaci o odehrání karty.
     * @param hrac Hráč, který kartu odehral
     * @param karta Odehraná karta
     */
    public void posliOdebraniKarty(Hrac hrac, Karta karta);
    
    /**
     * Pošle všem hráčům informaci o spálení karty.
     * @param hrac Hráč, jehož karta byla spálena
     * @param karta Spálená karta
     * @see #posliSpaleniVylozenéKarty(cz.honza.bang.sdk.Karta, cz.honza.bang.sdk.Hrac) 
     */
    public void posliSpaleniKarty(Hrac hrac, Karta karta);
    
    /**
     * Pošle všem hráčům informaci o líznutí nebo získání karty. Hráčovi, který kartu dostal se pošlou o
     * kartě všechny informace a ostatním hráčům se jenom pošle změna počtu karet.
     *
     * @param hrac Hráč, jehož karta byla spálena
     * @param karta Spálená karta
     * @see #posliZmenuPoctuKaret(cz.honza.bang.sdk.Hrac) 
     */
    public void posliNovouKartu(Hrac hrac,Karta karta);
    
    /**
     * Pošle všem hráčům informaci o spálení vyložené karty.
     * @param karta Spálená karta
     * @param odkud Hráč, od kterého byla karta spálena
     */
    public void posliSpaleniVylozenéKarty(Karta karta, Hrac odkud);
    
    /**
     * Pošle všem hráčům informaci o vyložení karty.
     * @param hrac Hráč, který kartu vyložil
     * @param predKoho Hráč, kterému se karta vykladá 
     * @param karta Vyložená karta
     */
    public void posliVylozeniKarty(Hrac hrac, Hrac predKoho, Karta karta);
    
    /**
     * Pošle všem hráčům rychlé oznámení. Toto oznámení vyjede do prostředka obrazovky a postupně zmizí.
     * Pomáhá dělat hru akční.
     * Používá se v pluginech (např. Prší - změna barvy, Uno - změna barvy, Bang - Vedle).
     * @param oznameni Obsah oznámení (např. název barvy)
     * @param vyjimka Hráč, který zprávu neobdrží (obvykle ten, co ji vyvolal), může být null
     */
    public void posliRychleOznameniVsem(String oznameni, Hrac vyjimka);
    
    /**
     * Pošle hráčovi rychlé oznámení. Toto oznámení vyjede do prostředka
     * obrazovky a postupně zmizí. Pomáhá dělat hru akční. Používá se v
     * pluginech (např. Prší - změna barvy, Uno - změna barvy, Bang - Vedle).
     *
     * @param oznameni Obsah oznámení (např. název barvy)
     * @param komu Komu se zobrazí
     */
    public void posliRychleOznameni(String oznameni, Hrac komu);
    
    /**
     * Všem hráčům pošle zprávu, že hra skončila. Zobrazí jim tabulku  vítězů,
     * která by se měla poslat přes {@link #posliVysledky(cz.honza.bang.sdk.Hrac[][])}
     * ještě před voláním této metody.
     * 
     * @see #posliVysledky(cz.honza.bang.sdk.Hrac[][]) 
     * @see #posliVitezstvi(cz.honza.bang.sdk.Hrac) 
     */
    public void posliKonecHry();

    /**
     * Pošle všem hráčům výsledkovou tabulku. Není to konec hry, tabulka se hráčům
     * neukáže, dokud se nazavolá {@link #posliKonecHry()}.
     * <p>
     * Tabulka podporuje více hráčů na stejném místě. Pole které je v parametru reprezentuje
     * pořadí. V tomto pořadí jsou další pole, ve kterých jsou spolu hráči na stejném místě.
     * Pokud má být na každém místě jenom jeden hráč, tak ve vnitřním poli bude vždy sám.
     * 
     * @param vysledky pole, jehož každá položka je jedno umístění (index 0 = místo 1...) a
     * v jednom umístění se může naházet více hráčů spolu.
     * @see #posliKonecHry() 
     * @see #posliVitezstvi(cz.honza.bang.sdk.Hrac) 
     */
    public void posliVysledky(Hrac[][] vysledky);
    
    /**
     * Pošle jednomu hráči otázku a možnosti odpovědí. Vrátí odpověď hráče jako CompletableFuture, které se splní, až hráč odpoví.
     * @param odKoho Hráč, kterému se otázka klade
     * @param moznosti Seznam možných odpovědí (Pořadí je důležité, protože odpověď bude indexem do tohoto seznamu)
     * @param nadpis Nadpis otázky
     * @param closable Zda lze dialog zavřít bez odpovědi (výchozí: false - nelze zavřít)
     * @return CompletableFuture, které se splní, až hráč odpoví (Ve formátu indexu do seznamu možností)
     */
    public CompletableFuture<String> pozadejOVyberMoznosti(Hrac odKoho, List<String> moznosti, String nadpis, boolean closable);


    /**
     * Pošle jednomu hráči otázku a možnosti odpovědí, které jsou reprezentovány kartami. Vrátí odpověď hráče jako CompletableFuture, které se splní, až hráč odpoví.
     * @param odKoho Hráč, kterému se otázka klade
     * @param karty Seznam karet, které reprezentují možnosti odpovědí (Pořadí není důležité, protože odpověď bude id vybrané karty)
     * @param nadpis Nadpis otázky
     * @param min Minimální počet karet, které musí hráč vybrat (obvykle 1)
     * @param max Maximální počet karet, které může hráč vybrat (obvykle 1, ale může být i více pro výběr více karet)
     * @param closable Zda lze dialog zavřít bez odpovědi (výchozí: false - nelze zavřít)
     * @return CompletableFuture, které se splní, až hráč odpoví (Ve formátu "id1,id2,..." s id vybraných karet)
     */
    public CompletableFuture<String> pozadejOKarty(Hrac odKoho, List<Karta> karty, String nadpis, int min, int max, boolean closable);


    /**
     * Pošle jednomu hráči otázku a možnosti odpovědí, které jsou reprezentovány jinými hráči. Vrátí odpověď hráče jako CompletableFuture, které se splní, až hráč odpoví.
     * @param odKoho Hráč, kterému se otázka klade
     * @param hraci Seznam hráčů, které reprezentují možnosti odpovědí (Pořadí není důležité, protože odpověď bude id vybraného hráče)
     * @param nadpis Nadpis otázky
     * @param min Minimální počet hráčů, které musí hráč vybrat (obvykle 1)
     * @param max Maximální počet hráčů, které může hráč vybrat (obvykle 1, ale může být i více pro výběr více hráčů)
     * @param closable Zda lze dialog zavřít bez odpovědi (výchozí: false - nelze zavřít)
     * @return CompletableFuture, které se splní, až hráč odpoví (Ve formátu "id1,id2,..." s id vybraných hráčů)
     */
    public CompletableFuture<String> pozadejOHrace(Hrac odKoho, List<Hrac> hraci,String nadpis,int min, int max, boolean closable);


    /**
     * Pošle příkaz klientovi v parametru {@code komu}. V té nahradí řetězec "data-id" reálným řetězcem,
     * který  jde využít na interní dohledání otázky zpět. Vrátí odpověď hráče jako CompletableFuture,
     * které se splní, až hráč odpoví. Většinou by se NEMĚLO používat přímo, ale spíše přes pozadejOVyberMoznosti() nebo pozadejOKarty() a pod.
     * @param komu Hráč, kterému se otázka klade
     * @return CompletableFuture, které se splní, až hráč odpoví
     * 
     * @see #pozadejOVyberMoznosti(cz.honza.bang.sdk.Hrac, java.util.List, java.lang.String, boolean) pro výběr z možností
     * @see #pozadejOKarty(cz.honza.bang.sdk.Hrac, java.util.List, java.lang.String, int, int, boolean) pro výběr karet
     * @see #pozadejOText(cz.honza.bang.sdk.Hrac, java.lang.String, java.lang.String, java.lang.String, boolean) 
     */
    public CompletableFuture<String> pozadejOdpoved(String otazka,Hrac komu);

    /**
     * Pošle jednomu hráči otázku s textovým vstupem. Vrátí odpověď hráče jako CompletableFuture, které se splní, až hráč odpoví.
     * @param odKoho Hráč, kterému se otázka klade
     * @param nadpis Nadpis otázky
     * @param placeholder Placeholder text v textovém poli (volitelné)
     * @param buttonText Text na tlačítku (volitelné)
     * @param closable Zda lze dialog zavřít bez odpovědi (výchozí: true - lze zavřít)
     * @return CompletableFuture, které se splní, až hráč odpoví (s textem, který hráč zadal)
     */
    public CompletableFuture<String> pozadejOText(Hrac odKoho, String nadpis, String placeholder, String buttonText, boolean closable);

    /**
     * Přidá nebo upraví vlastní tlačítko pro hráče.
     * @param komu Hráč, kterému se prvek zobrazí
     * @param buttonId Jedinečný identifikátor tlačítka (Opakovaným voláním se stejným id se mění ostatní parametry), pomocí 0 se vytvoří tlačítko nové
     * @param text Text na tlačítku
     * @param disabled Zda je tlačítko deaktivované
     * @param akce Akce, která se spustí při kliknutí na tlačítko.
     * @return ID tlačítka
     * 
     * @see #smazatUI(cz.honza.bang.sdk.Hrac, int) 
     * @see HerniPravidla#uiButtonClicked(cz.honza.bang.sdk.Hrac, int)
     */
    public int pridejUIButton(Hrac komu, int buttonId, String text, boolean disabled, Runnable akce);

    /**
     * Odstraní vlastní UI prvek.
     * @param komu Hráč, od kterého se prvek odebere
     * @param uiId ID prvku k odstranění
     * @see #pridejUIButton(cz.honza.bang.sdk.Hrac, int, java.lang.String, boolean, java.lang.Runnable) 
     */
    public void smazatUI(Hrac komu, int uiId);
    
    /**
     * Pošle všechna aktivní UI tlačítka hráči. Používá se při reconnectu.
     * @param komu Hráč, kterému se tlačítka pošlou
     * @see #smazatUI(cz.honza.bang.sdk.Hrac, int) 
     * @see #pridejUIButton(cz.honza.bang.sdk.Hrac, int, java.lang.String, boolean, java.lang.Runnable) 
     */
    public void posliVsechnaUITlacitka(Hrac komu);
    
    
    /**
     * Pomocná třída pro posílání dat do kola štěstí.
     * 
     * @see KomunikatorHry#posliKoloStesti(int, java.lang.String, java.util.List) 
     */
    public static class MoznostKolaStesti {

        private String name;
        private String barva;
        private int id;
        private int velikost;

        public MoznostKolaStesti(String name, String barva, int id, int velikost) {
            this.name = name;
            this.barva = barva;
            this.id = id;
            this.velikost = velikost;
        }

        public String getName() {return name;}
        public String getBarva() {return barva;}
        public int getId() {return id;}
        public int getVelikost() {return velikost;}
    }
    
    /**
     * Pošle všem klientům kolo štěstí, které vybere možnost s id = vybranaMoznost. 
     * Tato možnost je předem vybraná, je to jenom vizuální prvek pro pocit losování,
     * @param vybranaMoznost id možnosti, která má být vybraná
     * @param nadpis Nadpis dialogiu
     * @param moznosti Výřezy kola
     */
    void posliKoloStesti (int vybranaMoznost, String nadpis, List<MoznostKolaStesti> moznosti);

    /**
     * Vrátí kod hry, který hráči na začátku zadávali
     * @return šestimístné číslo
     */
    public int getIdHry();
    
    /**
     * Počet hráčů ve hře, kteří jsou zrovna živě připojjeni k serveru.
     */
    public int pocetHracu();

    /**
     * Vrátí admina, většinou ten kdo hru založil, ale může být změněn.
     * @see #setAdmin(cz.honza.bang.sdk.Hrac) 
     */
    public Hrac getAdmin();

    /**
     * Nastaví admina.
     * @param admin 
     * @see #getAdmin() 
     */
    public void setAdmin(Hrac admin);

}
