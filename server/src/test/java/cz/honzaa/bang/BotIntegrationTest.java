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

    private static int typHrySBoty = -1;
    private static int typHryBezBotu = -1;

    private static class TestHerniPravidla implements HerniPravidla {
        private final boolean podporujeBoty;

        public TestHerniPravidla(boolean podporujeBoty) {
            this.podporujeBoty = podporujeBoty;
        }

        @Override
        public boolean podporujeBoty() {
            return podporujeBoty;
        }

        @Override
        public void poSpusteniHry() {}
        @Override
        public void pripravBalicek(Balicek balicek) {}
        @Override
        public void spustitPrvniTah(SpravceTahu spravceTahu) {}
        @Override
        public boolean hracChceLiznout(Hrac hrac) { return false; }
        @Override
        public boolean hracChceUkoncitTah(Hrac hrac) { return true; }
        @Override
        public boolean muzeZahrat(Karta karta, Hrac hrac) { return false; }
        @Override
        public void pripravitHrace(Hrac hrac) {}
        @Override
        public void poOdehrani(Hrac kym) {}
        @Override
        public void dosliZivoty(Hrac komu) {}
    }

    private static class TestPlugin implements HerniPlugin {
        private final String jmeno;
        private final boolean podporujeBoty;

        public TestPlugin(String jmeno, boolean podporujeBoty) {
            this.jmeno = jmeno;
            this.podporujeBoty = podporujeBoty;
        }

        @Override
        public String getJmeno() { return jmeno; }
        @Override
        public String getPopis() { return jmeno; }
        @Override
        public String getURLPravidel() { return ""; }
        @Override
        public HerniPravidla vytvor(Hra hra) {
            return new TestHerniPravidla(podporujeBoty);
        }
    }

    @BeforeAll
    public static void initPlugins() {
        SpravceHernichPravidel.pregeneruj();
        for (int i = 0; SpravceHernichPravidel.getPlugin(i) != null; i++) {
            HraImp testHra = HraImp.vytvor(null, i);
            if (testHra.getHerniPravidla() != null) {
                if (testHra.getHerniPravidla().podporujeBoty() && typHrySBoty == -1) {
                    typHrySBoty = i;
                } else if (!testHra.getHerniPravidla().podporujeBoty() && typHryBezBotu == -1) {
                    typHryBezBotu = i;
                }
            }
        }
        if (typHrySBoty == -1) {
            typHrySBoty = SpravceHernichPravidel.pridejPlugin(new TestPlugin("TestSBoty", true));
        }
        if (typHryBezBotu == -1) {
            typHryBezBotu = SpravceHernichPravidel.pridejPlugin(new TestPlugin("TestBezBotu", false));
        }
    }

    @BeforeEach
    public void setUp() {
        mockServer = mock(SocketServer.class);
        komunikator = KomunikatorHryImp.vytvor(mockServer, 123456, typHrySBoty);
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

    @Test
    public void testPridejBotaKdyzHraNepodporujeBoty() {
        KomunikatorHryImp komBezBotu = KomunikatorHryImp.vytvor(mockServer, 999999, typHryBezBotu);
        WebSocket mockConn = mock(WebSocket.class);
        when(mockConn.isOpen()).thenReturn(true);
        komBezBotu.novyHrac(mockConn);

        // Volání přes metodu komunikátoru
        assertFalse(komBezBotu.pridejBota("BotVTestu"), "Bot by neměl jít přidat do hry, která nepodporuje boty");

        // Volání přes protokolovou zprávu
        komBezBotu.prislaZprava(mockConn, "pridejBota:BotVTestu");
        assertEquals(1, komBezBotu.getHra().getHraci().size(), "V lobby by měl zůstat pouze 1 hráč");
        verify(mockConn).send(contains("hra_nepodporuje_boty"));
    }
}
