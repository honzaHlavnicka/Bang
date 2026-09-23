package cz.honzaa.bang.pluginy.bang;

import cz.honzaa.bang.pluginy.bang.karty.*;
import cz.honzaa.bang.pluginy.bang.postavy.*;
import cz.honzaa.bang.pluginy.bang.zbrane.*;
import cz.honzaa.bang.sdk.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BangMechanicsTest {

    private Hra mockHra;
    private KomunikatorHry mockKomunikator;
    private SpravceTahu mockSpravceTahu;
    private Balicek<Karta> mockBalicek;
    private PravidlaBangu pravidla;

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockKomunikator = mock(KomunikatorHry.class);
        mockSpravceTahu = mock(SpravceTahu.class);
        @SuppressWarnings("unchecked")
        Balicek<Karta> balicek = (Balicek<Karta>) mock(Balicek.class);
        mockBalicek = balicek;

        when(mockHra.getKomunikator()).thenReturn(mockKomunikator);
        when(mockHra.getSpravceTahu()).thenReturn(mockSpravceTahu);
        when(mockHra.getHrajiciHraci()).thenReturn(Collections.emptyList());

        pravidla = new PravidlaBangu(mockHra, false);
        when(mockHra.getHerniPravidla()).thenReturn(pravidla);
    }

    @Test
    public void testBangLimitBeznyHrac() {
        Hrac hrac = mock(Hrac.class);
        when(hrac.getEfekty()).thenReturn(Collections.emptyList());
        when(hrac.getPostava()).thenReturn(JednoduchePostavy.NESMRTELNY_BILL);
        when(hrac.jeNaTahu()).thenReturn(true);
        when(hrac.getKarty()).thenReturn(new ArrayList<>());
        when(hrac.getMaximumZivotu()).thenReturn(4);

        // První Bang v tahu projde
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
        assertTrue(pravidla.UzZahralBang());

        // Druhý Bang v témže tahu neprojde
        assertFalse(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));

        // Hráč ukončí tah -> limit se resetuje
        boolean ukoncen = pravidla.hracChceUkoncitTah(hrac);
        assertTrue(ukoncen);
        assertFalse(pravidla.UzZahralBang());

        // V dalším tahu může Bang opět zahrát
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
    }

    @Test
    public void testBangLimitSVolcanicem() {
        Hrac hrac = mock(Hrac.class);
        Volcanic volcanic = new Volcanic(mockHra, mockBalicek);
        when(hrac.getEfekty()).thenReturn(List.of(volcanic));
        when(hrac.getPostava()).thenReturn(JednoduchePostavy.NESMRTELNY_BILL);

        // S Volcanicem může střílet neomezeně
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
    }

    @Test
    public void testBangLimitSWillyTheKid() {
        Hrac hrac = mock(Hrac.class);
        when(hrac.getEfekty()).thenReturn(Collections.emptyList());
        when(hrac.getPostava()).thenReturn(JednoduchePostavy.WILLY_THE_KID);

        // Willy the Kid má vestavěnou schopnost hrát neomezeně Bangů
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
        assertTrue(pravidla.pokusZahratKartuDoLimituKaretBang(hrac));
    }

    @Test
    public void testVlastnostiZbrani() {
        Volcanic volcanic = new Volcanic(mockHra, mockBalicek);
        assertEquals(1, volcanic.getVzdalenost());
        assertTrue(volcanic.umoznujeBangBezLimitu());

        Schofield schofield = new Schofield(mockHra, mockBalicek);
        assertEquals(2, schofield.getVzdalenost());
        assertFalse(schofield.umoznujeBangBezLimitu());

        Remington remington = new Remington(mockHra, mockBalicek);
        assertEquals(3, remington.getVzdalenost());
        assertFalse(remington.umoznujeBangBezLimitu());

        RevCarabine carabine = new RevCarabine(mockHra, mockBalicek);
        assertEquals(4, carabine.getVzdalenost());
        assertFalse(carabine.umoznujeBangBezLimitu());

        Winchester winchester = new Winchester(mockHra, mockBalicek);
        assertEquals(5, winchester.getVzdalenost());
        assertFalse(winchester.umoznujeBangBezLimitu());
    }

    @Test
    public void testVezeniNemuzeBytVylozenoPredSerifaAniPredSebe() {
        Vezeni vezeni = new Vezeni(mockHra, mockBalicek);

        Hrac hrac = mock(Hrac.class);
        when(hrac.getRole()).thenReturn(Role.BANDITA);

        Hrac serif = mock(Hrac.class);
        when(serif.getRole()).thenReturn(Role.SERIF);

        Hrac bandita2 = mock(Hrac.class);
        when(bandita2.getRole()).thenReturn(Role.BANDITA);

        // Nelze vyložit na šerifa
        assertFalse(vezeni.vylozit(serif, hrac));

        // Nelze vyložit sám na sebe
        assertFalse(vezeni.vylozit(hrac, hrac));

        // Lze vyložit na jiného hráče (ne šerifa)
        assertTrue(vezeni.vylozit(bandita2, hrac));
    }

    @Test
    public void testPivoFungovani() {
        Hrac hrac = mock(Hrac.class);
        Pivo pivo = new Pivo(mockHra, mockBalicek);

        // 1. Ve více než 2 hráčích pivo přidá život
        when(mockHra.getHrajiciHraci()).thenReturn(List.of(mock(Hrac.class), mock(Hrac.class), mock(Hrac.class)));
        assertTrue(pivo.odehrat(hrac));
        verify(hrac, times(1)).pridejZivot();

        // 2. Ve 2 hráčích ve standardní variantě pivo nefunguje
        when(mockHra.getHrajiciHraci()).thenReturn(List.of(mock(Hrac.class), mock(Hrac.class)));
        assertTrue(pivo.odehrat(hrac));
        verify(mockKomunikator).posliRychleOznameniVsem("Pivo nefunguje", null);
        verify(hrac, times(1)).pridejZivot(); // stále jen 1 z předchozího volání

        // 3. Ve 2 hráčích ve variantě bez limitu na piva pivo FUNGUJE
        PravidlaBangu pravidlaBezLimitu = new PravidlaBangu(mockHra, true);
        when(mockHra.getHerniPravidla()).thenReturn(pravidlaBezLimitu);

        assertTrue(pivo.odehrat(hrac));
        verify(hrac, times(2)).pridejZivot();
    }

    @Test
    public void testKartyLizuDostavnikAWellsFargo() {
        Hrac hrac = mock(Hrac.class);

        Dostavnik dostavnik = new Dostavnik(mockHra, mockBalicek);
        assertTrue(dostavnik.odehrat(hrac));
        verify(hrac, times(2)).lizni();

        WellsFargo wellsFargo = new WellsFargo(mockHra, mockBalicek);
        assertTrue(wellsFargo.odehrat(hrac));
        verify(hrac, times(5)).lizni(); // 2 + 3 = 5
    }

    @Test
    public void testSalonLeciVsechnyHrace() {
        Hrac h1 = mock(Hrac.class);
        Hrac h2 = mock(Hrac.class);
        when(mockHra.getHrajiciHraci()).thenReturn(List.of(h1, h2));

        Salon salon = new Salon(mockHra, mockBalicek);
        assertTrue(salon.odehrat(h1));

        verify(h1).pridejZivot();
        verify(h2).pridejZivot();
    }

    @Test
    public void testSchopnostiPostav() {
        // Paul Regret má odstup +1
        PaulRegret paul = new PaulRegret();
        assertEquals(1, paul.getBonusOdstupu());

        // Rose Doolan má dosah +1
        RoseDoolan rose = new RoseDoolan();
        assertEquals(1, rose.getBonusDosahu());

        // Bart Cassidy lízne kartu po ztrátě života
        BartCassidy bart = new BartCassidy();
        Hrac bartHrac = mock(Hrac.class);
        bart.poZtrateZivota(mockHra, bartHrac);
        verify(bartHrac).lizni();

        // Suzy Lafayette si lízne, když nemá karty v ruce
        Hrac suzyHrac = mock(Hrac.class);
        when(suzyHrac.getPostava()).thenReturn(JednoduchePostavy.SUZY_LAFAYTTE);
        when(suzyHrac.getKarty()).thenReturn(Collections.emptyList());

        assertTrue(pravidla.hracChceLiznout(suzyHrac));
        verify(suzyHrac).lizni();

        // Když karty má, volné lízání neprojde
        when(suzyHrac.getKarty()).thenReturn(List.of(mock(Karta.class)));
        assertFalse(pravidla.hracChceLiznout(suzyHrac));
    }

    @Test
    public void testMustangAHlediBonusy() {
        Mustang mustang = new Mustang(mockHra, mockBalicek);
        assertEquals(1, mustang.getEfekt().getBonusOdstupu());
        assertEquals(0, mustang.getEfekt().getBonusDosahu());

        Hledi hledi = new Hledi(mockHra, mockBalicek);
        assertEquals(1, hledi.getEfekt().getBonusDosahu());
        assertEquals(0, hledi.getEfekt().getBonusOdstupu());
    }
}
