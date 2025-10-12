/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

import cz.honza.bang.Hra;
import cz.honza.bang.Hrac;
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
public class KomunikatorHry {
    private Hra hra;
    private SocketServer socket;
    private Map<Hrac, WebSocket> websocketPodleHracu = new ConcurrentHashMap<>();
    private Map<WebSocket, Hrac> hraciPodleWebsocketu = new ConcurrentHashMap<>();
    private Map<String, Hrac> hraciPodlIdentifikatoru = new ConcurrentHashMap<>();
    private int idHry;
    private final long SMAZAT_NEAKTIVNI_HRU_MS = 300_000;
    private final Map<Integer, CompletableFuture<String>> cekajiciOdpovedi = new ConcurrentHashMap<>();
    private int podleniIdCekaciOdpovedi = 0;
    
    public KomunikatorHry(SocketServer socket,int id) {
        this.socket = socket;
        idHry = id;
    }
    public static KomunikatorHry vytvor(SocketServer socket,int id,int typHry){
        KomunikatorHry komunikator = new KomunikatorHry(socket, id);
        komunikator.hra = Hra.vytvor(komunikator, id,typHry);
        return komunikator;
    }
    
    public void prislaZprava(WebSocket conn, String message) {
        Hrac hrac = hraciPodleWebsocketu.get(conn);
        
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
                posiChybu(hrac,Chyba.NEMUZES_UKONCIT_TAH);
        }
        if(message.startsWith("linuti")){
            hrac.lizniKontrolovane();
        }
        if(message.startsWith("dialog:")){ //očekávaný formát: "dialog:<ID>,<DATA>"
            String[] data = message.replace("dialog:", "").split(",",2);
            zpracujPozadanouOdpoved(Integer.valueOf(data[0]), data[1]);
        }
    }
    
    /**
     * Pošle zprávu všekm hráčům ve hře.
     * @param co Zpráva, která se pošle všem hráčům
     */
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
    public void posliVsem(String co,Hrac komuNe) {
        for (WebSocket conn : hraciPodleWebsocketu.keySet()) {
            if(!hraciPodleWebsocketu.get(conn).equals(komuNe)){
                conn.send(co);
            }
            
        }
    }
    
    public void posli(Hrac komu, String co){
        websocketPodleHracu.get(komu).send(co);
        System.out.println("posilani zpravy: " + co + ", ::: "+websocketPodleHracu.get(komu));
    }
    
    public boolean novyHrac(WebSocket websocket){
        if(hra.isZahajena()){
            websocket.send("error:{\"error\":\"tato hra už byla zahájena. Bohužel se už nejde připojit.\"}");
            return false;
        }
        
        websocket.send("pripojenKeHre");
        Hrac hrac = hra.novyHrac();
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
        
//        StringBuilder sb = new StringBuilder("aktualizace{hraci:[");
//        for (Hrac hrac : hra.getHraci()) {
//            sb.append('\"');
//            sb.append(hrac.getJmeno());
//            sb.append('\"');
//            sb.append(',');
//        }
//        sb.append("]}");
//        conn.send(sb.toString());
        
        hra.nactiHru(conn,hraciPodleWebsocketu.get(conn));
        
        
        //TODO: načtení hry
    }
    
    public void hracOdpojen(WebSocket conn){
        Hrac hrac = hraciPodleWebsocketu.get(conn);
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
    public void posiChybu(Hrac komu,Chyba chyba){
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
        Hrac hrac = hraciPodlIdentifikatoru.get(token);
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
    
    public CompletableFuture<String> pozadejOdpoved(String otazka,Hrac komu) {
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

    
    //AKCE:

    public int getIdHry() {
        return idHry;
    }
    public int pocetHracu(){
        return hraciPodlIdentifikatoru.size();
    }
    
}
