/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cz.honza.bang.sdk;


import java.util.Stack;

/**
 *
 * @author jan.hlavnicka.s
 */
public interface HerniPravidla {    
    
    /**
     * Spustí se po spuštění hry.Může třeba vyložit kartu atd.
     * @see #pripravitHrace(cz.honza.bang.Hrac)
     */
    public void poSpusteniHry();
    
    /**
     * Připravý hráče ke hře, například mu rozdá karty.
     */
    public void pripravitHrace(Hrac hrac);

    /**
     * Volat poté, co hráč odehraje kartu.
     * @param kym
     */
    public void poOdehrani(Hrac kym);
    
    /**
     * Volat, když má někdo 0 životů.
     * @param komu
     */
    public void dosliZivoty(Hrac komu);

    /**
     * Volat, pokud hráč CHCE ukončit tah.
     * Nemělo by se volat v kartách a podobně
     * @param kdo
     * @return
     */
    public boolean hracChceUkoncitTah(Hrac kdo);

    /**
     * Volat pokud hráč klikne na lízací balíček nebo řekne, že chce lízat.
     * Nemělo by se volat v kartách a podobně.
     * @param kdo
     * @return true - bylo mu zíznuto.
     * @return false - nebylo mu zíznuto
     */
    public boolean hracChceLiznout(Hrac kdo);
    
    /**
     * Nplní balíček kartami, popřípadě zamíchá.
     * @param balicek
     */
    public void pripravBalicek(Balicek<Karta> balicek);
    /**
     * Naplní balíček postavami, které se mohou rozdávat.
     * Mělo by je zamíchat, protože se rozdávají od vrchu.
     * @param balicekPostav
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
    
    default public String getVychoziZadniObrazek(){
        return "bang";
    }

    default public  boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co){
        return true;
    }
}
