
package cz.honza.bang;
/*
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;

import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/hra")
public class BangEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Nový klient se připojil: " + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println("Od klienta (" + session.getId() + "): " + message);
        // odpověď klientovi
        return "Server přijal: " + message;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Klient se odpojil: " + session.getId());
    }
}
*/