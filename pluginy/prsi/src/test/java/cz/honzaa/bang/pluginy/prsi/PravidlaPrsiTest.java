package cz.honzaa.bang.pluginy.prsi;

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

public class PravidlaPrsiTest {

    private Hra hra;
    private SpravceTahu spravceTahu;
    private Balicek<Karta> odhazovaciBalicek;
    private Balicek<Karta> lizaciBalicek;
    private KomunikatorHry komunikator;
    private PravidlaPrsi pravidla;
    private Hrac hrac1;
    private Hrac hrac2;
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
        hraci = new ArrayList<>(List.of(hrac1, hrac2));
        when(hra.getHraci()).thenReturn(hraci);

        pravidla = new PravidlaPrsi(hra);
    }

    @Test
    @DisplayName("pripravBalicek vytvoří přesně 32 karet pro 4 barvy a 8 hodnot")
    @SuppressWarnings("unchecked")
    public void testPripravBalicek() {
        Balicek<Karta> balicek = Mockito.mock(Balicek.class);
        List<Karta> vlozeneKarty = new ArrayList<>();
        doAnswer(invocation -> {
            vlozeneKarty.add(invocation.getArgument(0));
            return null;
        }).when(balicek).vratNahoru(any(Karta.class));

        pravidla.pripravBalicek(balicek);

        assertEquals(32, vlozeneKarty.size(), "Balíček na prší musí obsahovat přesně 32 karet");
        verify(balicek).zamichej();
    }

    @Test
    @DisplayName("pripravitHrace rozdá hráči na začátku 4 karty")
    public void testPripravitHrace() {
        pravidla.pripravitHrace(hrac1);
        verify(hrac1, times(4)).lizni();
    }

    @Test
    @DisplayName("hracChceLiznout lízne 1 kartu a ukončí tah, pokud není penalizace")
    public void testHracChceLiznoutBezPenalizace() {
        when(hrac1.jeNaTahu()).thenReturn(true);
        boolean liznul = pravidla.hracChceLiznout(hrac1);

        assertTrue(liznul);
        verify(hrac1, times(1)).lizni();
        verify(hrac1).konecTahu();
    }

    @Test
    @DisplayName("Sedmičky akumulují karty na líznutí (nekrvavá +2, červená +4)")
    public void testSedmickyAkumulace() {
        when(hrac1.jeNaTahu()).thenReturn(true);

        pravidla.zahranaSedmicka(false); // +2
        pravidla.zahranaSedmicka(true);  // +4 -> celkem 6

        boolean liznul = pravidla.hracChceLiznout(hrac1);
        assertTrue(liznul);
        verify(hrac1, times(6)).lizni();
        verify(hrac1).konecTahu();

        // Po líznutí se čítač vynuluje
        when(hrac2.jeNaTahu()).thenReturn(true);
        pravidla.hracChceLiznout(hrac2);
        verify(hrac2, times(1)).lizni();
    }

    @Test
    @DisplayName("Při penalizaci ze sedmiček nelze hrát běžné karty, ale lze přebít další sedmičkou")
    public void testMuzeZahratPriSedmicce() {
        pravidla.zahranaSedmicka(false); // penalizace aktivní

        PrsiKarta beznaKarta = new PrsiKarta(hra, lizaciBalicek, PrsiBarva.ZALUDY, PrsiHodnota.OSMA);
        PrsiSedmicka sedmicka = new PrsiSedmicka(hra, lizaciBalicek, PrsiBarva.CERVENE, pravidla);

        assertFalse(pravidla.muzeZahrat(beznaKarta, hrac1), "Běžnou kartu nelze hrát, když je aktivní trest ze sedmičky");
        assertTrue(pravidla.muzeZahrat(sedmicka, hrac1), "Sedmičku lze přebít další sedmičkou");
    }

    @Test
    @DisplayName("Když svršek čeká na barvu, nikdo nemůže hrát ani lízat")
    public void testSvrsekCekaNaBarvuBlokujeTah() {
        PrsiSvrsek svrsek = Mockito.mock(PrsiSvrsek.class);
        when(svrsek.isCekaNaBarvu()).thenReturn(true);
        when(odhazovaciBalicek.nahledni()).thenReturn(svrsek);
        when(hrac1.jeNaTahu()).thenReturn(true);

        PrsiKarta karta = new PrsiKarta(hra, lizaciBalicek, PrsiBarva.KULE, PrsiHodnota.OSMA);
        assertFalse(pravidla.muzeZahrat(karta, hrac1), "Nelze hrát, dokud svršek čeká na barvu");
        assertFalse(pravidla.hracChceLiznout(hrac1), "Nelze lízat, dokud svršek čeká na barvu");
    }

    @Test
    @DisplayName("Když hráč odhodí poslední kartu, zařadí se do pořadí a při zbývajícím 1 hráči hra končí")
    public void testUkonceniHryPoPosledniKarter() {
        when(hrac1.getKarty()).thenReturn(List.of()); // Hráč 1 nemá karty (vyhrál)

        pravidla.poOdehrani(hrac1);

        verify(komunikator).posliVysledky(any(Hrac[][].class));
        verify(komunikator).posliKonecHry();
    }
}
