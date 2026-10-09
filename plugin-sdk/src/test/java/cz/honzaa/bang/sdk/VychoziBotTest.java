package cz.honzaa.bang.sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VychoziBotTest {

    private Hra mockHra;
    private Hrac mockHrac;
    private HerniPravidla mockPravidla;
    private VychoziBot bot;

    private static class DummyHratelnaKarta extends Karta implements HratelnaKarta {
        public DummyHratelnaKarta() {
            super(null, null);
        }

        @Override
        public String getJmeno() {
            return "TestKarta";
        }

        @Override
        public String getObrazek() {
            return "test";
        }

        @Override
        public boolean odehrat(Hrac kym) {
            return true;
        }
    }

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockHrac = mock(Hrac.class);
        mockPravidla = mock(HerniPravidla.class);
        when(mockHra.getHerniPravidla()).thenReturn(mockPravidla);

        bot = new VychoziBot();
    }

    @Test
    public void testBotZahrajeKartyPokudMuze() {
        when(mockHrac.jeNaTahu()).thenReturn(true, false);
        when(mockHrac.jeZivy()).thenReturn(true);

        DummyHratelnaKarta karta = new DummyHratelnaKarta();
        List<Karta> karty = new ArrayList<>(List.of(karta));
        when(mockHrac.getKarty()).thenReturn(karty);
        when(mockPravidla.muzeZahrat(karta, mockHrac)).thenReturn(true);

        bot.naTahu(mockHra, mockHrac);

        verify(mockHrac).odehranaKarta(String.valueOf(karta.getId()));
    }

    @Test
    public void testBotLiznePokudNemuzuZahrat() {
        when(mockHrac.jeNaTahu()).thenReturn(true);
        when(mockHrac.jeZivy()).thenReturn(true);
        when(mockHrac.getKarty()).thenReturn(new ArrayList<>());
        when(mockPravidla.hracChceLiznout(mockHrac)).thenReturn(true);

        bot.naTahu(mockHra, mockHrac);

        verify(mockPravidla).hracChceLiznout(mockHrac);
    }

    @Test
    public void testBotDialogDefaults() {
        DummyHratelnaKarta k1 = new DummyHratelnaKarta();
        DummyHratelnaKarta k2 = new DummyHratelnaKarta();

        CompletableFuture<String> kartyResult = bot.pozadavekNaKarty(mockHra, mockHrac, List.of(k1, k2), "Vyber", 1, 1);
        assertEquals(String.valueOf(k1.getId()), kartyResult.join());

        Hrac h1 = mock(Hrac.class);
        when(h1.getId()).thenReturn(10);
        Hrac h2 = mock(Hrac.class);
        when(h2.getId()).thenReturn(20);

        CompletableFuture<String> hraciResult = bot.pozadavekNaHrace(mockHra, mockHrac, List.of(mockHrac, h2), "Vyber", 1, 1);
        assertEquals("20", hraciResult.join());

        CompletableFuture<String> moznostiResult = bot.pozadavekNaMoznosti(mockHra, mockHrac, List.of("A", "B"), "Vyber");
        assertEquals("0", moznostiResult.join());
    }
}
