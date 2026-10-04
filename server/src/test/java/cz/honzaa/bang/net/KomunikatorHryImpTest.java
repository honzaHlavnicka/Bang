package cz.honzaa.bang.net;

import cz.honzaa.bang.HracImp;
import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import org.java_websocket.WebSocket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KomunikatorHryImpTest {

    private KomunikatorHryImp komunikator;
    private WebSocket mockConn;
    private HracImp hrac1;

    private static class DummyKarta extends Karta {
        private final String jmeno;

        public DummyKarta(String jmeno) {
            super(null, null);
            this.jmeno = jmeno;
        }

        @Override
        public String getJmeno() {
            return jmeno;
        }

        @Override
        public String getObrazek() {
            return jmeno;
        }

        @Override
        public String getZadniObrazek() {
            return "rub";
        }
    }

    @BeforeAll
    public static void initPlugins() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        SocketServer mockServer = mock(SocketServer.class);
        komunikator = KomunikatorHryImp.vytvor(mockServer, 123456, 0);
        mockConn = mock(WebSocket.class);
        when(mockConn.isOpen()).thenReturn(true);

        komunikator.novyHrac(mockConn);
        hrac1 = komunikator.getAdmin();
    }

    @Test
    public void testPozadejOKartyNullSafe() {
        List<Karta> kartySNull = new ArrayList<>();
        kartySNull.add(new DummyKarta("Karta1"));
        kartySNull.add(null);
        kartySNull.add(new DummyKarta("Karta2"));

        assertDoesNotThrow(() -> komunikator.pozadejOKarty(hrac1, kartySNull, "Vyber", 1, 1, false));
        assertDoesNotThrow(() -> komunikator.pozadejOKarty(hrac1, null, "Vyber", 1, 1, false));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());
        assertTrue(captor.getAllValues().stream().anyMatch(msg -> msg.startsWith("vyberKartu:")));
    }

    @Test
    public void testPozadejOHraceNullSafe() {
        Hrac hrac2 = mock(Hrac.class);
        when(hrac2.getId()).thenReturn(2);

        List<Hrac> hraciSNull = Arrays.asList(hrac1, null, hrac2);

        assertDoesNotThrow(() -> komunikator.pozadejOHrace(hrac1, hraciSNull, "Vyber hráče", 1, 1, false));
        assertDoesNotThrow(() -> komunikator.pozadejOHrace(hrac1, null, "Vyber hráče", 1, 1, false));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());
        assertTrue(captor.getAllValues().stream().anyMatch(msg -> msg.startsWith("vyberHrace:")));
    }
}
