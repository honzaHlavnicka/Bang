package cz.honzaa.bang.net;

import cz.honzaa.bang.HracImp;
import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.*;
import cz.honzaa.bang.sdk.KomunikatorHry.MoznostKolaStesti;
import org.java_websocket.WebSocket;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KomunikatorHryImpTest {

    private KomunikatorHryImp komunikator;
    private SocketServer mockServer;
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

    private String extractToken(WebSocket conn) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeastOnce()).send(captor.capture());
        return captor.getAllValues().stream()
                .filter(z -> z.startsWith("token:"))
                .findFirst()
                .map(z -> z.substring(12)) // "token:" (6) + "123456" (6)
                .orElseThrow(() -> new AssertionError("Token nebyl nalezen v odeslaných zprávách"));
    }

    private List<String> getAllSentMessages(WebSocket conn) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(conn, atLeast(0)).send(captor.capture());
        return captor.getAllValues();
    }

    @BeforeAll
    public static void initPlugins() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        mockServer = mock(SocketServer.class);
        komunikator = KomunikatorHryImp.vytvor(mockServer, 123456, 0);
        mockConn = mock(WebSocket.class);
        when(mockConn.isOpen()).thenReturn(true);

        komunikator.novyHrac(mockConn);
        hrac1 = komunikator.getAdmin();
    }

    // ==========================================
    // 1. ZÁKLADNÍ FUNKCE KOMUNIKÁTORU A HRÁČŮ
    // ==========================================

    @Test
    @DisplayName("První připojený hráč je nastaven jako admin a obdrží úvodní zprávy")
    public void testNovyHracPrvniJeAdminANastaveni() {
        assertNotNull(hrac1, "První hráč by měl existovat");
        assertEquals(hrac1, komunikator.getAdmin(), "První připojený hráč musí být admin");
        assertEquals(1, komunikator.pocetHracu());
        assertTrue(komunikator.jeHracPripojen(hrac1));

        List<String> messages = getAllSentMessages(mockConn);
        assertTrue(messages.stream().anyMatch(m -> m.equals("pripojenKeHre")), "Hráč musí dostat pripojenKeHre");
        assertTrue(messages.stream().anyMatch(m -> m.startsWith("token:123456")), "Hráč musí dostat token");
        assertTrue(messages.stream().anyMatch(m -> m.startsWith("novyHrac:")), "Hráč musí dostat novyHrac");
    }

    @Test
    @DisplayName("Připojení druhého hráče zachová původního admina a rozešle notifikace")
    public void testNovyHracDalsiHracNezmeniAdmina() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);

        boolean pridano = komunikator.novyHrac(conn2);
        assertTrue(pridano);
        assertEquals(2, komunikator.pocetHracu());
        assertEquals(hrac1, komunikator.getAdmin(), "Admin se připojením dalšího hráče nesmí změnit");

        List<String> messagesConn2 = getAllSentMessages(conn2);
        assertTrue(messagesConn2.stream().anyMatch(m -> m.equals("pripojenKeHre")));

        List<String> messagesConn1 = getAllSentMessages(mockConn);
        long novyHracMessages = messagesConn1.stream().filter(m -> m.startsWith("novyHrac:")).count();
        assertTrue(novyHracMessages >= 2, "Hráč 1 by měl obdržet oznámení o obou hráčích");
    }

    @Test
    @DisplayName("Server odmítne 31. hráče chybou o plném serveru")
    public void testNovyHracLimit30Hracu() {
        for (int i = 2; i <= 30; i++) {
            WebSocket conn = mock(WebSocket.class);
            when(conn.isOpen()).thenReturn(true);
            assertTrue(komunikator.novyHrac(conn), "Hráč " + i + " by měl být úspěšně přidán");
        }
        assertEquals(30, komunikator.pocetHracu());

        WebSocket conn31 = mock(WebSocket.class);
        when(conn31.isOpen()).thenReturn(true);
        boolean pridano = komunikator.novyHrac(conn31);
        assertFalse(pridano, "31. hráč nesmí být přidán");
        verify(conn31).send("error:{\"error\":\"server je plný.\"}");
    }

    @Test
    @DisplayName("Nový hráč se nemůže připojit do již zahájené hry")
    public void testNovyHracKdyzJeHraZahajena() {
        komunikator.getHra().setZahajena(true);

        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        boolean pridano = komunikator.novyHrac(conn2);
        assertFalse(pridano);
        verify(conn2).send(contains("tato hra už byla zahájena"));
    }

    @Test
    @DisplayName("Po odpojení admina přechází role admina na dalšího připojeného hráče")
    public void testHracOdpojenAAktualizaceAdmina() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);

        HracImp hrac2 = (HracImp) komunikator.getHra().getHraci().get(1);

        // Hráč 1 (admin) se odpojí
        komunikator.hracOdpojen(mockConn);

        assertFalse(komunikator.jeHracPripojen(hrac1));
        assertTrue(komunikator.jeHracPripojen(hrac2));
        assertEquals(hrac2, komunikator.getAdmin(), "Novým adminem se musí stát připojený hráč 2");

        List<String> conn2Messages = getAllSentMessages(conn2);
        assertTrue(conn2Messages.stream().anyMatch(m -> m.equals("odpojeniHrace:" + hrac1.getId())));
    }

    @Test
    @DisplayName("Návrat hráče (reconnect) se správným tokenem obnoví spojení")
    public void testVraciSeHracPlatnyANeplatnyToken() {
        String token = extractToken(mockConn);
        assertTrue(komunikator.jeTokenHracePlatny(token));

        WebSocket wrongConn = mock(WebSocket.class);
        when(wrongConn.isOpen()).thenReturn(true);
        assertFalse(komunikator.vraciSeHrac(wrongConn, "neplatnyTokenXYZ"));
        verify(wrongConn).send(contains("hráč v této hře nenalezen"));

        // Úspěšný reconnect s novým spojením
        WebSocket newConn = mock(WebSocket.class);
        when(newConn.isOpen()).thenReturn(true);
        boolean reconnectOk = komunikator.vraciSeHrac(newConn, token);
        assertTrue(reconnectOk);

        verify(mockConn).send(contains("\"kod\":" + Chyba.PRIPOJEN_V_JINE_ZALOZCE.getKod()));
        verify(mockConn).close(); // Staré spojení musí být uzavřeno
        assertTrue(komunikator.jeHracPripojen(hrac1));

        List<String> newMessages = getAllSentMessages(newConn);
        assertTrue(newMessages.stream().anyMatch(m -> m.equals("pripojenKeHre")));
        assertTrue(newMessages.stream().anyMatch(m -> m.equals("pripojeniHrace:" + hrac1.getId())));
    }

    @Test
    @DisplayName("Vracející se hráč do zahájené hry obdrží zprávu hraZacala")
    public void testVraciSeHracPriZahajeneHre() {
        String token = extractToken(mockConn);
        komunikator.getHra().setZahajena(true);

        WebSocket newConn = mock(WebSocket.class);
        when(newConn.isOpen()).thenReturn(true);
        assertTrue(komunikator.vraciSeHrac(newConn, token));

        List<String> newMessages = getAllSentMessages(newConn);
        assertTrue(newMessages.stream().anyMatch(m -> m.equals("hraZacala")));
    }

    // ==========================================
    // 2. POSÍLÁNÍ ZPRÁV A NOTIFIKACÍ
    // ==========================================

    @Test
    @DisplayName("posliVsem a posliVsem s výjimkou doručí zprávy správným příjemcům")
    public void testPosliVsemAPosliVsemSVyjimkou() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);

        komunikator.posliVsem("zpravaProVsechny");
        verify(mockConn).send("zpravaProVsechny");
        verify(conn2).send("zpravaProVsechny");

        komunikator.posliVsem("zpravaBezJednicky", hrac1);
        verify(conn2).send("zpravaBezJednicky");
        verify(mockConn, never()).send("zpravaBezJednicky");
    }

    @Test
    @DisplayName("posli odpojenému hráči bezpečně ignoruje odeslání bez výjimky")
    public void testPosliOdpojenemuHracoviNezpusobiChybu() {
        komunikator.hracOdpojen(mockConn);
        assertDoesNotThrow(() -> komunikator.posli(hrac1, "zpravaProOdpojeneho"));
    }

    @Test
    @DisplayName("posliChybu pošle správně naformátovaný JSON s kódem i skupinou")
    public void testPosliChybuFormat() {
        komunikator.posliChybu(hrac1, Chyba.NEJSI_ADMIN_HRY);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());

        String errorMsg = captor.getAllValues().stream()
                .filter(m -> m.startsWith("error:"))
                .reduce((first, second) -> second)
                .orElseThrow();

        JSONObject json = new JSONObject(errorMsg.replace("error:", ""));
        assertEquals(Chyba.NEJSI_ADMIN_HRY.getKod(), json.getInt("kod"));
        assertEquals(Chyba.NEJSI_ADMIN_HRY.getSkupina(), json.getInt("skupina"));
        assertEquals(Chyba.NEJSI_ADMIN_HRY.getZprava(), json.getString("error"));

        // Vlastní chybový text
        komunikator.posliChybu(hrac1, Chyba.CHYBA_PROTOKOLU, "Vlastní text chyby");
        verify(mockConn).send(contains("Vlastní text chyby"));
    }

    @Test
    @DisplayName("Stavové a herní notifikace správně posílají data klientům")
    public void testPosliNotifikaceAStavy() {
        komunikator.posliStavovouZpravu("Zpráva\ns novým řádkem\r");
        verify(mockConn).send("stavHry:Zpráva s novým řádkem");

        komunikator.posliRychleOznameni("Rychlé info", hrac1);
        verify(mockConn).send("rychleOznameni:Rychlé info");

        komunikator.posliZmenuPoctuZivotu(hrac1);
        verify(mockConn).send("pocetZivotu:" + hrac1.getId() + "," + hrac1.getZivoty());

        komunikator.posliZmenuPoctuKaret(hrac1);
        verify(mockConn, never()).send("zmenaPoctuKaret:" + hrac1.getId() + ",0"); // komuNe je sám hráč

        komunikator.posliZahajeniTahu(hrac1);
        verify(mockConn, never()).send("tahZacal:" + hrac1.getId()); // komuNe je sám hráč

        komunikator.posliZmenuJmena(hrac1);
        verify(mockConn).send("noveJmeno:" + hrac1.getId() + "," + hrac1.getJmeno());

        komunikator.posliSkonceniHrace(hrac1);
        verify(mockConn).send("hracSkoncil:" + hrac1.getId());

        komunikator.posliVitezstvi(hrac1);
        verify(mockConn).send("vyhral:" + hrac1.getId());

        komunikator.posliZadniObrazekLizacihoBalicku("zadni_strana.png");
        verify(mockConn).send("obrazekDobiracihoBalicku:zadni_strana.png");
    }

    @Test
    @DisplayName("posliKonecHry je idempotentní a odešle se pouze jednou")
    public void testPosliKonecHryIdempotentni() {
        komunikator.posliKonecHry();
        komunikator.posliKonecHry();

        List<String> messages = getAllSentMessages(mockConn);
        long count = messages.stream().filter(m -> m.equals("konecHry")).count();
        assertEquals(1, count, "Zpráva konecHry smí být odeslána pouze jednou");
    }

    @Test
    @DisplayName("posliKoloStesti serializuje možnosti a vybranou možnost do JSON")
    public void testPosliKoloStesti() {
        List<MoznostKolaStesti> moznosti = List.of(
                new MoznostKolaStesti("Vyhra", "#00ff00", 1, 5),
                new MoznostKolaStesti("Prohra", "#ff0000", 2, 2)
        );
        komunikator.posliKoloStesti(1, "Kolo štěstí", moznosti);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());

        String koloMsg = captor.getAllValues().stream()
                .filter(m -> m.startsWith("koloStesti:"))
                .findFirst()
                .orElseThrow();

        JSONObject json = new JSONObject(koloMsg.replace("koloStesti:", ""));
        assertEquals("Kolo štěstí", json.getString("nadpis"));
        assertEquals(1, json.getInt("vybranaMoznost"));
        assertEquals(2, json.getJSONArray("moznosti").length());
    }

    @Test
    @DisplayName("posliVysledky rozešle matici pořadí hráčů")
    public void testPosliVysledky() {
        Hrac[][] vysledky = new Hrac[][]{{hrac1}};
        komunikator.posliVysledky(vysledky);
        verify(mockConn).send("vysledkyHry:[[" + hrac1.getId() + "]]");
    }

    @Test
    @DisplayName("Manipulace s kartami posílá korektní zprávy protokolu")
    public void testPosliManipulaceSKartami() {
        DummyKarta karta = new DummyKarta("Bum");

        komunikator.posliNovouKartu(hrac1, karta);
        verify(mockConn).send(startsWith("novaKarta:"));

        komunikator.posliOdebraniKarty(hrac1, karta);
        verify(mockConn).send(startsWith("odehrat:" + hrac1.getId() + "|"));

        komunikator.posliSpaleniKarty(hrac1, karta);
        verify(mockConn).send(startsWith("spalit:" + hrac1.getId() + "|"));

        komunikator.posliVylozeniKarty(hrac1, null, karta);
        verify(mockConn).send(startsWith("vylozeni:" + hrac1.getId() + "," + hrac1.getId() + ","));

        komunikator.posliSpaleniVylozenéKarty(karta, hrac1);
        verify(mockConn).send("spalenaVylozena:" + karta.getId() + "," + hrac1.getId());
    }

    // ==========================================
    // 3. DIALOGY A ČEKÁNÍ NA ODPOVĚĎ
    // ==========================================

    @Test
    @DisplayName("pozadejOVyberMoznosti pošle JSON výběru a po dialog zprávě dokončí future")
    public void testPozadejOVyberMoznostiADialogOdpoved() throws Exception {
        CompletableFuture<String> future = komunikator.pozadejOVyberMoznosti(hrac1, List.of("Volba A", "Volba B"), "Vyber volbu", false);
        assertFalse(future.isDone());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());

        String vyberAkciMsg = captor.getAllValues().stream()
                .filter(m -> m.startsWith("vyberAkci:"))
                .findFirst()
                .orElseThrow();

        JSONObject json = new JSONObject(vyberAkciMsg.replace("vyberAkci:", ""));
        int dialogId = json.getInt("id");

        // Klient odpoví přes protokol
        komunikator.prislaZprava(mockConn, "dialog:" + dialogId + ",1");

        assertTrue(future.isDone());
        assertEquals("1", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("pozadejOText pošle JSON a po dialog zprávě dokončí future textem")
    public void testPozadejOTextADialogOdpoved() throws Exception {
        CompletableFuture<String> future = komunikator.pozadejOText(hrac1, "Zadej jméno", "Sem piš", "Potvrdit", false);
        assertFalse(future.isDone());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());

        String vyberTextMsg = captor.getAllValues().stream()
                .filter(m -> m.startsWith("vyberText:"))
                .findFirst()
                .orElseThrow();

        JSONObject json = new JSONObject(vyberTextMsg.replace("vyberText:", ""));
        int dialogId = json.getInt("id");

        komunikator.prislaZprava(mockConn, "dialog:" + dialogId + ",Pavel");

        assertTrue(future.isDone());
        assertEquals("Pavel", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Neplatný formát zprávy dialog způsobí chybu protokolu")
    public void testProtokolDialogNeplatnyFormat() {
        komunikator.prislaZprava(mockConn, "dialog:bezCarkyANeplatne");
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.CHYBA_PROTOKOLU.getKod()));
    }

    @Test
    @DisplayName("Neznámé ID dialogu je bezpečně ignorováno bez výjimky")
    public void testProtokolDialogNeznameId() {
        assertDoesNotThrow(() -> komunikator.prislaZprava(mockConn, "dialog:999999,odpoved"));
    }

    // ==========================================
    // 4. VLASTNÍ UI PRVKY (CUSTOM UI BUTTONS)
    // ==========================================

    @Test
    @DisplayName("Životní cyklus vlastního UI tlačítka a vyvolání akce přes uiClick")
    public void testCustomUIButtonLifecycleAClick() {
        AtomicBoolean kliknuto = new AtomicBoolean(false);

        int uiId = komunikator.pridejUIButton(hrac1, 55, "Klikni zde", false, () -> kliknuto.set(true));
        assertEquals(55, uiId);

        List<String> messages = getAllSentMessages(mockConn);
        String noveUIMsg = messages.stream().filter(m -> m.startsWith("noveUI:")).findFirst().orElseThrow();
        JSONObject json = new JSONObject(noveUIMsg.replace("noveUI:", ""));
        assertEquals(55, json.getInt("id"));
        assertEquals("Klikni zde", json.getString("text"));
        assertFalse(json.getBoolean("disabled"));

        // Klient klikne na tlačítko
        komunikator.prislaZprava(mockConn, "uiClick:55");
        assertTrue(kliknuto.get(), "Callback akce tlačítka musel být spuštěn");

        // Opětovné poslání tlačítek (např. po reconnectu)
        komunikator.posliVsechnaUITlacitka(hrac1);
        long noveUICount = getAllSentMessages(mockConn).stream().filter(m -> m.startsWith("noveUI:")).count();
        assertTrue(noveUICount >= 2);

        // Smazání tlačítka
        komunikator.smazatUI(hrac1, 55);
        verify(mockConn).send("odebratUI:55");
    }

    @Test
    @DisplayName("Automatické přidělení ID tlačítka pokud je zadáno 0 nebo záporné")
    public void testCustomUIButtonAutomatickeId() {
        int id1 = komunikator.pridejUIButton(hrac1, 0, "Btn 1", false, () -> {});
        int id2 = komunikator.pridejUIButton(hrac1, -1, "Btn 2", false, () -> {});

        assertTrue(id1 > 0);
        assertTrue(id2 > id1, "Následující automatické ID musí být vyšší");
    }

    @Test
    @DisplayName("Neplatné ID v uiClick vrátí chybu protokolu")
    public void testCustomUIButtonNeplatneId() {
        komunikator.prislaZprava(mockConn, "uiClick:neplatneCislo");
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.CHYBA_PROTOKOLU.getKod()));
    }

    // ==========================================
    // 5. PROTOKOLOVÉ ZPRÁVY (prislaZprava)
    // ==========================================

    @Test
    @DisplayName("Zprávy od nepřipojeného websocketu bezpečně odbaví veřejné požadavky")
    public void testProtokolZpravaOdNeznamehoWebsocketu() {
        WebSocket neznamyConn = mock(WebSocket.class);
        when(neznamyConn.isOpen()).thenReturn(true);

        komunikator.prislaZprava(neznamyConn, "getIdHry");
        verify(neznamyConn).send("setIdHry:123456");

        komunikator.prislaZprava(neznamyConn, "nactiPreklady");
        verify(neznamyConn).send(startsWith("preklady:"));

        komunikator.prislaZprava(neznamyConn, "nactiPreklady:en");
        verify(neznamyConn, atLeast(2)).send(startsWith("preklady:"));

        // Neznámý websocket nesmí způsobit pád při "nactiHru" (nesmí volat nactiHru bez HracImp)
        assertDoesNotThrow(() -> komunikator.prislaZprava(neznamyConn, "nactiHru"));
        verify(neznamyConn, never()).send(startsWith("povoleneUI:"));

        // Registrovaný hráč může načíst hru
        komunikator.prislaZprava(mockConn, "nactiHru");
        verify(mockConn, atLeastOnce()).send(startsWith("povoleneUI:"));

        // Neznámá zpráva je bezpečně ignorována
        komunikator.prislaZprava(neznamyConn, "neznamaZpravaTest");
        verifyNoMoreInteractions(neznamyConn);
    }

    @Test
    @DisplayName("Protokolová zpráva noveJmeno změní jméno hráče a rozešle notifikaci")
    public void testProtokolNoveJmeno() {
        komunikator.prislaZprava(mockConn, "noveJmeno:SheriffDan");
        assertEquals("SheriffDan", hrac1.getJmeno());
        verify(mockConn, atLeastOnce()).send("noveJmeno:" + hrac1.getId() + ",SheriffDan");
    }

    @Test
    @DisplayName("Protokol vyhodHrace: kontrola práv admina, neplatného ID a úspěšného vyhození")
    public void testProtokolVyhodHraceAdminVSCizi() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);
        HracImp hrac2 = (HracImp) komunikator.getHra().getHraci().get(1);

        // 1. Ne-admin zkusí vyhodit admina
        komunikator.prislaZprava(conn2, "vyhodHrace:" + hrac1.getId());
        verify(conn2, atLeastOnce()).send(contains("\"kod\":" + Chyba.NEJSI_ADMIN_HRY.getKod()));
        assertEquals(2, komunikator.pocetHracu());

        // 2. Admin zadá neplatné ID
        komunikator.prislaZprava(mockConn, "vyhodHrace:neplatneId");
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.CHYBA_PROTOKOLU.getKod()));

        // 3. Admin vyhodí hráče 2 před zahájením hry
        komunikator.prislaZprava(mockConn, "vyhodHrace:" + hrac2.getId());
        verify(conn2, atLeastOnce()).send(contains("\"kod\":" + Chyba.VYHOZEN_ZE_HRY.getKod()));
        verify(conn2).close();
        assertEquals(1, komunikator.pocetHracu());
    }

    @Test
    @DisplayName("Admin nemůže vyhodit hráče po zahájení hry")
    public void testProtokolVyhodHracePoZahajeni() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);
        HracImp hrac2 = (HracImp) komunikator.getHra().getHraci().get(1);

        komunikator.getHra().setZahajena(true);

        komunikator.prislaZprava(mockConn, "vyhodHrace:" + hrac2.getId());
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.KARTA_NEJDE_ZAHRAT.getKod()));
        assertEquals(2, komunikator.pocetHracu(), "Hráč nesmí být vyhozen po zahájení");
    }

    @Test
    @DisplayName("Protokol zahajeniHry: pouze admin může zahájit hru")
    public void testProtokolZahajeniHryOpravneni() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);

        // Ne-admin zkusí zahájit hru
        komunikator.prislaZprava(conn2, "zahajeniHry");
        verify(conn2, atLeastOnce()).send(contains("\"kod\":" + Chyba.NEJSI_ADMIN_HRY.getKod()));
        assertFalse(komunikator.getHra().isZahajena());

        // Admin zahájí hru
        komunikator.prislaZprava(mockConn, "zahajeniHry");
        assertTrue(komunikator.getHra().isZahajena());
    }

    @Test
    @DisplayName("Protokol konecTahu pošle chybu pokud hráč nemůže ukončit tah")
    public void testProtokolKonecTahuPokudNemuzes() {
        // Hráč není na tahu
        komunikator.prislaZprava(mockConn, "konecTahu");
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.NEMUZES_UKONCIT_TAH.getKod()));
    }

    @Test
    @DisplayName("Protokol nahradHru: ne-admin je odmítnut a admin korektně rozešle přesměrování")
    public void testProtokolNahradHruOpravneniAFormat() {
        WebSocket conn2 = mock(WebSocket.class);
        when(conn2.isOpen()).thenReturn(true);
        komunikator.novyHrac(conn2);

        // 1. Ne-admin zkusí nahradit hru
        komunikator.prislaZprava(conn2, "nahradHru:0");
        verify(conn2, atLeastOnce()).send(contains("\"kod\":" + Chyba.NEJSI_ADMIN_HRY.getKod()));

        // 2. Admin zadá neplatné ID hry
        komunikator.prislaZprava(mockConn, "nahradHru:spatneId");
        verify(mockConn, atLeastOnce()).send(contains("\"kod\":" + Chyba.CHYBA_PROTOKOLU.getKod()));

        // 3. Admin zadá platné ID nové hry
        KomunikatorHryImp mockNovaHra = mock(KomunikatorHryImp.class);
        when(mockNovaHra.getIdHry()).thenReturn(987654);
        when(mockServer.novaHra(0)).thenReturn(mockNovaHra);

        komunikator.prislaZprava(mockConn, "nahradHru:0");
        verify(mockConn).send(startsWith("nahradHruBezPtani:987654,"));
        verify(conn2).send(startsWith("nahradHru:987654,"));
    }

    // ==========================================
    // 6. PŮVODNÍ NULL-SAFETY TESTY
    // ==========================================

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
