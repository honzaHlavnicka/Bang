/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

/**
 *
 * @author honza
 */
import cz.honza.bang.pravidla.SpravceHernichPravidel;
import cz.honza.bang.sdk.Chyba;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketServer extends WebSocketServer {
    private Map<WebSocket, KomunikatorHryImp> komunikatoryHracu = new  ConcurrentHashMap<>();
    private Map<String, KomunikatorHryImp> hryPodleId = new ConcurrentHashMap<>();
    private Set<Integer> pouziteKody = new HashSet<>();
    private Random random = new Random();
    private String adminPassword;
    private Set<WebSocket> overeniAdmini = ConcurrentHashMap.newKeySet();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean overujeSeHeslo;
    
    public SocketServer(InetSocketAddress address) {
        super(address);
        System.out.println("Adresa serveru je: " + address.toString());
        System.out.println("Port je: " + address.getPort());
        
        // Načti heslo z proměnné prostředí
        this.adminPassword = System.getenv("ADMIN_PASSWORD");
        if (this.adminPassword == null || this.adminPassword.isEmpty()) {
            this.adminPassword = "heslo123"; // Výchozí heslo
            System.out.println("[!] ADMIN_PASSWORD není nastaveno, použije se výchozí heslo. Nastavte ADMIN_PASSWORD pro produkci!");
        }
        
        // Načti pluginy už při startu
        System.out.println("Načítám pluginy...");
        SpravceHernichPravidel.pregeneruj();
        
        overujeSeHeslo = false;
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
        conn.send("welcome");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + reason);
       
        KomunikatorHryImp komunikator = komunikatoryHracu.remove(conn);
        overeniAdmini.remove(conn);
        
        if(komunikator != null){           //Pokud byl uživatel připojen ke hře.
            komunikator.hracOdpojen(conn);
            // TODO: odpojení peerJS
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("zpráva: " + message);

        if (message.startsWith("serverInfo:")) {
            String zadaneHeslo = message.replace("serverInfo:", "");
            overAdminHeslo(conn, zadaneHeslo).thenAccept(uspech -> {
                if (uspech) {
                    conn.send("serverDataHTML:" + serverDataHTML());
                }
            });
            return;
        }
        if (message.startsWith("gameInfo:")) {
            String[] parts = message.substring("gameInfo:".length()).split(":", 2);
            if (parts.length < 2) {
                posliChybu(conn, Chyba.CHYBA_PROTOKOLU);
                return;
            }
            String kod = parts[0];
            String heslo = parts[1];

            overAdminHeslo(conn, heslo).thenAccept(uspech -> {
                if (uspech) {
                    KomunikatorHryImp komunikator = hryPodleId.get(kod);
                    if (komunikator == null) {
                        posliChybu(conn, Chyba.HRA_NEEXISTUJE);
                        return;
                    }
                    String gameInfoJSON = komunikator.getGameStateJSON();
                    conn.send("gameInfoJSON:" + gameInfoJSON);
                }
            });
            return;
        }
        if (message.startsWith("restartovatPluginy:")) {
            String zadaneHeslo = message.replace("restartovatPluginy:", "");
            overAdminHeslo(conn, zadaneHeslo).thenAccept(uspech -> {
                if (uspech) {
                    SpravceHernichPravidel.pregeneruj();
                    conn.send("ok");
                }
            });
            return;
        }
        if(message.startsWith("infoHer")){
            StringBuilder sb = new StringBuilder("infoHer:{\"verze\":\"1.0.0\",\"hry\":");
            sb.append(SpravceHernichPravidel.getJSONVytvoritelneHry());
            //tady jsou přidávat další data o serveru.
            sb.append("}");
            conn.send(sb.toString());
            return;
        }

        if (message.startsWith("novaHra") || message.startsWith("pripojeniKeHre:111")) {
            KomunikatorHryImp komunikatorCoAsiNeexistuje = komunikatoryHracu.get(conn); //pro kontrolu, jestli už hrač hru nehraje
            if (komunikatorCoAsiNeexistuje != null) {
                posliChybu(conn, Chyba.UZ_PRIPOJEN);
                return;
            }
            int typHry;
            try{
                typHry = Integer.parseInt(message.replace("novaHra:", ""));
            }catch(NumberFormatException ex){
                typHry = 0;
            }
            
            int kodKry = nahodneIdHry();
            KomunikatorHryImp komunikator = KomunikatorHryImp.vytvor(this, kodKry, typHry);
            hryPodleId.put(Integer.toString(kodKry), komunikator);
            pouziteKody.add(Integer.valueOf(kodKry));
            System.out.println("novaHra:" + kodKry);
            conn.send("novaHra:" + kodKry);
            komunikator.novyHrac(conn);
            komunikatoryHracu.put(conn, komunikator);
            komunikator.nactiHru(conn);
            return;
        }
        
        if(message.startsWith("pripojeniKeHre:")){
            String idHry = message.replace("pripojeniKeHre:", "");
            KomunikatorHryImp komunikatorCoAsiNeexistuje = komunikatoryHracu.get(conn); //pro kontrolu, jestli už hrač hru nehraje
            if(komunikatorCoAsiNeexistuje != null){
                posliChybu(conn, Chyba.UZ_PRIPOJEN);
                return;
            }

            KomunikatorHryImp komunikatorHry = hryPodleId.get(idHry);
            if(komunikatorHry == null){
                posliChybu(conn, Chyba.HRA_NEEXISTUJE);
                return;
            }
            if(komunikatorHry.novyHrac(conn)){ //přidá nového hráče, pokud se to nepovede, tak ho to nebude ukládat k dané hře, aby se ještě mohl připojit.
                komunikatoryHracu.put(conn, komunikatorHry);
                komunikatorHry.nactiHru(conn);
            }

            return;
        }
        
        if(message.startsWith("vraceniSe:")){
            if(message.length() < 16 + 8){
                conn.send("error:{\"error\":\"Neplatný token\"}");
                return;
            }
            KomunikatorHryImp komunikator = hryPodleId.get(message.substring(10, 16));
            if(komunikator == null){
                conn.send("error:{\"error\":\"Hra, do které se snažíš připojit neexistuje\"}");
                return;
            }
            if(komunikator.vraciSeHrac( conn,message.substring(16))){
                komunikatoryHracu.put(conn, komunikator);
               
            }
            return;
        }

        
        KomunikatorHryImp komunikator = komunikatoryHracu.get(conn);
        if(komunikator == null){
            conn.send("error:{\"error\":\"Nejsi připojen ke hře\"}");
            return;
        }
        
        komunikator.prislaZprava(conn, message);
        
        conn.send("Echo: " + message);

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        System.out.println("errrrrrrror");
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
    }
   
    private int nahodneIdHry(){
        int kod = random.nextInt(999999 - 100000 + 1) + 100000;
        if(pouziteKody.contains(kod)){
            return nahodneIdHry(); 
        }
        return kod;
    }
    
    public void ukoncitHru(int kod){
        hryPodleId.remove(String.valueOf(kod));
        pouziteKody.remove(Integer.valueOf(kod));
        
    }
    
    private String serverDataHTML(){
        StringBuilder sb = new StringBuilder("<div>");
        sb.append("<h1>Server data</h1>");
        sb.append("<h2>adress:</h2>adresa: ");
        sb.append(this.getAddress());
        sb.append(", port:");
        sb.append(this.getPort());
        sb.append(", host:");
        sb.append(this.getAddress().getHostName());
        sb.append(", isReuseAddr:");
        sb.append(this.getAddress().isUnresolved());


        sb.append("<h2> použité kódy her:</h2>");
        sb.append(pouziteKody.toString());

        
        sb.append("<h2>připojení:</h2>");
        sb.append("<table><Thead><tr><th>remoteSocketAdress</th></th>gameId</th><th>isOpen</th><th>ReadyState</th></tr></thead><tbopdy>");
        
        for (WebSocket connection : getConnections()) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(connection.getRemoteSocketAddress());
            sb.append("</td><td>");
            KomunikatorHryImp komunikator = komunikatoryHracu.get(connection);
            if(komunikator == null){
                sb.append("nepřipojen");
            }else{
                sb.append(komunikator.getIdHry());
            }
            
            sb.append("</td><td>");
            sb.append(connection.isOpen());
            sb.append("</td><td>");
            sb.append(connection.getReadyState().name());
            sb.append("</td></tr>");
        }
        sb.append("</tbody></table>");
        sb.append("<h2>Hry:</h2>");
        sb.append("<table><Thead><tr><th>id</th><th>pocetHracu</th></tr></thead><tbopdy>");

        for (String idHry : hryPodleId.keySet()) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(idHry);
            sb.append("</td><td>");
            KomunikatorHryImp komunikator = hryPodleId.get(idHry);
       
            sb.append(komunikator.pocetHracu());
            sb.append(" hráčů");
            sb.append("</td></tr>");
        }
        sb.append("</tbody></table>");

        sb.append("<h2>Celkový počet připojení:</h2>");
        sb.append(getConnections().size());

        sb.append("<h2>Celkový počet her:</h2>");
        sb.append(hryPodleId.size());
        



        sb.append("</div>");
        return sb.toString();
    }
    public void posliChybu(WebSocket conn, Chyba chyba) {
        conn.send("error:{\"error\":\"" + chyba.getZprava() + "\",\"kod\":" + chyba.getKod() + ",\"skupina\":" + chyba.getSkupina() + "}");
    }
    
    /**
     * Ověří heslo. První pokus na spojení trvá 4 sekundy. 
     * Další pokusy ze stejného (již ověřeného) spojení jsou okamžité.
     *
     * @return true pokud je heslo správné, false pokud je špatné.
     */
    private CompletableFuture<Boolean> overAdminHeslo(WebSocket conn, String zadaneHeslo) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (overeniAdmini.contains(conn)) {
            // Spojení je už ověřené
            future.complete(adminPassword.equals(zadaneHeslo));
            return future;
        }
        
        if(overujeSeHeslo){ // Už probíhá 4s pauza před kontrolou hesla.
            future.complete(false);
            return future;
        }
        
        // Spojení není ověřené.
        overujeSeHeslo = true;
        scheduler.schedule(() -> {
            overujeSeHeslo = false;
            if (adminPassword.equals(zadaneHeslo)) {
                overeniAdmini.add(conn);
                future.complete(true);
            } else {
                System.out.println("[SECURITY] Odmítnut admin přístup z " + conn.getRemoteSocketAddress());
                posliChybu(conn, Chyba.SPATNE_HESLO);
                future.complete(false);
            }
        }, 4, TimeUnit.SECONDS);

        return future;
    }

    public static void main(String[] args) {
        SocketServer server = new SocketServer(new InetSocketAddress("0.0.0.0", 22207));
        server.start();
    }
}

