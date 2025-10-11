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
import cz.honza.bang.SpravceHernichPravidel;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer extends WebSocketServer {
    private Map<WebSocket, KomunikatorHry> komunikatoryHracu = new  ConcurrentHashMap<>();
    private Map<String, KomunikatorHry> hryPodleId = new ConcurrentHashMap<>();
    private List<Integer> pouziteKody = new ArrayList<>();
    private Random random = new Random();

    public SocketServer(InetSocketAddress address) {
        super(address);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
        conn.send("welcome");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + reason);
        //FIX: java.lang.NullPointerException: Cannot invoke "cz.honza.bang.net.KomunikatorHry.hracOdpojen(org.java_websocket.WebSocket)" because the return value of "java.util.Map.remove(Object)" is null
        komunikatoryHracu.remove(conn).hracOdpojen(conn);
        
       
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("zpráva: " + message);

        if(message.startsWith("pripojeniKeHre:222")){ //TODO: ODSTRANIT! POUZE PRO TESTY, BESPEČNOSTNÍ CHYBA. 
            conn.send("popoup:zde je seznam všech her\n" + hryPodleId);
            return;
        }
        
        if(message.startsWith("serverInfo")){//TODO: omezit přístup heslem
            conn.send("serverDataHTML:"+serverDataHTML());
            return;
        }
           
        if(message.startsWith("infoHer")){
            StringBuilder sb = new StringBuilder("infoHer:{\"verze\":\"0.0.7\",\"hry\":");
            sb.append(SpravceHernichPravidel.getJSONVytvoritelneHry());
            //tady jsou přidávat další data o serveru.
            sb.append("}");
            conn.send(sb.toString());
            return;
        }

        if (message.startsWith("novaHra") || message.startsWith("pripojeniKeHre:111")) {
            KomunikatorHry komunikatorCoAsiNeexistuje = komunikatoryHracu.get(conn); //pro kontrolu, jestli už hrač hru nehraje
            if (komunikatorCoAsiNeexistuje != null) {
                conn.send("error:{\"error\":\"už jsi připojen ke hře\"}");
                return;
            }
            int typHry;
            try{
                typHry = Integer.parseInt(message.replace("novaHra", ""));
            }catch(NumberFormatException ex){
                typHry = 0;
            }
            
            int kodKry = nahodneIdHry();
            KomunikatorHry komunikator = KomunikatorHry.vytvor(this, kodKry, typHry);
            hryPodleId.put(Integer.toString(kodKry), komunikator);
            System.out.println("novaHra:" + kodKry);
            conn.send("novaHra:" + kodKry);
            komunikator.novyHrac(conn);
            komunikatoryHracu.put(conn, komunikator);
            komunikator.nactiHru(conn);
            return;
        }
        
        if(message.startsWith("pripojeniKeHre:")){
            String idHry = message.replace("pripojeniKeHre:", "");
            KomunikatorHry komunikatorCoAsiNeexistuje = komunikatoryHracu.get(conn); //pro kontrolu, jestli už hrač hru nehraje
            if(komunikatorCoAsiNeexistuje != null){
                conn.send("error:{\"error\":\"už jsi připojen ke hře\"}");
                return;
            }

            KomunikatorHry komunikatorHry = hryPodleId.get(idHry);
            if(komunikatorHry == null){
                conn.send("error:{\"error\":\"Hra neexistuje\"}");
                return;
            }
            if(komunikatorHry.novyHrac(conn)){ //přidá nového hráče, pokud se to nepovede, tak ho to nebude ukládat k dané hře, aby se ještě mohl připojit.
                komunikatoryHracu.put(conn, komunikatorHry);
                komunikatorHry.nactiHru(conn);
            }

            return;
        }
        
        if(message.startsWith("vraceniSe:")){
            KomunikatorHry komunikator = hryPodleId.get(message.substring(10, 16));
            if(komunikator == null){
                conn.send("error:{\"error\":\"hra do které se snažíš připojit neexistuje\"}");
                return;
            }
            if(komunikator.vraciSeHrac( conn,message.substring(16))){
                komunikatoryHracu.put(conn, komunikator);
               
            }
            return;
        }

        
        KomunikatorHry komunikator = komunikatoryHracu.get(conn);
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
        int kod = random.nextInt(100000, 999999);
        if(pouziteKody.indexOf(Integer.valueOf(kod)) != -1){
            return nahodneIdHry(); 
        }
        return kod;
    }
    
    public void ukoncitHru(int kod){
        hryPodleId.remove(String.valueOf(kod));
        
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
            KomunikatorHry komunikator = komunikatoryHracu.get(connection);
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
            KomunikatorHry komunikator = hryPodleId.get(idHry);
       
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

    public static void main(String[] args) {
        SocketServer server = new SocketServer(new InetSocketAddress("0.0.0.0", 8887));
        server.start();
        
    }
    
    
}

