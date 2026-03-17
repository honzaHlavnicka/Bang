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
import org.java_websocket.server.WebSocketServer;
import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.net.URL;

public class ServerApp {

    public static void main(String[] args) {
        int port = 8080; // Výchozí port
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException e) {
                System.err.println("[!] SERVER_PORT má neplatnou hodnotu: " + portEnv + ", používám port 8080");
            }
        }
        
        System.out.println("Spouštím server na portu: " + port);
        
        WebSocketServer server = new SocketServer(new InetSocketAddress("0.0.0.0", port));
        server.setReuseAddr(true);
        server.start();
    }
}
