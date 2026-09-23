package cz.honzaa.bang;

import cz.honzaa.bang.net.KomunikatorHryImp;
import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.*;
import org.java_websocket.WebSocket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HraImpTest {

    private KomunikatorHryImp mockKomunikator;

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
            return "zezadu";
        }
    }

    @BeforeAll
    public static void initPlugins() {
        SpravceHernichPravidel.pregeneruj();
    }

    @BeforeEach
    public void setUp() {
        mockKomunikator = mock(KomunikatorHryImp.class);
        when(mockKomunikator.getIdHry()).thenReturn(111);
    }

    @Test
    public void testVytvoreniHryAInicializaceBalicku() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);

        assertNotNull(hra.getPlugin(), "Herní plugin nesmí být null");
        assertNotNull(hra.getHerniPravidla(), "Herní pravidla nesmí být null");
        assertNotNull(hra.getBalicek(), "Lízací balíček nesmí být null");
        assertNotNull(hra.getOdhazovaciBalicek(), "Odhazovací balíček nesmí být null");

        // Pravidla hry naplní balíček kartami
        assertFalse(hra.getBalicek().jePrazdny(), "Lízací balíček po vytvoření hry nesmí být prázdný");
        assertTrue(hra.getBalicek().pocet() >= 32, "Balíček by měl mít alespoň 32 karet, nalezeno: " + hra.getBalicek().pocet());
    }

    @Test
    public void testPridaniHracuAVyberPostav() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);

        HracImp hrac1 = hra.novyHrac();
        HracImp hrac2 = hra.novyHrac();

        assertNotNull(hrac1);
        assertNotNull(hrac2);
        assertNotEquals(hrac1.getId(), hrac2.getId(), "Každý hráč musí mít unikátní ID");

        // hracVytvoren rozdá 2 postavy na výběr
        hra.hracVytvoren(hrac1);
        hra.hracVytvoren(hrac2);

        // Po zahájení hry musí mít hráč postavu zajištěnou
        hrac1.zajistiPostavu();
        assertNotNull(hrac1.getPostava(), "Hráč 1 musí mít přiřazenu postavu");
    }

    @Test
    public void testZahajeniHryJednouANemennostOpakovanehoVolani() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);

        HracImp h1 = hra.novyHrac();
        h1.setJmeno("Hrac 1");
        hra.hracVytvoren(h1);

        HracImp h2 = hra.novyHrac();
        h2.setJmeno("Hrac 2");
        hra.hracVytvoren(h2);

        assertFalse(hra.isZahajena());

        hra.setZahajena(true);
        assertTrue(hra.isZahajena());
        assertNotNull(hra.getSpravceTahu(), "SpravceTahu musí být po zahájení hry inicializován");

        verify(mockKomunikator, times(1)).posliZahajeniHry();

        // Opakované volání setZahajena nesmí znovu volat posliZahajeniHry
        hra.setZahajena(true);
        verify(mockKomunikator, times(1)).posliZahajeniHry();
    }

    @Test
    public void testProhodBalicky() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);

        // Vyprázdníme balíčky
        while (!hra.getBalicek().jePrazdny()) {
            hra.getBalicek().lizni();
        }

        DummyKarta k1 = new DummyKarta("Karta 1");
        DummyKarta k2 = new DummyKarta("Karta 2");
        DummyKarta k3 = new DummyKarta("Karta 3");

        // Vložíme do odhazovacího balíčku: k1 naspod, pak k2, pak k3 nahoře
        hra.getOdhazovaciBalicek().vratNahoru(k1);
        hra.getOdhazovaciBalicek().vratNahoru(k2);
        hra.getOdhazovaciBalicek().vratNahoru(k3);

        assertEquals(0, hra.getBalicek().pocet());
        assertEquals(3, hra.getOdhazovaciBalicek().pocet());

        // Prohodíme balíčky: odhazovací se otočí a stane se lízacím
        hra.prohodBalicky();

        assertEquals(3, hra.getBalicek().pocet());
        assertEquals(0, hra.getOdhazovaciBalicek().pocet());

        // Pořadí lízání z nového balíčku: k1, k2, k3
        assertEquals(k1, hra.getBalicek().lizni());
        assertEquals(k2, hra.getBalicek().lizni());
        assertEquals(k3, hra.getBalicek().lizni());
    }

    @Test
    public void testOtocVrchniKartu() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);

        DummyKarta k1 = new DummyKarta("Dynamit");
        hra.getBalicek().vratNahoru(k1);

        Karta otocena = hra.otocVrchniKartu();

        assertEquals(k1, otocena);
        assertEquals(k1, hra.getOdhazovaciBalicek().nahledni(), "Otočená karta musí skončit na odhazovacím balíčku");

        // Musí být rozeslána zpráva odehrat:-1
        verify(mockKomunikator).posliVsem(contains("odehrat:-1|"));
    }

    @Test
    public void testSkoncilAVyhralHrac() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);
        HracImp h1 = hra.novyHrac();
        HracImp h2 = hra.novyHrac();
        hra.hracVytvoren(h1);
        hra.hracVytvoren(h2);
        hra.setZahajena(true);

        hra.skoncil(h1);
        verify(mockKomunikator).posliSkonceniHrace(h1);
        assertFalse(hra.getHrajiciHraci().contains(h1), "Hráč 1 byl vyřazen z hrajících hráčů");

        hra.vyhral(h2);
        verify(mockKomunikator).posliVitezstvi(h2);
    }

    @Test
    public void testNactiHruPosleVsechnaPotrebnaData() {
        HraImp hra = HraImp.vytvor(mockKomunikator, 0);
        HracImp h1 = hra.novyHrac();
        h1.setJmeno("Pepa");
        hra.hracVytvoren(h1);
        hra.setZahajena(true);

        WebSocket mockConn = mock(WebSocket.class);
        hra.nactiHru(mockConn, h1);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockConn, atLeastOnce()).send(captor.capture());

        List<String> zpravy = captor.getAllValues();
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("noveIdHrace:" + h1.getId())));
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("setIdHry:111")));
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("hraci:[") && z.contains("Pepa")));
        assertTrue(zpravy.stream().anyMatch(z -> z.startsWith("povoleneUI:")));
    }
}
