/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cz.honza.bang.sdk;


import java.util.Stack;

/**
 * Základní třída pluginu na ovlivnovani hry. Narozdíl od {@link HerniPlugin herního pluginu} neexistuje jeden na server,
 * ale jeden na jednu konkrétní probíhající hru. Obsahuje základní nastavení a potřebné funkčnosti a události ve hře.
 * 
 * Pokud vytváříte vlastní plugin, tak implementací metod této třídy by jste měli začít.
 * 
 * Aby se pravidla načetli do serveru, je třeba vytvořit {@link HerniPlugin}
 * 
 * Určena pro implementaci od autora pluginu.
 * @author jan.hlavnicka.s
 */
public interface HerniPravidla {    
    
    /**
     * Spustí se po spuštění hry. Může například vyložit kartu..
     * @see #pripravitHrace(cz.honza.bang.Hrac)
     */
    public void poSpusteniHry();
    
    /**
     * Připravý hráče ke hře, například mu rozdá karty. Volá se na po zahájení hry, po volání metody {@link #poSpusteniHry() }, před zahájením prvního tahu.
     * Postupně se zavolá na každého hráče.
     */
    public void pripravitHrace(Hrac hrac);

    /**
     * Volá se poté, co hráč odehraje kartu. Nemůž enic změnit, ale může nějak reagovat.
     * @param kym
     */
    public void poOdehrani(Hrac kym);
    
    /**
     * Volá se, když má někdo 0 životů. Hodí se například k vyřazení hráče ze hry, nebo ukončení hry.
     * @param komu
     */
    public void dosliZivoty(Hrac komu);

    /**
     * Volá se, pokud hráč CHCE ukončit tah.
     * Pokud hráč ukončit tah může, tak to MUSÍ zařídit tato metoda, ideálně pomocí {@link SpravceTahu#dalsiHracSUpozornenim() }
     * Ten kdo ji zavolal nemusí být zrovna na tahu.
     * Nemělo by se volat v kartách a podobně
     * @param kdo chce ukončit tah
     * @return
     */
    public boolean hracChceUkoncitTah(Hrac kdo);

    /**
     * Volat pokud hráč klikne na lízací balíček nebo řekne, že chce lízat.
     * Nemělo by se volat v kartách a podobně.
     * 
     * Líznutí musí provést tato metoda. (hrac.lini())
     * 
     * @param kdo
     * @return true - bylo mu líznuto.
     * @return false - nebylo mu líznuto
     */
    public boolean hracChceLiznout(Hrac kdo);
    
    /**
     * Nplní balíček kartami, popřípadě zamíchá.
     * 
     * Pro plugin velmi důležitá metoda, která na začátku hry dá do balíčku správný počet všech karet. Každá karta musí být vlastní instancí,
     * V balíčku nesmí být žádná karta dvakrát. Musí se na každou kartu vyttvořit vlastní objekt. Karty by měl autor pluginu vytvořit implementováním
     * {@link Karta}, {@link HratelnaKarta} a {@link VylozitelnaKarta}
     * @param balicek balíček, do kterého se vše vloží pomocí .vratitNahoru()
     */
    public void pripravBalicek(Balicek<Karta> balicek);
    /**
     * Naplní balíček postavami, které se mohou rozdávat. Objekt postavy nemusí být unikátní, pokud postava v sobě nic nedělá. Pokud například přidává efekt, tak musí být na každou
     * instanci v balíčku jedna instance třídy. Na primitivní postavy stačí použít enum.
     * Mělo by je zamíchat, protože se rozdávají od vrchu.
     * @param balicekPostav Balíček co se má naplnit.
     */
    default public void pripravBalicekPostav(Stack<Postava> balicekPostav){

    };
    /**
     * Volá se když hráč začíná svůj tah.
     * @param komu
     */
    default public void zacalTah(Hrac komu){};
    /**
     * Volá se když hráč končí svůj tah.
     * @param komu
     */
    default public void skoncilTah(Hrac komu){};
    /**
     * Může hráč spálit danou kartu?
     * Nemělo by to kartu spalovat, to řeší engine.
     * @param co
     */
    default public boolean muzeSpalit(Karta co){return false;}
    /**
     * Mělo by vrátit Array UIPrvky, které by měly být viditelné pro hráče.
     * @return viditelné prvky
     */
    default public UIPrvek[] getViditelnePrvky()  {
        return UIPrvek.values();
    };
    
    /**
     * Volá se před zahráním karty, mělo by vrátit zda se karta může zahrát.
     * Nemělo by nahrazovat logiku v kartě, ale může se hodit na nějaká všeobecná omezení.
     * @param co 
     * @param kdo
     * @return může zahrát
     */
    default public boolean muzeZahrat(Karta co,Hrac kdo){
        return true;
    }
    
    /**
     * Vrátí vzhled karty zezadu. To může každá karta přepsat.
     * Poze označení bez přípony souboru a absolutní cesty.
     * @return 
     */
    default public String getVychoziZadniObrazek(){
        return "zezadu";
    }

    /**
     * Může hráč kartu vyložit? Nemělo by nahrazovat logiku v kartě.
     * @param kdo
     * @param co
     * @return 
     * @see #muzeSpalit(cz.honza.bang.sdk.Karta) 
     * @see #muzeZahrat(cz.honza.bang.sdk.Karta, cz.honza.bang.sdk.Hrac) 
     */
    default public  boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co){
        return true;
    }
    
    
    /**
     * Spustí první tah. Je někdo konrétní kdo má hrát, je to náhodně apod.
     * 
     * Ve výchozím chování to je hráč, který založil hru.
     * @param spravceTahu 
     */
    default public  void spustitPrvniTah(SpravceTahu spravceTahu){
        // Výchozí chování: získej prvního hráče a zahaj jeho tah
        Hrac prvniHrac = spravceTahu.dalsiHrac();
        prvniHrac.zahajitTah();
    }

    /**
     * Volá se když hráč klikne na vlastní UI prvek.
     * 
     * Volá se pouze pokud byl callback nastaven na null, jinak se volá callback.
     * @param hrac Hráč, který klikl
     * @param uiId ID prvku, na který hráč klikl
     * @see cz.honza.bang.sdk.KomunikatorHry#pridejUIButton(cz.honza.bang.sdk.Hrac, int, java.lang.String, boolean, java.lang.Runnable)
     */
    default public void uiButtonClicked(Hrac hrac, int uiId){
        // Výchozí chování - nic se neděje
    }
}
