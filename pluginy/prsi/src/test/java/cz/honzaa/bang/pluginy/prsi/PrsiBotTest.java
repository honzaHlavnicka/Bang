package cz.honzaa.bang.pluginy.prsi;

import cz.honzaa.bang.sdk.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrsiBotTest {

    private Hra mockHra;
    private Hrac mockHrac;
    private PravidlaPrsi pravidla;
    private PrsiBot bot;

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockHrac = mock(Hrac.class);
        pravidla = new PravidlaPrsi(mockHra);
        bot = new PrsiBot();

        when(mockHra.getHerniPravidla()).thenReturn(pravidla);
    }

    @Test
    public void testPrsiBotOdehrajeKartuPokudMuze() {
        when(mockHrac.jeNaTahu()).thenReturn(true);
        when(mockHrac.jeZivy()).thenReturn(true);

        Balicek<Karta> odhazovaci = mock(Balicek.class);
        PrsiKarta vrchni = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.CERVENE, PrsiHodnota.SEDMA);
        when(odhazovaci.nahledni()).thenReturn(vrchni);
        when(mockHra.getOdhazovaciBalicek()).thenReturn(odhazovaci);

        PrsiKarta vRuce = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.CERVENE, PrsiHodnota.OSMA);
        when(mockHrac.getKarty()).thenReturn(List.of(vRuce));

        bot.naTahu(mockHra, mockHrac);

        verify(mockHrac).odehranaKarta(String.valueOf(vRuce.getId()));
    }

    @Test
    public void testPrsiBotLiznePokudNemaKartu() {
        when(mockHrac.jeNaTahu()).thenReturn(true);
        when(mockHrac.jeZivy()).thenReturn(true);

        Balicek<Karta> odhazovaci = mock(Balicek.class);
        PrsiKarta vrchni = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.CERVENE, PrsiHodnota.SEDMA);
        when(odhazovaci.nahledni()).thenReturn(vrchni);
        when(mockHra.getOdhazovaciBalicek()).thenReturn(odhazovaci);

        PrsiKarta vRuce = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.KULE, PrsiHodnota.OSMA);
        when(mockHrac.getKarty()).thenReturn(List.of(vRuce));

        bot.naTahu(mockHra, mockHrac);

        verify(mockHrac).lizniKontrolovane();
    }

    @Test
    public void testPrsiBotVybiraBarvuPodleKaret() {
        Balicek<Karta> odhazovaci = mock(Balicek.class);
        PrsiKarta k1 = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.ZALUDY, PrsiHodnota.DESITKA);
        PrsiKarta k2 = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.ZALUDY, PrsiHodnota.DEVITKA);
        PrsiKarta k3 = new PrsiKarta(mockHra, odhazovaci, PrsiBarva.CERVENE, PrsiHodnota.KRAL);

        when(mockHrac.getKarty()).thenReturn(List.of(k1, k2, k3));

        List<String> moznosti = List.of("$prsi.color_kule", "$prsi.color_zelene", "$prsi.color_cervene", "$prsi.color_zaludy");
        CompletableFuture<String> result = bot.pozadavekNaMoznosti(mockHra, mockHrac, moznosti, "Vyber barvu");

        // Žaludy jsou index 3 a bot jich má nejvíc (2 ks)
        assertEquals("3", result.join());
    }
}
