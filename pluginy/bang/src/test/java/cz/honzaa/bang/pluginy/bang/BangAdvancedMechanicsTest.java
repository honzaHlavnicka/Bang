package cz.honzaa.bang.pluginy.bang;

import cz.honzaa.bang.pluginy.bang.karty.Hokynarstvi;
import cz.honzaa.bang.pluginy.bang.karty.Indiani;
import cz.honzaa.bang.pluginy.bang.karty.Kulomet;
import cz.honzaa.bang.pluginy.bang.karty.Vezeni;
import cz.honzaa.bang.pluginy.bang.postavy.*;
import cz.honzaa.bang.sdk.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BangAdvancedMechanicsTest {

    private Hra hra;
    private SpravceTahu spravceTahu;
    private KomunikatorHry komunikator;
    private Balicek<Karta> odhazovaciBalicek;
    private Balicek<Karta> lizaciBalicek;
    private PravidlaBangu pravidla;

    private static class DummyKarta extends Karta {
        public DummyKarta() {
            super(null, null);
        }

        @Override
        public String getObrazek() {
            return "dummy";
        }

        @Override
        public String getJmeno() {
            return "Dummy";
        }

        @Override
        public String getZadniObrazek() {
            return "rub";
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        hra = Mockito.mock(Hra.class);
        spravceTahu = Mockito.mock(SpravceTahu.class);
        komunikator = Mockito.mock(KomunikatorHry.class);
        odhazovaciBalicek = Mockito.mock(Balicek.class);
        lizaciBalicek = Mockito.mock(Balicek.class);

        when(hra.getSpravceTahu()).thenReturn(spravceTahu);
        when(hra.getKomunikator()).thenReturn(komunikator);
        when(hra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);
        when(hra.getBalicek()).thenReturn(lizaciBalicek);

        pravidla = Mockito.spy(new PravidlaBangu(hra, false)); // standardní hra s omezeným počtem karet
        when(hra.getHerniPravidla()).thenReturn(pravidla);
    }

    @Test
    @DisplayName("hracChceUkoncitTah blokuje konec tahu, pokud má hráč více karet než je maximum životů")
    public void testLimitKaretVrucePodleZivotu() {
        Hrac hrac = Mockito.mock(Hrac.class);
        when(hrac.jeNaTahu()).thenReturn(true);
        when(hrac.getMaximumZivotu()).thenReturn(3);

        List<Karta> karty = new ArrayList<>(List.of(new DummyKarta(), new DummyKarta(), new DummyKarta(), new DummyKarta()));
        when(hrac.getKarty()).thenReturn(karty);

        // 4 karty > 3 životy -> nelze ukončit tah
        assertFalse(pravidla.hracChceUkoncitTah(hrac), "Hráč s více kartami než životy nesmí ukončit tah");
        verify(komunikator).posliRychleOznameni(eq("Moc karet"), eq(hrac));
        verify(hrac, never()).konecTahu();

        // Po odhození 1 karty (3 karty <= 3 životy)
        karty.remove(0);
        assertTrue(pravidla.hracChceUkoncitTah(hrac), "Hráč s kartami <= životy smí ukončit tah");
        verify(hrac).konecTahu();
    }

    @Test
    @DisplayName("Vulture Sam získá všechny karty zemřelého hráče z ruky i ze stolu")
    public void testVultureSamDedeckVsechKaret() {
        Hrac zemrely = Mockito.mock(Hrac.class);
        Hrac vultureSam = Mockito.mock(Hrac.class);

        when(vultureSam.getPostava()).thenReturn(JednoduchePostavy.VULTURE_SAM);
        when(hra.getHrajiciHraci()).thenReturn(List.of(vultureSam));

        List<Karta> kartyVulture = new ArrayList<>();
        when(vultureSam.getKarty()).thenReturn(kartyVulture);

        Karta k1 = new DummyKarta();
        Karta k2 = new DummyKarta();
        List<Karta> kartyZemrely = new ArrayList<>(List.of(k1, k2));
        when(zemrely.getKarty()).thenReturn(kartyZemrely);
        when(zemrely.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(zemrely.getEfekty()).thenReturn(new ArrayList<>());

        pravidla.dosliZivoty(zemrely);

        // Karty byly přesunuty do ruky Vulture Sama
        assertEquals(2, kartyVulture.size());
        assertTrue(kartyVulture.contains(k1));
        assertTrue(kartyVulture.contains(k2));

        // Žádná karta nebyla spálena do odhazovacího balíčku
        verify(odhazovaciBalicek, never()).vratNahoru(any());
    }

    @Test
    @DisplayName("Při smrti hráče bez Vulture Sama se karty spálí do odhazovacího balíčku")
    public void testSpaleniKaretBezVultureSama() {
        Hrac zemrely = Mockito.mock(Hrac.class);
        Hrac jinyHrac = Mockito.mock(Hrac.class);
        when(jinyHrac.getPostava()).thenReturn(JednoduchePostavy.WILLY_THE_KID);
        when(hra.getHrajiciHraci()).thenReturn(List.of(jinyHrac));

        Karta k1 = new DummyKarta();
        when(zemrely.getKarty()).thenReturn(new ArrayList<>(List.of(k1)));
        when(zemrely.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(zemrely.getEfekty()).thenReturn(new ArrayList<>());

        pravidla.dosliZivoty(zemrely);

        verify(odhazovaciBalicek).vratNahoru(k1);
    }

    @Test
    @DisplayName("Wild Will má 3 životy a lízne 3 karty na začátku tahu")
    public void testWildWillLizani() {
        WildWill postava = new WildWill();
        assertEquals(3, postava.getMaximumZivotu());

        Hrac hrac = Mockito.mock(Hrac.class);
        postava.lizniNaZacatkuTahu(hrac, hra);

        verify(hrac, times(3)).lizni();
    }

    @Test
    @DisplayName("Jourdonnais přiřadí hráči efekt barelu")
    public void testJourdonnaisBarelEfekt() {
        Jourdonnais postava = new Jourdonnais();
        assertEquals(4, postava.getMaximumZivotu());

        Hrac hrac = Mockito.mock(Hrac.class);
        List<Efekt> efekty = new ArrayList<>();
        when(hrac.getEfekty()).thenReturn(efekty);

        postava.pridaniPostavy(hrac);
        assertEquals(1, efekty.size(), "Jourdonnais musí přidat 1 efekt (Barel)");

        postava.odebraniPostavy(hrac);
        assertEquals(0, efekty.size(), "Po odebrání postavy musí být efekt odstraněn");
    }

    @Test
    @DisplayName("Indiáni požádají všechny ostatní hráče o kartu Bang nebo odebrání života")
    public void testIndianiZahrani() {
        Hrac kym = Mockito.mock(Hrac.class);
        Hrac hrac1 = Mockito.mock(Hrac.class);
        Hrac hrac2 = Mockito.mock(Hrac.class);

        when(hra.getHrajiciHraci()).thenReturn(List.of(kym, hrac1, hrac2));
        when(hrac1.getKarty()).thenReturn(new ArrayList<>());
        when(hrac2.getKarty()).thenReturn(new ArrayList<>());

        when(komunikator.pozadejOKarty(any(), any(), any(), eq(1), eq(1), eq(false)))
                .thenReturn(new CompletableFuture<>());

        Indiani indiani = new Indiani(hra, lizaciBalicek);
        boolean odehrano = indiani.odehrat(kym);

        assertTrue(odehrano);
        verify(komunikator).pozadejOKarty(eq(hrac1), any(), eq("Vyber o co přijdeš kvůli Indiánům!"), eq(1), eq(1), eq(false));
        verify(komunikator).pozadejOKarty(eq(hrac2), any(), eq("Vyber o co přijdeš kvůli Indiánům!"), eq(1), eq(1), eq(false));
        verify(komunikator, never()).pozadejOKarty(eq(kym), any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Kulomet vyvolá akci Bang proti všem ostatním hráčům")
    public void testKulometZahrani() {
        Hrac kym = Mockito.mock(Hrac.class);
        Hrac souper = Mockito.mock(Hrac.class);

        when(hra.getHrajiciHraci()).thenReturn(List.of(kym, souper));

        doNothing().when(pravidla).vyvolejAkciBang(any(), any(), any());

        Kulomet kulomet = new Kulomet(hra, lizaciBalicek);
        boolean odehrano = kulomet.odehrat(kym);

        assertTrue(odehrano);
        verify(pravidla).vyvolejAkciBang(eq(kym), eq(souper), any());
        verify(pravidla, never()).vyvolejAkciBang(eq(kym), eq(kym), any());
    }

    @Test
    @DisplayName("Vězení na začátku tahu odstraní kartu a vrátí ji do odhazovacího balíčku")
    public void testVezeniNaZacatekTahu() {
        Hrac hrac = Mockito.mock(Hrac.class);
        when(hrac.getJmeno()).thenReturn("TestHrac");
        when(hra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);

        Vezeni vezeni = new Vezeni(hra, lizaciBalicek);
        vezeni.naZacatekTahu(hra, hrac);

        verify(hra).otocVrchniKartu();
        verify(hrac).odeberVylozenouKartu(vezeni);
        verify(odhazovaciBalicek).vratNahoru(vezeni);
        verify(komunikator).posliSpaleniVylozenéKarty(vezeni, hrac);
    }

    @Test
    @DisplayName("Hokynářství funguje i když je lízací i odhazovací balíček prázdný")
    public void testHokynarstviPrazdnyBalicek() {
        Hrac hrac1 = Mockito.mock(Hrac.class);
        when(hrac1.getJmeno()).thenReturn("Hrac1");
        when(hra.getHrajiciHraci()).thenReturn(List.of(hrac1));
        when(hra.getBalicek()).thenReturn(lizaciBalicek);
        when(hra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);
        when(lizaciBalicek.lizni()).thenReturn(null);
        when(odhazovaciBalicek.jePrazdny()).thenReturn(true);

        Hokynarstvi hokynarstvi = new Hokynarstvi(hra, lizaciBalicek);
        boolean vysledek = hokynarstvi.odehrat(hrac1);

        assertTrue(vysledek);
        verify(komunikator, never()).pozadejOKarty(any(), any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Hokynářství rozdá karty a nechá hráče vybrat")
    public void testHokynarstviVyberKarty() {
        Hrac hrac1 = Mockito.mock(Hrac.class);
        when(hrac1.getJmeno()).thenReturn("Hrac1");
        List<Karta> kartyHrace = new ArrayList<>();
        when(hrac1.getKarty()).thenReturn(kartyHrace);

        when(hra.getHrajiciHraci()).thenReturn(List.of(hrac1));
        when(hra.getBalicek()).thenReturn(lizaciBalicek);
        when(hra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);

        DummyKarta k1 = new DummyKarta();
        when(lizaciBalicek.lizni()).thenReturn(k1);
        when(komunikator.pozadejOKarty(eq(hrac1), any(), any(), eq(1), eq(1), eq(false)))
                .thenReturn(CompletableFuture.completedFuture(String.valueOf(k1.getId())));

        Hokynarstvi hokynarstvi = new Hokynarstvi(hra, lizaciBalicek);
        boolean vysledek = hokynarstvi.odehrat(hrac1);

        assertTrue(vysledek);
        verify(komunikator).pozadejOKarty(eq(hrac1), any(), any(), eq(1), eq(1), eq(false));
        assertTrue(kartyHrace.contains(k1));
        verify(komunikator).posliNovouKartu(hrac1, k1);
        verify(komunikator).posliZmenuPoctuKaret(hrac1);
    }
}
