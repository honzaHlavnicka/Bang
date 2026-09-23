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

public class SocketServerReconnectTest {

    private SocketServer server;

    @BeforeAll
    public static void init() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        server = new SocketServer(new InetSocketAddress(0));
    }

    private String extractToken(WebSocket conn) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());
        return captor.getAllValues().stream()
                .filter(z -> z.startsWith("token:"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Zpráva 'token:' nebyla nalezena v: " + captor.getAllValues()))
                .replace("token:", "")
                .trim();
    }

    private String extractIdHry(WebSocket conn) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());
        return captor.getAllValues().stream()
                .filter(z -> z.startsWith("novaHra:"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Zpráva 'novaHra:' nebyla nalezena v: " + captor.getAllValues()))
                .replace("novaHra:", "")
                .trim();
    }

    @Test
    public void testOvereniTokenuPlatnyANeplatny() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");

        String fullToken = extractToken(adminConn);
        assertEquals(49, fullToken.length(), "Token by měl mít 6 znaků ID hry + 43 znaků tokenu");

        WebSocket testConn = mock(WebSocket.class);
        when(testConn.isOpen()).thenReturn(true);

        // 1. Ověření platného tokenu
        server.onMessage(testConn, "overeniTokenu:" + fullToken);
        verify(testConn).send("overeniTokenu:true");

        // 2. Ověření příliš krátkého tokenu (< 6 znaků)
        server.onMessage(testConn, "overeniTokenu:abc");
        verify(testConn).send("overeniTokenu:false");

        // 3. Ověření neexistující hry
        server.onMessage(testConn, "overeniTokenu:999999" + fullToken.substring(6));
        verify(testConn, times(2)).send("overeniTokenu:false");

        // 4. Ověření neexistujícího hráče v existující hře
        String idHry = fullToken.substring(0, 6);
        server.onMessage(testConn, "overeniTokenu:" + idHry + "neplatnyTokenHraceXYZ1234567890123456789012");
        verify(testConn, times(3)).send("overeniTokenu:false");
    }

    @Test
    public void testVraceniSeUspesneObnoviHrace() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");
        String idHry = extractIdHry(adminConn);

        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);
        String hrac2Token = extractToken(hrac2Conn);

        // Hráč 2 se odpojí (např. výpadek sítě)
        server.onClose(hrac2Conn, 1006, "Abnormal closure", false);

        // Admin dostane notifikaci o odpojení hráče
        ArgumentCaptor<String> adminCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminConn, atLeastOnce()).send(adminCaptor.capture());
        assertTrue(adminCaptor.getAllValues().stream().anyMatch(z -> z.startsWith("odpojeniHrace:")),
                "Admin by měl obdržet informaci o odpojení hráče: " + adminCaptor.getAllValues());

        // Hráč 2 se znovu připojí z nového socketu
        WebSocket hrac2Reconnected = mock(WebSocket.class);
        when(hrac2Reconnected.isOpen()).thenReturn(true);
        server.onMessage(hrac2Reconnected, "vraceniSe:" + hrac2Token);

        ArgumentCaptor<String> reconnectCaptor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Reconnected, atLeastOnce()).send(reconnectCaptor.capture());
        List<String> zpravy = reconnectCaptor.getAllValues();

        assertTrue(zpravy.contains("pripojenKeHre"), "Reconnected hráč musí obdržet 'pripojenKeHre': " + zpravy);
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("setIdHry:" + idHry)), "Musí obdržet setIdHry");
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("noveIdHrace:")), "Musí obdržet své ID hráče");

        // Ověříme, že reconnected hráč může posílat zprávy do hry (např. setPostava) bez chyby "Nejsi připojen ke hře"
        server.onMessage(hrac2Reconnected, "getIdHry");
        verify(hrac2Reconnected, atLeastOnce()).send("setIdHry:" + idHry);
    }

    @Test
    public void testVraceniSePriBehuHryZasleHraZacala() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");
        String idHry = extractIdHry(adminConn);

        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);
        String hrac2Token = extractToken(hrac2Conn);

        // Zahájíme hru
        server.onMessage(adminConn, "zahajeniHry");

        // Hráč 2 se odpojí a vrátí se
        server.onClose(hrac2Conn, 1000, "Normal closure", false);

        WebSocket hrac2Reconnected = mock(WebSocket.class);
        when(hrac2Reconnected.isOpen()).thenReturn(true);
        server.onMessage(hrac2Reconnected, "vraceniSe:" + hrac2Token);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Reconnected, atLeastOnce()).send(captor.capture());
        List<String> zpravy = captor.getAllValues();

        assertTrue(zpravy.contains("hraZacala"), "Při probíhající hře musí návrat hráče poslat 'hraZacala': " + zpravy);
    }

    @Test
    public void testVraceniSeNeplatneFormaty() {
        WebSocket conn = mock(WebSocket.class);
        when(conn.isOpen()).thenReturn(true);

        // 1. Příliš krátký token (< 24 znaků)
        server.onMessage(conn, "vraceniSe:12345");
        verify(conn).send("error:{\"error\":\"neplatný token\"}");

        // 2. Neexistující hra (ale dostatečná délka)
        server.onMessage(conn, "vraceniSe:999999012345678901234567890123456789012345678");
        verify(conn).send("error:{\"error\":\"Hra do které se snažíš připojit neexistuje\"}");

        // 3. Existující hra, ale neexistující hráč
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");
        String idHry = extractIdHry(adminConn);

        server.onMessage(conn, "vraceniSe:" + idHry + "neexistujicitoken01234567890123456789012345678");
        verify(conn).send("error:{\"error\":\"hráč v této hře nenalezen\"}");
    }

    @Test
    public void testVraceniSeUzavrePredchoziSocketPokudBylOtevren() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");
        String idHry = extractIdHry(adminConn);

        WebSocket hrac2OldConn = mock(WebSocket.class);
        when(hrac2OldConn.isOpen()).thenReturn(true);
        server.onMessage(hrac2OldConn, "pripojeniKeHre:" + idHry);
        String hrac2Token = extractToken(hrac2OldConn);

        // Hráč 2 se vrátí s novým spojením, aniž by byl starý socket explicitně zavřen
        WebSocket hrac2NewConn = mock(WebSocket.class);
        when(hrac2NewConn.isOpen()).thenReturn(true);
        server.onMessage(hrac2NewConn, "vraceniSe:" + hrac2Token);

        // Starý socket musí být zavřen
        verify(hrac2OldConn).close();
    }

    @Test
    public void testAdminMigracePriOdpojeniAdmina() {
        WebSocket adminConn = mock(WebSocket.class);
        when(adminConn.isOpen()).thenReturn(true);
        server.onMessage(adminConn, "novaHra:0");
        String idHry = extractIdHry(adminConn);

        WebSocket hrac2Conn = mock(WebSocket.class);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "pripojeniKeHre:" + idHry);

        // Zkusíme zahájit hru jako hráč 2 -> mělo by selhat (NEJSI_ADMIN_HRY)
        server.onMessage(hrac2Conn, "zahajeniHry");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Conn, atLeastOnce()).send(captor.capture());
        assertTrue(captor.getAllValues().stream().anyMatch(z -> z.startsWith("error:") && z.contains("\"kod\":" + Chyba.NEJSI_ADMIN_HRY.getKod())),
                "Hráč 2 nesmí mít právo zahájit hru dokud je admin připojen");

        // Admin se odpojí
        server.onClose(adminConn, 1001, "Admin disconnected", false);

        // Nyní se adminem stává Hráč 2 (jediný připojený hráč).
        // Zahájení hry hráčem 2 by nyní již nemělo vyhazovat NEJSI_ADMIN_HRY!
        reset(hrac2Conn);
        when(hrac2Conn.isOpen()).thenReturn(true);
        server.onMessage(hrac2Conn, "zahajeniHry");

        ArgumentCaptor<String> afterPromoCaptor = ArgumentCaptor.forClass(String.class);
        verify(hrac2Conn, atLeastOnce()).send(afterPromoCaptor.capture());
        assertFalse(afterPromoCaptor.getAllValues().stream().anyMatch(z -> z.startsWith("error:") && z.contains("\"kod\":" + Chyba.NEJSI_ADMIN_HRY.getKod())),
                "Po odpojení původního admina musí být Hráč 2 povýšen na nového admina: " + afterPromoCaptor.getAllValues());
    }
}
