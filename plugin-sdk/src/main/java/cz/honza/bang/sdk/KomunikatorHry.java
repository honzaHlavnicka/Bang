/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 *
 * @author honza
 */
public interface KomunikatorHry {

    /**
     * Pošle zprávu všekm hráčům ve hře.
     * @param co Zpráva, která se pošle všem hráčům
     */
    public void posliVsem(String co);
    
    
    
    /**
     * Pošle zprávu všem hráčům ve hře, kromě jednoho. Hodí se pro poslání podrobné informace jednomu hráči a méně podrobné informaci ostatním.
     * @param co Zpráva, která se pošle všem hráčům, kromě jednoho
     * @param komuNe Hráč, který zprávu neobdrží
     */
    public void posliVsem(String co,Hrac komuNe);
    
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
     */
    public void posliZahajeniHry();
    
    /**
     * Pošle všem hráčům informaci o skončení hráče v hře.
     * @param hrac Hráč, který skončil
     */
    public void posliSkonceniHrace(Hrac hrac);
    
    /**
     * Pošle všem hráčům informaci o vítězství hráče.
     * @param hrac Hráč, který vyhrál
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
     */
    public void posliSpaleniKarty(Hrac hrac, Karta karta);
    
    /**
     * Pošle všem hráčům informaci o líznutí nebo získání karty.
     *
     * @param hrac Hráč, jehož karta byla spálena
     * @param karta Spálená karta
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
     * Pošle hráčům rychlé oznámení (plugin-specifická zpráva).
     * Používá se v pluginech (např. Prší - zmena barvy, Uno - zmena barvy).
     * @param oznameni Obsah oznámení (např. název barvy)
     * @param vyjimka Hráč, který zprávu neobdrží (obvykle ten, co ji vyvolal)
     */
    public void posliRychleOznameni(String oznameni, Hrac vyjimka);

    /**
     * Všem hráčům pošle zprávu, že hra skončila.
     */
    public void posliKonecHry();

    /**
     * Pošle všem hráčům výsledkovou tabulku. Není to konec hry.
     * @param vysledky pole, jehož každá položka je jedno umístění (index 0 = místo 1...) a v jednom umístění se může naházet více hráčů spolu.
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
     * Pošle příkaz klientovy v parametru @param <komu>. V té nahradí řetězec "data-id" reálným řetězcem,
     * který  jde využít na interní dohledání otázky zpět. Vrátí odpověď hráče jako CompletableFuture,
     * které se splní, až hráč odpoví. Většinou by se NNEMĚLO používat přímo, ale spíše přes pozadejOVyberMoznosti() nebo pozadejOKarty() a pod.
     * @param komu Hráč, kterému se otázka klade
     * @return CompletableFuture, které se splní, až hráč odpoví
     * 
     * @see pozadejOVyberMoznosti() pro výběr z možností
     * @see pozadejOKarty() pro výběr karet
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
     * @param buttonId Jedinečný identifikátor tlačítka (opakovaným voláním s stejným id se mění parametry)
     * @param text Text na tlačítku
     * @param disabled Zda je tlačítko deaktivované
     * @return ID tlačítka
     */
    public int pridejUIButton(Hrac komu, int buttonId, String text, boolean disabled);

    /**
     * Odstraní vlastní UI prvek.
     * @param komu Hráč, od kterého se prvek odebere
     * @param uiId ID prvku k odstranění
     */
    public void smazatUI(Hrac komu, int uiId);
    
    /**
     * Pošle všechna aktivní UI tlačítka hráči. Používá se při reconnectu.
     * @param komu Hráč, kterému se tlačítka pošlou
     */
    public void posliVsechnaUITlacitka(Hrac komu);
    
    
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

        public String getName() {
            return name;
        }

        public String getBarva() {
            return barva;
        }

        public int getId() {
            return id;
        }

        public int getVelikost() {
            return velikost;
        }
    }
    
    /**
     * Pošle všem klientům kolo štěstí, které vybere možnost s id = vybranaMoznost. 
     * @param vybranaMoznost
     * @param nadpis
     * @param moznosti 
     */
    void posliKoloStesti (int vybranaMoznost, String nadpis, List<MoznostKolaStesti> moznosti);

    public int getIdHry();
    public int pocetHracu();

    public Hrac getAdmin();

    public void setAdmin(Hrac admin);
    
    
    
}
