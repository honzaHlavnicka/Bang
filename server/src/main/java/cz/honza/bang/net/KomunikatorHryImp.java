/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.HraImp;
import cz.honza.bang.HracImp;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.zastupnaKarta;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private int posledniIdCekaciOdpovedi = 0;
    private HracImp admin;
    private int pocetPripojenychHracu = 0;
    private Map<HracImp, Map<Integer, CustomUIButton>> customUIByPlayer = new ConcurrentHashMap<>();
    private int nextUIButtonId = 1000;
    
    // Timeout pro smazání neaktivní hry
    private Timer hraCleupTimer = null;
    private TimerTask hraCleupTask = null;
    
    // Třída pro reprezentaci vlastního UI prvku
    private static class CustomUIButton {
        int id;
        String text;
        boolean disabled;
        
        CustomUIButton(int id, String text, boolean disabled) {
            this.id = id;
            this.text = text;
            this.disabled = disabled;
        }
    }
    
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
            posliZmenuJmena(hrac);
        }
        if(message.startsWith("nactiHru")){
            nactiHru(conn);
        }
        if(message.startsWith("chat:")){
            posliVsem(message + " [od: "+hrac.getJmeno()+"]");
        }
        if(message.startsWith("zahajeniHry")){
            // Kontrola, jestli je hráč admin (ten, který vytvořil hru)
            if (!hrac.equals(getAdmin())) {
                posliChybu(hrac, Chyba.NEJSI_ADMIN_HRY);
                return;
            }
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
            try{
                zpracujPozadanouOdpoved(Integer.valueOf(data[0]), data[1]);
            }catch(Exception ex){
                posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
            }
        }
        if(message.startsWith("uiClick:")){ //očekávaný formát: "uiClick:<ID>"
            try {
                int uiId = Integer.parseInt(message.replace("uiClick:", ""));
                hra.getHerniPravidla().uiButtonClicked(hrac, uiId);
            } catch (NumberFormatException ex) {
                posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
            }
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
        if(message.startsWith("novaHraSHracema:")){
            if(!hrac.equals(admin)){
                posliChybu(hrac, Chyba.NEJSI_ADMIN_HRY);
                return;
            }
            try {
                int id = Integer.parseInt(message.replace("novaHraSHracema:", ""));
                smazatHruAVyrobytNovou(id);
            } catch (NumberFormatException ex) {
                posliChybu(hrac, Chyba.CHYBA_PROTOKOLU);
            }
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

        if(pocetPripojenychHracu >= 30){
            websocket.send("error:{\"error\":\"server je plný.\"}");
            return false;
        }
        
        // Zrušit timeout smazání hry - nějaký hráč se znovu připojil
        zrusitTimeoutSmezaniHry();
        
        websocket.send("pripojenKeHre");
        HracImp hrac = hra.novyHrac();
        if(websocketPodleHracu.isEmpty()){
            admin = hrac;
        }
        websocketPodleHracu.put(hrac, websocket);
        hraciPodleWebsocketu.put(websocket, hrac);
        String identifikator = GeneratorTokenu.NovytokenHrace();
        websocket.send("token:" + idHry + identifikator);
        hraciPodlIdentifikatoru.put(identifikator, hrac);
        hra.hracVytvoren(hrac);
        posliNovehoHrace(hrac);

        pocetPripojenychHracu++;

        return true;


    }
    
    public void nactiHru(WebSocket conn){
        hra.nactiHru(conn,hraciPodleWebsocketu.get(conn));
    }
    
    public void hracOdpojen(WebSocket conn){
        HracImp hrac = hraciPodleWebsocketu.get(conn);
        hraciPodleWebsocketu.remove(conn);
        websocketPodleHracu.remove(hrac);
        if(hraciPodleWebsocketu.isEmpty()){
            // Zrušit starý timeout pokud existuje
            zrusitTimeoutSmezaniHry();
            
            // Vytvořit nový timeout
            hraCleupTimer = new Timer("HraCleanupTimer-" + idHry, true);
            hraCleupTask = new TimerTask() {
                @Override
                public void run() {
                    socket.ukoncitHru(idHry);
                }
            };
            hraCleupTimer.schedule(hraCleupTask, SMAZAT_NEAKTIVNI_HRU_MS);
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
        conn.send("error:{\"error\":\"" + chyba.getZprava() + "\",\"kod\":" + chyba.getKod() + ",\"skupina\":" + chyba.getSkupina()+ "}");
        
    }
    
    /**
     * Pošle stavovou zprávu všem hráčům. Zpráva se zobrazí v centru obrazovky.
     * @param zprava Text zprávy, která se bude zobrazovat (např. "Hráč vybírá barvu...")
     */
    @Override
    public void posliStavovuZpravu(String zprava) {
        posliVsem("stavHry:" + zprava);
    }
    
    // ===== IMPLEMENTACE METOD AKCÍ =====

    @Override
    public void posliZmenuPostavy(Hrac hrac) {
        posliVsem("setPostava:" + hrac.getId() + "," + hrac.getPostava().name());
    }
    @Override
    public void posliZmenuPoctuKaret(Hrac hrac) {
        posliVsem("zmenaPoctuKaret:" + hrac.getId() + "," + hrac.getKarty().size(), hrac);
    }
    
    @Override
    public void posliZmenuPoctuZivotu(Hrac hrac) {
        posliVsem("pocetZivotu:" + hrac.getId() + "," + hrac.getZivoty());
    }
    
    @Override
    public void posliZahajeniTahu(Hrac hrac) {
        posliVsem("tahZacal:" + hrac.getId(), hrac);
    }
    
    @Override
    public void posliZmenuJmena(Hrac hrac) {
        posliVsem("noveJmeno:" + hrac.getId() + "," + hrac.getJmeno());
    }
    
    @Override
    public void posliNovehoHrace(Hrac hrac) {
        posliVsem("novyHrac:" + hrac.toJSON());
    }
    
    @Override
    public void posliZahajeniHry() {
        posliVsem("hraZacala");
    }
    
    @Override
    public void posliSkonceniHrace(Hrac hrac) {
        posliVsem("hracSkoncil:" + hrac.getId());
    }
    
    @Override
    public void posliVitezstvi(Hrac hrac) {
        posliVsem("vyhral:" + hrac.getId());
    }
    
    @Override
    public void posliOdebraniKarty(Hrac hrac, cz.honza.bang.sdk.Karta karta) {
        posliVsem("odehrat:" + hrac.getId() + "|" + karta.toJSON());
    }
    
    @Override
    public void posliSpaleniKarty(Hrac hrac, cz.honza.bang.sdk.Karta karta) {
        posliVsem("spalit:" + hrac.getId() + "|" + karta.toJSON());
    }
    
    @Override
    public void posliSpaleniVylozenéKarty(cz.honza.bang.sdk.Karta karta, Hrac odkud) {
        posliVsem("spalenaVylozena:" + karta.getId() + "," + odkud.getId());
    }
    
    @Override
    public void posliVylozeniKarty(Hrac hrac, Hrac predKoho, cz.honza.bang.sdk.Karta karta) {
        String predKohoId = (predKoho != null) ? String.valueOf(predKoho.getId()) : String.valueOf(hrac.getId());
        posliVsem("vylozeni:" + hrac.getId() + "," + predKohoId + "," + karta.toJSON());
    }
    
    @Override
    public void posliRychleOznameni(String oznameni, Hrac vyjimka) {
        posliVsem("rychleOznameni:" + oznameni, vyjimka);
    }

    @Override
    public void posliKonecHry() {
        posliVsem("konecHry");
    }
    
    public CompletableFuture<String> pozadejOHrace(Hrac odKoho, List<Hrac> hraci,String nadpis,int min, int max){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", nadpis);
        json.put("min", min);
        json.put("max", max);
        JSONArray hraciNaVyber = new JSONArray();
        for (Hrac hrac : hraci) {
            hraciNaVyber.put(hrac.getId());
        }
        json.put("hraci", hraciNaVyber);
        
        
        return pozadejOdpoved("vyberHrace:" + json.toString(), odKoho);
    }
    
    public CompletableFuture<String> pozadejOKarty(Hrac odKoho, List<Karta> karty, String nadpis, int min, int max){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("nadpis", nadpis);
        json.put("min", min);
        json.put("max", max);
        JSONArray kartyNaVyber = new JSONArray();
        for (Karta karta : karty) {
            JSONObject kartaJson = new JSONObject();
            kartaJson.put("id", karta.getId());
            kartaJson.put("obrazek", karta.getObrazek());
            kartaJson.put("jmeno", karta.getJmeno());
            kartyNaVyber.put(kartaJson);
            
        }
        json.put("karty", kartyNaVyber);
        
        return pozadejOdpoved("vyberKartu:" + json.toString(), odKoho);
    }
    
    public CompletableFuture<String> pozadejOVyberMoznosti(Hrac odKoho, List<String> moznosti, String nadpis){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        JSONArray akce = new JSONArray();
        for (int i = 0; i < moznosti.size(); i++) {
            JSONObject akceObj = new JSONObject();
            akceObj.put("id", i);
            akceObj.put("nazev", moznosti.get(i));
            akce.put(akceObj);
        }
        json.put("akce", akce); 

        return pozadejOdpoved("vyberAkci:" + json.toString(), odKoho);
    }

    public CompletableFuture<String> pozadejOText(Hrac odKoho, String nadpis, String placeholder, String buttonText){
        JSONObject json = new JSONObject();
        json.put("id", "data-id");
        json.put("title", nadpis);
        if(placeholder != null) json.put("placeholder", placeholder);
        if(buttonText != null) json.put("buttonText", buttonText);
        
        return pozadejOdpoved("vyberText:" + json.toString(), odKoho);
    }

    @Override
    public void posliVysledky(Hrac[][] vysledky) {
        int radky = vysledky.length;
        int[][] idPole = new int[radky][];

        for (int i = 0; i < radky; i++) {
            int pocetHracuVeSkupine = vysledky[i].length;
            idPole[i] = new int[pocetHracuVeSkupine];

            for (int j = 0; j < pocetHracuVeSkupine; j++) {
                idPole[i][j] = vysledky[i][j].getId();
            }
        }

        String json = new JSONArray(idPole).toString();
        posliVsem("vysledkyHry:" + json);
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
         //hráč už je připojen. je potřeba ho odpojit a nahradit novým pripojenim
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
        posledniIdCekaciOdpovedi++;
        Integer id = posledniIdCekaciOdpovedi;
        
        
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

    
    private void smazatHruAVyrobytNovou(int id){
        hra = HraImp.vytvor(this,id);
        Map<WebSocket, HracImp> novyHraciPodleWebsocketu = new ConcurrentHashMap<>();
        Map<HracImp,WebSocket> novyWebsocketPodleHracu = new ConcurrentHashMap<>();
        Map<String, HracImp> novyHraciPodlIdentifikatoru = new ConcurrentHashMap<>();
        for (Map.Entry<WebSocket, HracImp> entry : hraciPodleWebsocketu.entrySet()) {
            WebSocket conn = entry.getKey();
            Hrac staryHrac = entry.getValue();
            HracImp novyHrac = hra.novyHrac();
            
            novyHraciPodleWebsocketu.put(conn, novyHrac);
            novyWebsocketPodleHracu.put(novyHrac,conn);
            
            
            String identifikator = GeneratorTokenu.NovytokenHrace();
            conn.send("token:" + idHry + identifikator);
            hraciPodlIdentifikatoru.put(identifikator, novyHrac);
            
            
            if(admin.equals(staryHrac)){
                setAdmin(novyHrac);
            }
        }
        
        hraciPodleWebsocketu = novyHraciPodleWebsocketu;
        websocketPodleHracu = novyWebsocketPodleHracu;
        
        cekajiciOdpovedi.clear();
        
                
        
        // zavolat hráč vytvořen na hru   
        for (Map.Entry<String, HracImp> entry : hraciPodlIdentifikatoru.entrySet()) {
            Hrac hrac = entry.getValue();
            hra.hracVytvoren(hrac);
        }
            
       //poslat informace o změně hráčům
       
        //<s>zahájit hru</s>, nebo přesunout všechny do čekací místnosti.        
    }

    @Override
    public void posliNovouKartu(Hrac hrac, Karta karta) {
        posli(hrac,"novaKarta:"+karta.toJSON());
        posliZmenuPoctuKaret(hrac);
    }

    @Override
    public int pridejUIButton(Hrac komu, int buttonId, String text, boolean disabled) {
        HracImp hracImp = (HracImp) komu;
        customUIByPlayer.computeIfAbsent(hracImp, k -> new ConcurrentHashMap<>());
        Map<Integer, CustomUIButton> playerUI = customUIByPlayer.get(hracImp);
        
        // Pokud je buttonId 0 nebo záporné, přidělíme nový ID
        if (buttonId <= 0) {
            buttonId = nextUIButtonId++;
        }
        
        CustomUIButton button = new CustomUIButton(buttonId, text, disabled);
        playerUI.put(buttonId, button);
        
        // Pošli zprávu klientovi
        JSONObject json = new JSONObject();
        json.put("id", buttonId);
        json.put("text", text);
        json.put("disabled", disabled);
        
        posli(komu, "noveUI:" + json.toString());
        
        return buttonId;
    }

    @Override
    public void smazatUI(Hrac komu, int uiId) {
        HracImp hracImp = (HracImp) komu;
        Map<Integer, CustomUIButton> playerUI = customUIByPlayer.get(hracImp);
        
        if (playerUI != null) {
            playerUI.remove(uiId);
            posli(komu, "odebratUI:" + uiId);
        }
    }

    /**
     * Zruší timeout smazání hry. Volá se při reconnectu hráče.
     */
    private void zrusitTimeoutSmezaniHry() {
        if (hraCleupTask != null) {
            hraCleupTask.cancel();
            hraCleupTask = null;
        }
        if (hraCleupTimer != null) {
            hraCleupTimer.cancel();
            hraCleupTimer = null;
        }
    }
    
    /**
     * Čistí všechny zdroje hry. Volá se při zrušení hry.
     */
    public void cleanup() {
        zrusitTimeoutSmezaniHry();
    }
    
}