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

    public static void main(String[] args) throws Exception {
        // vytvoření serveru
        WebSocketServer server = new  SocketServer(new InetSocketAddress("0.0.0.0", 9999));// tvá implementace WebSocketServer
        server.setReuseAddr(true);
        server.start();

        // vytvoření GUI okna
        JFrame frame = new JFrame("Bang server");
        JButton stopButton = new JButton("Stop server");

        stopButton.addActionListener(e -> {
            try {
                server.stop(); // korektně ukončí server
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            frame.dispose(); // zavře okno
            System.exit(0);  // ukončí program
        });

        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Server běží!"));
        frame.add(stopButton);
       
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
