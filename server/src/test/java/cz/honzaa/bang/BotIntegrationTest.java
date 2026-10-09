package cz.honzaa.bang;

import cz.honzaa.bang.net.KomunikatorHryImp;
import cz.honzaa.bang.net.SocketServer;
import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.*;
import org.java_websocket.WebSocket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BotIntegrationTest {

    private KomunikatorHryImp komunikator;
    private SocketServer mockServer;
    private WebSocket mockConnAdmin;
    private HracImp admin;

    @BeforeAll
    public static void initPlugins() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        mockServer = mock(SocketServer.class);
        komunikator = KomunikatorHryImp.vytvor(mockServer, 123456, 0);
        mockConnAdmin = mock(WebSocket.class);
        when(mockConnAdmin.isOpen()).thenReturn(true);

        komunikator.novyHrac(mockConnAdmin);
        admin = komunikator.getAdmin();
    }

    @Test
    public void testPridejBotaDoLobby() {
        boolean pridano = komunikator.pridejBota("Bot Radek");
        assertTrue(pridano, "Bot by měl být úspěšně přidán do lobby");

        List<Hrac> hraci = komunikator.getHra().getHraci();
        assertEquals(2, hraci.size());

        Hrac botHrac = hraci.get(1);
        assertEquals("Bot Radek", botHrac.getJmeno());
        assertTrue(botHrac.isBot());
    }

    @Test
    public void testPridejBotaPresSpravuProtokolu() {
        komunikator.prislaZprava(mockConnAdmin, "pridejBota:SuperBot");

        List<Hrac> hraci = komunikator.getHra().getHraci();
        assertEquals(2, hraci.size());
        assertEquals("SuperBot", hraci.get(1).getJmeno());
        assertTrue(hraci.get(1).isBot());
    }

    @Test
    public void testBotOdpovidaNaDialogy() throws Exception {
        komunikator.pridejBota("Botík");
        HracImp botHrac = (HracImp) komunikator.getHra().getHraci().get(1);

        HerniBot mockBot = mock(HerniBot.class);
        when(mockBot.pozadavekNaMoznosti(any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture("2"));

        botHrac.setBotInstance(mockBot);

        CompletableFuture<String> odpoved = komunikator.pozadejOVyberMoznosti(
                botHrac, List.of("A", "B", "C"), "Vyber", false
        );

        String vysledek = odpoved.get(2, TimeUnit.SECONDS);
        assertEquals("2", vysledek);
        verify(mockBot).pozadavekNaMoznosti(any(), eq(botHrac), eq(List.of("A", "B", "C")), eq("Vyber"));
    }
}
