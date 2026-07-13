/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *
 * Původní verze tohoto projektu vznikla jako školní dílo na Gymnáziu, Praha 6, Arabská 14, v roce 2026.
 *
 * Toto je domácí verze souborů z programování.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String[] args) {
        int port = 8080; // Výchozí port
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException e) {
                logger.warn("SERVER_PORT má neplatnou hodnotu: {}, používám port 8080", portEnv);
            }
        }
        
        logger.info("Spouštím server na portu: {}", port);
        
        WebSocketServer server = new SocketServer(new InetSocketAddress("0.0.0.0", port));
        server.setReuseAddr(true);
        server.start();
    }
}
