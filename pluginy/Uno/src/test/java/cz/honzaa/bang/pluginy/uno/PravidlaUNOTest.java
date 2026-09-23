package cz.honzaa.bang.pluginy.uno;

import cz.honzaa.bang.sdk.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PravidlaUNOTest {

    private Hra hra;
    private SpravceTahu spravceTahu;
    private Balicek<Karta> odhazovaciBalicek;
    private Balicek<Karta> lizaciBalicek;
    private KomunikatorHry komunikator;
    private PravidlaUNO pravidla;
    private Hrac hrac1;
    private Hrac hrac2;
    private Hrac hrac3;
    private List<Hrac> hraci;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        hra = Mockito.mock(Hra.class);
        spravceTahu = Mockito.mock(SpravceTahu.class);
        odhazovaciBalicek = Mockito.mock(Balicek.class);
        lizaciBalicek = Mockito.mock(Balicek.class);
        komunikator = Mockito.mock(KomunikatorHry.class);

        when(hra.getSpravceTahu()).thenReturn(spravceTahu);
        when(hra.getOdhazovaciBalicek()).thenReturn(odhazovaciBalicek);
        when(hra.getBalicek()).thenReturn(lizaciBalicek);
        when(hra.getKomunikator()).thenReturn(komunikator);

        hrac1 = Mockito.mock(Hrac.class);
        hrac2 = Mockito.mock(Hrac.class);
        hrac3 = Mockito.mock(Hrac.class);
        hraci = new ArrayList<>(List.of(hrac1, hrac2, hrac3));
        when(hra.getHraci()).thenReturn(hraci);

        pravidla = new PravidlaUNO(hra);
    }

    @Test
    @DisplayName("pripravBalicek vytvoří standardní balíček UNO o 108 kartách")
    @SuppressWarnings("unchecked")
    public void testPripravBalicek() {
        Balicek<Karta> balicek = Mockito.mock(Balicek.class);
        List<Karta> vlozeneKarty = new ArrayList<>();
        doAnswer(invocation -> {
            vlozeneKarty.add(invocation.getArgument(0));
            return null;
        }).when(balicek).vratNahoru(any(Karta.class));

        pravidla.pripravBalicek(balicek);

        assertEquals(108, vlozeneKarty.size(), "Standardní UNO balíček musí obsahovat 108 karet");
        verify(balicek).zamichej();
    }

    @Test
    @DisplayName("pripravitHrace rozdá každému hráči 8 karet")
    public void testPripravitHrace() {
        pravidla.pripravitHrace(hrac1);
        verify(hrac1, times(8)).lizni();
    }

    @Test
    @DisplayName("hracChceLiznout umožní hráči na tahu líznout a ukončit tah")
    public void testHracChceLiznout() {
        when(hrac1.jeNaTahu()).thenReturn(true);
        when(hrac2.jeNaTahu()).thenReturn(false);

        assertFalse(pravidla.hracChceLiznout(hrac2), "Hráč mimo tah nemůže lízat");
        assertTrue(pravidla.hracChceLiznout(hrac1), "Hráč na tahu může lízat");

        verify(hrac1).lizni();
        verify(hrac1).konecTahu();
    }

    @Test
    @DisplayName("hracChceUkoncitTah nelze svévolně přeskočit")
    public void testHracChceUkoncitTah() {
        assertFalse(pravidla.hracChceUkoncitTah(hrac1));
    }

    @Test
    @DisplayName("poOdehrani posune tah a oznámí výhru při odhození poslední karty")
    public void testPoOdehraniVitezstvi() {
        when(hrac1.getKarty()).thenReturn(List.of()); // Hráč 1 nemá karty (dohrál)

        pravidla.poOdehrani(hrac1);

        verify(spravceTahu).dalsiHracSUpozornenim();
        verify(hra).skoncil(hrac1);
        verify(hra).vyhral(hrac1); // 3 hráči celkem, 2 zbývají -> oznámí dílčí výhru
        verify(komunikator, never()).posliKonecHry();
    }

    @Test
    @DisplayName("Hra končí, když po dohrání hráče zbývá už jen poslední hráč")
    public void testKonecHryKdyzZbyvaPosledniHrac() {
        // Hráč 1 dohrál
        when(hrac1.getKarty()).thenReturn(List.of());
        pravidla.poOdehrani(hrac1);

        // Hráč 2 dohrál (zbývá jen hrac3)
        when(hrac2.getKarty()).thenReturn(List.of());
        pravidla.poOdehrani(hrac2);

        verify(komunikator).posliVysledky(any(Hrac[][].class));
        verify(komunikator).posliKonecHry();
    }
}
