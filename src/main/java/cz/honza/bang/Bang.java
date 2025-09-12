/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

Toto je domácí verze souborů z programování.
 */

package cz.honza.bang;

import cz.honza.bang.net.SocketServer;
import java.net.InetSocketAddress;

/**
 *
 * @author honza
 */
public class Bang {

    public static void main(String[] args) {
       
        SocketServer server = new SocketServer(new InetSocketAddress("0.0.0.0", 8887));
        server.start();
        
        
        
    }
}
