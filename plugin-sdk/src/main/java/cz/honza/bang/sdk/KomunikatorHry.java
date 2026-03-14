/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

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
   
    public CompletableFuture<String> pozadejOdpoved(String otazka,Hrac komu);
       

    public int getIdHry();
    public int pocetHracu();

    public Hrac getAdmin();

    public void setAdmin(Hrac admin);
    
    
    
}
