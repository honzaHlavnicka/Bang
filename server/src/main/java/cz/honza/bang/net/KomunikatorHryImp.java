/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;

/**
 *
 * @author honza
 */
public class KomunikatorHryImp implements cz.honza.bang.sdk.KomunikatorHry{
    private HraImp hra;
    private SocketServer socket;
    private Map<HracImp, WebSocket> websocketPodleHracu = new ConcurrentHashMap<>();
    private Map<WebSocket, HracImp> hraciPodleWebsocketu = new ConcurrentHashMap<>();
    private Map<String, HracImp> hraciPodlIdentifikatoru = new ConcurrentHashMap<>();
    private int idHry;
    private final long SMAZAT_NEAKTIVNI_HRU_MS = 300_000;
    private final Map<Integer, CompletableFuture<String>> cekajiciOdpovedi = new ConcurrentHashMap<>();
    private int podleniIdCekaciOdpovedi = 0;
    private HracImp admin;
    
    public KomunikatorHryImp(SocketServer socket,int id) {
        this.socket = socket;
        idHry = id;
    }
    public static KomunikatorHryImp vytvor(SocketServer socket,int id,int typHry){
        KomunikatorHryImp komunikator = new KomunikatorHryImp(socket, id);
        komunikator.hra = HraImp.vytvor(komunikator,typHry);
        return komunikator;
    }
    
    public void prislaZprava(WebSocket conn, String message) {
        HracImp hrac = hraciPodleWebsocketu.get(conn);
        
        if(message.startsWith("noveJmeno:")){
            hrac.setJmeno(message.replace("noveJmeno:", ""));
            posliVsem("noveJmeno:" + hrac.getId() + "," + hrac.getJmeno());
        }
        if(message.startsWith("nactiHru")){
            nactiHru(conn);
        }
        if(message.startsWith("chat:")){
            posliVsem(message + " [od: "+hrac.getJmeno()+"]");
        }
        if(message.startsWith("zahajeniHry")){
            hra.setZahajena(true);
        }
        if(message.startsWith("getIdHry")){
            conn.send("setIdHry:"+idHry);
        }
        if(message.startsWith("setPostava:")){
            hrac.setPostava(message.replace("setPostava:", ""));
        }
        if(message.startsWith("odehrani:")){
            hrac.odehranaKarta(message.replace("odehrani:", ""));
        }
        if(message.startsWith("konecTahu")){
            if(!hra.getHerniPravidla().hracChceUkoncitTah(hrac))
                posliChybu(hrac,Chyba.NEMUZES_UKONCIT_TAH);
        }
        if(message.startsWith("linuti")){
            hrac.lizniKontrolovane();
        }
        if(message.startsWith("dialog:")){ //očekávaný formát: "dialog:<ID>,<DATA>"
            String[] data = message.replace("dialog:", "").split(",",2);
            zpracujPozadanouOdpoved(Integer.valueOf(data[0]), data[1]);
        }
        if(message.startsWith("vylozeni:")){
            String[] data = message.replace("vylozeni:", "").split(",",2);
            if(data.length == 1){
                data = new String[]{data[0],Integer.toString(hrac.getId())};
            }else if(data.length == 0){
                posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
                return;
            }
            hrac.vylozitKartu(data[0], data[1]);
        }
        if(message.startsWith("spaleni:")){
            hrac.spalitKartu(message.replace("spaleni:", ""));
        }
    }
    
    /**
     * Pošle zprávu všekm hráčům ve hře.
     * @param co Zpráva, která se pošle všem hráčům
     */
    @Override
    public void posliVsem(String co){
        for (WebSocket conn : hraciPodleWebsocketu.keySet()) {
            conn.send(co);
        }
    }
    
    
    
    /**
     * Pošle zprávu všem hráčům ve hře, kromě jednoho. Hodí se pro poslání podrobné informace jednomu hráči a méně podrobné informaci ostatním.
     * @param co Zpráva, která se pošle všem hráčům, kromě jednoho
     * @param komuNe Hráč, který zprávu neobdrží
     */
    @Override

    public void posliVsem(String co,cz.honza.bang.sdk.Hrac komuNe) {
        for (WebSocket conn : hraciPodleWebsocketu.keySet()) {
            if(!hraciPodleWebsocketu.get(conn).equals(komuNe)){
                conn.send(co);
            }
        }
    }
    
