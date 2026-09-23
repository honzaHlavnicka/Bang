package cz.honzaa.bang.net;

import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.Chyba;
import org.java_websocket.WebSocket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.InetSocketAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SocketServerProtocolTest {

    private SocketServer server;

    @BeforeAll
    public static void init() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        server = new SocketServer(new InetSocketAddress(0));
    }

    @Test
    public void testNovaHraASpojeni() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        // Klient pošle požadavek na novou hru
        server.onMessage(conn, "novaHra:0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());

        List<String> zpravy = captor.getAllValues();
        // Server musí odpovědět ID vytvořené hry
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("novaHra:")), "Musí přijít potvrzení o nové hře: " + zpravy);
    }

    @Test
    public void testNovaHraUzPripojenVratiChybu() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        server.onMessage(conn, "novaHra:0");

        // Pokus o vytvoření další hry ze stejného spojení
        server.onMessage(conn, "novaHra:0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());

        List<String> zpravy = captor.getAllValues();
        assertTrue(zpravy.stream().anyMatch(z -> z.contains(String.valueOf(Chyba.UZ_PRIPOJEN.getKod()))),
                "Při pokusu o vytvoření druhé hry ze stejného socketu musí server vrátit UZ_PRIPOJEN");
    }

    @Test
    public void testPripojeniKNeexistujiciHreVratiChybu() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        server.onMessage(conn, "pripojeniKeHre:999999");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());

        List<String> zpravy = captor.getAllValues();
        assertTrue(zpravy.stream().anyMatch(z -> z.contains(String.valueOf(Chyba.HRA_NEEXISTUJE.getKod()))),
                "Při připojení k neexistující hře musí server vrátit HRA_NEEXISTUJE");
    }

    @Test
    public void testPripojeniKDvouHracuDoStejneHry() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);

        server.onMessage(adminConn, "novaHra:0");

        ArgumentCaptor<String> adminCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminConn, atLeastOnce()).send(adminCaptor.capture());

        String novaHraZprava = adminCaptor.getAllValues().stream()
                .filter(z -> z.startsWith("novaHra:"))
                .findFirst()
                .orElseThrow();

        String idHry = novaHraZprava.replace("novaHra:", "").trim();

        // Druhý hráč se připojí do téže hry
        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);

        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);

        ArgumentCaptor<String> hrac2Captor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Conn, atLeastOnce()).send(hrac2Captor.capture());

        List<String> hrac2Zpravy = hrac2Captor.getAllValues();
        assertTrue(hrac2Zpravy.stream().anyMatch(z -> z.startsWith("noveIdHrace:")),
                "Druhý hráč musí dostat své ID: " + hrac2Zpravy);
        assertTrue(hrac2Zpravy.stream().anyMatch(z -> z.startsWith("setIdHry:" + idHry)),
                "Druhý hráč musí dostat ID hry: " + hrac2Zpravy);
    }

    @Test
    public void testNeopravnenyHracNemuzeZahajitHru() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");

        ArgumentCaptor<String> adminCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminConn, atLeastOnce()).send(adminCaptor.capture());
        String idHry = adminCaptor.getAllValues().stream()
                .filter(z -> z.startsWith("novaHra:"))
                .findFirst().orElseThrow().replace("novaHra:", "").trim();

        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);

        // Hráč 2 (ne admin) se pokusí zahájit hru
        server.onMessage(hrac2Conn, "zahajeniHry");

        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Conn, atLeastOnce()).send(errorCaptor.capture());

        List<String> zpravy = errorCaptor.getAllValues();
        assertTrue(zpravy.stream().anyMatch(z -> z.contains(String.valueOf(Chyba.NEJSI_ADMIN_HRY.getKod()))),
                "Ne-admin hráč nesmí mít právo zahájit hru: " + zpravy);
    }

    @Test
    public void testAdminMuzeVyhoditHracePredZahajenimHry() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");

        ArgumentCaptor<String> adminCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminConn, atLeastOnce()).send(adminCaptor.capture());
        String idHry = adminCaptor.getAllValues().stream()
                .filter(z -> z.startsWith("novaHra:"))
                .findFirst().orElseThrow().replace("novaHra:", "").trim();

        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);

        ArgumentCaptor<String> h2Captor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Conn, atLeastOnce()).send(h2Captor.capture());

        String idZprava = h2Captor.getAllValues().stream()
                .filter(z -> z.startsWith("noveIdHrace:"))
                .findFirst().orElseThrow();
        int hrac2Id = Integer.parseInt(idZprava.replace("noveIdHrace:", "").trim());

        // Admin vyhodí hráče 2
        server.onMessage(adminConn, "vyhodHrace:" + hrac2Id);

        // Hráč 2 musí dostat zprávu s chybou a být odpojen
        verify(hrac2Conn).close();
    }

    @Test
    public void testZpravaBezPripojeniKeHreVratiChybu() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        // Pošleme herní příkaz bez předchozího připojení do hry
        server.onMessage(conn, "odehrani:5");

        verify(conn).send("error:{\"error\":\"Nejsi připojen ke hře\"}");
    }

    @Test
    public void testOvereniNeplatnehoTokenu() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        server.onMessage(conn, "overeniTokenu:kratky");
        verify(conn).send("overeniTokenu:false");

        server.onMessage(conn, "overeniTokenu:999999neexistujicitoken");
        verify(conn, times(2)).send("overeniTokenu:false");
    }
}
