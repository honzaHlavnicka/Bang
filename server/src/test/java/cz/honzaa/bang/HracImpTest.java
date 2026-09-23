package cz.honzaa.bang;

import cz.honzaa.bang.sdk.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HracImpTest {

    private Hra mockHra;
    private KomunikatorHry mockKomunikator;
    private HerniPravidla mockPravidla;
    private SpravceTahu mockSpravceTahu;
    private BalicekImp<Karta> balicek;
    private BalicekImp<Karta> odhazovaciBalicek;

    private HracImp hrac;

    private static class TestKarta extends Karta implements VylozitelnaKarta {
        private final String jmeno;
        private final Efekt efekt;
        public boolean spalena = false;

        public TestKarta(String jmeno, Efekt efekt) {
            super(null, null);
            this.jmeno = jmeno;
            this.efekt = efekt;
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
        public Efekt getEfekt() {
            return efekt;
        }

        @Override
        public boolean vylozit(Hrac predKoho, Hrac kym) {
            return true;
        }

        @Override
        public void spalitVylozenou() {
            this.spalena = true;
        }
    }

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockKomunikator = mock(KomunikatorHry.class);
        mockPravidla = mock(HerniPravidla.class);
        mockSpravceTahu = mock(SpravceTahu.class);

        balicek = new BalicekImp<>();
        odhazovaciBalicek = new BalicekImp<>();

        when(mockHra.getKomunikator()).thenReturn(mockKomunikator);
        when(mockHra.getHerniPravidla()).thenReturn(mockPravidla);
        when(mockHra.getSpravceTahu()).thenReturn(mockSpravceTahu);
        when(mockHra.getBalicek()).thenReturn(balicek);
        when(mockHra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);

        hrac = new HracImp(mockHra);
    }

    @Test
    public void testSanitizaceJmenaProtiProtocolInjection() {
        // Jméno obsahující čárky, nové řádky a \r
        // HracImp: replace(",", " ").replace("\n", " ").replace("\r", "").trim()
        hrac.setJmeno("Pepa, Zdepa\nAdmin\rHack  ");
        assertEquals("Pepa  Zdepa AdminHack", hrac.getJmeno(), "Jméno musí být očištěno od čárek, \\n a \\r");
        verify(mockKomunikator).posliZmenuJmena(hrac);

        // Null jméno se bezpečně převede na prázdný řetězec
        hrac.setJmeno(null);
        assertEquals("", hrac.getJmeno());
    }

    @Test
    public void testZivotyNavyseniAZastropovani() {
        hrac.setMaximumZivotu(4);
        hrac.setZivoty(3);

        // Navýšení na 4 (maximum)
        assertTrue(hrac.pridejZivot());
        assertEquals(4, hrac.getZivoty());
        verify(mockKomunikator, atLeastOnce()).posliZmenuPoctuZivotu(hrac);

        // Další navýšení nesmí překročit maximum
        assertFalse(hrac.pridejZivot());
        assertEquals(4, hrac.getZivoty(), "Životy nesmí překročit maximumZivotu");
    }

    @Test
    public void testZivotySnizeniASmrt() {
        hrac.setMaximumZivotu(4);
        hrac.setZivoty(1);

        Efekt efekt = mock(Efekt.class);
        hrac.pridejEfekt(efekt);
        when(mockHra.getHraci()).thenReturn(List.of(hrac));

        // Odebrání posledního života způsobí smrt
        boolean prezil = hrac.odeberZivot();
        assertFalse(prezil, "Při 0 životech odeberZivot vrací false");
        assertEquals(0, hrac.getZivoty());
        assertFalse(hrac.jeZivy());

        // Ověříme volání efektů a pravidel
        verify(efekt).poZtrateZivota(mockHra, hrac);
        verify(efekt).poZabitiKohokoliv(hrac, hrac);
        verify(mockPravidla).dosliZivoty(hrac);

        // Opakovaný odeberZivot na mrtvém hráči nezpůsobí záporné životy
        boolean prezilZnovu = hrac.odeberZivot();
        assertFalse(prezilZnovu);
        assertEquals(0, hrac.getZivoty());
    }

    @Test
    public void testLizaniZBeznehoBalicku() {
        TestKarta testKarta = new TestKarta("Bang", null);
        balicek.vratNahoru(testKarta);

        assertEquals(0, hrac.getKarty().size());
        hrac.lizni();

        assertEquals(1, hrac.getKarty().size());
        assertEquals(testKarta, hrac.getKarty().get(0));
        verify(mockKomunikator).posliNovouKartu(hrac, testKarta);
    }

    @Test
    public void testLizaniKdyzJeLizaciBalicekPrazdnyAProhodiSe() {
        TestKarta testKarta = new TestKarta("Panika", null);
        odhazovaciBalicek.vratNahoru(testKarta);

        doAnswer(invocation -> {
            balicek.vratNahoru(testKarta);
            return null;
        }).when(mockHra).prohodBalicky();

        hrac.lizni();

        verify(mockHra).prohodBalicky();
        assertEquals(1, hrac.getKarty().size());
        assertEquals(testKarta, hrac.getKarty().get(0));
    }

    @Test
    public void testLizaniKdyzJsouObaBalickyPrazdneVyhodiChybu() {
        assertTrue(balicek.jePrazdny());
        assertTrue(odhazovaciBalicek.jePrazdny());

        hrac.lizni();

        verify(mockKomunikator).posliChybu(hrac, Chyba.DOSLI_KARTY_V_BALICKU);
        assertTrue(hrac.getKarty().isEmpty());
    }

    @Test
    public void testFyzickaVzdalenostVKruhu() {
        HracImp p0 = new HracImp(mockHra);
        HracImp p1 = new HracImp(mockHra);
        HracImp p2 = new HracImp(mockHra);
        HracImp p3 = new HracImp(mockHra);
        HracImp p4 = new HracImp(mockHra);

        List<Hrac> kruh = List.of(p0, p1, p2, p3, p4);
        when(mockSpravceTahu.getHrajiciHraci()).thenReturn(kruh);

        // p0 sousedí s p1 a p4 (vzdálenost 1)
        assertEquals(1, p0.fyzickaVzdalenostK(p1));
        assertEquals(1, p0.fyzickaVzdalenostK(p4));

        // p0 má vzdálenost 2 k p2 i p3
        assertEquals(2, p0.fyzickaVzdalenostK(p2));
        assertEquals(2, p0.fyzickaVzdalenostK(p3));

        // Neznámý hráč vyvolá výjimku
        HracImp cizi = new HracImp(mockHra);
        assertThrows(IllegalArgumentException.class, () -> p0.fyzickaVzdalenostK(cizi));
    }

    @Test
    public void testVzdalenostSModifikatoryDosahuAOdstupu() {
        HracImp p0 = new HracImp(mockHra);
        HracImp p1 = new HracImp(mockHra);
        HracImp p2 = new HracImp(mockHra);

        when(mockSpravceTahu.getHrajiciHraci()).thenReturn(List.of(p0, p1, p2));

        // Fyzická vzdálenost p0 k p2 je 1 (v 3 hráčích jsou všichni sousedé)
        assertEquals(1, p0.fyzickaVzdalenostK(p2));

        // Cíl má Mustang (odstup +1)
        Efekt mustang = mock(Efekt.class);
        when(mustang.getBonusOdstupu()).thenReturn(1);
        p2.pridejEfekt(mustang);

        // Vzdálenost se zvýší na 2
        assertEquals(2, p0.vzdalenostK(p2));

        // Útočník má zbraň/hledí (dosah +1)
        Efekt hledi = mock(Efekt.class);
        when(hledi.getBonusDosahu()).thenReturn(1);
        p0.pridejEfekt(hledi);

        // 1 (fyzická) + 1 (odstup) - 1 (dosah) = 1
        assertEquals(1, p0.vzdalenostK(p2));

        // Útočník má obrovský dosah (+10) -> vzdálenost nesmí klesnout pod 1
        when(hledi.getBonusDosahu()).thenReturn(10);
        assertEquals(1, p0.vzdalenostK(p2), "Vzdálenost nesmí být menší než 1");
    }

    @Test
    public void testVylozeneKartyPridaniAOdebrani() {
        Efekt efekt = mock(Efekt.class);
        TestKarta karta = new TestKarta("Barel", efekt);

        hrac.pridejVylozenouKartu(karta, hrac);

        assertEquals(1, hrac.getVylozeneKarty().size());
        assertTrue(hrac.getEfekty().contains(efekt));
        verify(efekt).prirazeni(hrac);

        hrac.odeberVylozenouKartu(karta);

        assertEquals(0, hrac.getVylozeneKarty().size());
        assertFalse(hrac.getEfekty().contains(efekt));
        verify(efekt).odebrani(hrac);
        assertTrue(karta.spalena);
    }

    @Test
    public void testToJSONFormat() {
        hrac.setJmeno("Sheriff \"Bob\"");
        hrac.setMaximumZivotu(5);
        hrac.setZivoty(4);

        String json = hrac.toJSON();
        assertNotNull(json);
        assertTrue(json.contains("\"jmeno\":\"Sheriff \\\"Bob\\\"\""), "Uvozovky ve jméně musí být správně escapovány: " + json);
        assertTrue(json.contains("\"zivoty\":4"));
        assertTrue(json.contains("\"maximumZivotu\":5"));
        assertTrue(json.contains("\"pocetKaret\":0"));
    }
}