    @Override
    public void posli(cz.honza.bang.sdk.Hrac komu, String co){
        websocketPodleHracu.get(komu).send(co);
        System.out.println("posilani zpravy: " + co + ", ::: "+websocketPodleHracu.get(komu));
    }
    
    public boolean novyHrac(WebSocket websocket){
        if(hra.isZahajena()){
            websocket.send("error:{\"error\":\"tato hra už byla zahájena. Bohužel se už nejde připojit.\"}");
            return false;
        }
        
        websocket.send("pripojenKeHre");
        HracImp hrac = hra.novyHrac();
        websocketPodleHracu.put(hrac, websocket);
        hraciPodleWebsocketu.put(websocket, hrac);
        String identifikator = GeneratorTokenu.NovytokenHrace();
        websocket.send("token:" + idHry + identifikator);
        hraciPodlIdentifikatoru.put(identifikator, hrac);
        hra.hracVytvoren(hrac);
        posliVsem("novyHrac:"+hrac.toJSON()); 
        return true;
    }
    
    public void nactiHru(WebSocket conn){
        conn.send("načítání hry. tohle bude nejakej json.");
        hra.nactiHru(conn,hraciPodleWebsocketu.get(conn));
    }
    
    public void hracOdpojen(WebSocket conn){
        HracImp hrac = hraciPodleWebsocketu.get(conn);
        hraciPodleWebsocketu.remove(conn);
        websocketPodleHracu.remove(hrac);
        if(hraciPodleWebsocketu.isEmpty()){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    socket.ukoncitHru(idHry);
                }
            }, SMAZAT_NEAKTIVNI_HRU_MS); 

        }
    }
    
    /**
     * Pošle klientovi chybovou zprávu.
     * @param komu komu se má chyba doručit.
     * @param chyba chyba, která se posílá.
     */
    @Override

    public void posliChybu(cz.honza.bang.sdk.Hrac komu,Chyba chyba){
        WebSocket conn = websocketPodleHracu.get(komu);
        conn.send("error:{\"error\":\"" + chyba.getZprava() + "\",\"kod\":" + chyba.getKod() + ",\"skupina:\":" + chyba.getSkupina()+ "}");
        
    }
    
    /**
     *
     * @param conn kdo se připojuje
     * @param token token který hráč používá aby se připojil
     * 
     * @return úspěch akce
     */
    public boolean vraciSeHrac(WebSocket conn, String token){
        HracImp hrac = hraciPodlIdentifikatoru.get(token);
        if(hrac == null){
            conn.send("error:{\"error\":\"hráč v této hře nenalezen\"}");
            return false;
        }
        
        WebSocket puvodniHrac = websocketPodleHracu.get(hrac);    
        if(puvodniHrac != null){
         //hráč už je připojen. je potřeba ho odpojit a nahradit novym pripojenim
         puvodniHrac.close();
        }
        
        hraciPodleWebsocketu.put(conn, hrac);
        websocketPodleHracu.put(hrac, conn);
        
        conn.send("pripojenKeHre");
        
        if(hra.isZahajena()){
            conn.send("hraZacala");
        }
        
        nactiHru(conn);
        return true;
    }
    
    @Override
    public CompletableFuture<String> pozadejOdpoved(String otazka,cz.honza.bang.sdk.Hrac komu) {
        //připravý si id:
        podleniIdCekaciOdpovedi++;
        Integer id = podleniIdCekaciOdpovedi;
        
        
        CompletableFuture<String> future = new CompletableFuture<>();
        cekajiciOdpovedi.put(id, future);

        posli(komu, otazka.replace("data-id", id.toString()));
        

        return future;
    }
    
    private void zpracujPozadanouOdpoved(Integer id, String odpoved) {
        CompletableFuture<String> future = cekajiciOdpovedi.remove(id);
        if (future != null) {
            future.complete(odpoved);
        } else {
            System.err.println("Nepodařilo se najít čekající odpověď pro id: " + id);
        }
    }

    

    @Override
    public int getIdHry() {
        return idHry;
    }
    @Override
    public int pocetHracu(){
        return hraciPodlIdentifikatoru.size();
    }

    @Override
    public HracImp getAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(cz.honza.bang.sdk.Hrac admin) {
        this.admin = (HracImp) admin;
    }

   

}
