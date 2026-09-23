package cz.honzaa.bang.pluginy.bang;

import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.KomunikatorHry;
import cz.honzaa.bang.sdk.SpravceTahu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PravidlaBanguTest {

    private Hra mockHra;
    private KomunikatorHry mockKomunikator;
    private SpravceTahu mockSpravceTahu;
    private PravidlaBangu pravidla;

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockKomunikator = mock(KomunikatorHry.class);
        mockSpravceTahu = mock(SpravceTahu.class);
        when(mockHra.getKomunikator()).thenReturn(mockKomunikator);
        when(mockHra.getSpravceTahu()).thenReturn(mockSpravceTahu);
        when(mockHra.getHrajiciHraci()).thenReturn(Collections.emptyList());

        pravidla = new PravidlaBangu(mockHra, false);
    }

    @Test
    public void testRoleProDvaHraceJsouObaOdpadlici() {
        List<Role> role = Role.poleRoliBangu(2);
        assertEquals(2, role.size());
        assertEquals(Role.ODPADLIK, role.get(0));
        assertEquals(Role.ODPADLIK, role.get(1));
    }

    @Test
    public void testRoleProTriAzSedmHracuNemeneny() {
        List<Role> role3 = Role.poleRoliBangu(3);
        assertEquals(3, role3.size());
        assertEquals(Role.SERIF, role3.get(0));
        assertEquals(Role.BANDITA, role3.get(1));
        assertEquals(Role.ODPADLIK, role3.get(2));

        List<Role> role4 = Role.poleRoliBangu(4);
        assertEquals(4, role4.size());
        assertEquals(Role.SERIF, role4.get(0));
        assertEquals(Role.BANDITA, role4.get(1));
        assertEquals(Role.ODPADLIK, role4.get(2));
        assertEquals(Role.BANDITA, role4.get(3));
    }

    @Test
    public void testPoSpusteniHryDvaHraciZiskajiOdpadlika() {
        Hrac hrac1 = mock(Hrac.class);
        Hrac hrac2 = mock(Hrac.class);
        when(mockHra.getHraci()).thenReturn(Arrays.asList(hrac1, hrac2));

        pravidla.poSpusteniHry();

        verify(hrac1).priraditRoliNaZacatkuHry(Role.ODPADLIK);
        verify(hrac2).priraditRoliNaZacatkuHry(Role.ODPADLIK);
    }

    @Test
    public void testSpustitPrvniTahSeSerifemVybereSerifa() {
        Hrac hrac1 = mock(Hrac.class);
        when(hrac1.getRole()).thenReturn(Role.SERIF);
        when(mockHra.getHraci()).thenReturn(List.of(hrac1));

        pravidla.spustitPrvniTah(mockSpravceTahu);

        verify(mockSpravceTahu).dalsiHracPodleRole(Role.SERIF);
        verify(mockSpravceTahu, never()).dalsiHracPodlePodminky(any());
    }

    @Test
    public void testSpustitPrvniTahBezSerifaVybereNahodnehoHrace() {
        Hrac hrac1 = mock(Hrac.class);
        Hrac hrac2 = mock(Hrac.class);
        when(hrac1.getRole()).thenReturn(Role.ODPADLIK);
        when(hrac2.getRole()).thenReturn(Role.ODPADLIK);
        when(mockHra.getHraci()).thenReturn(Arrays.asList(hrac1, hrac2));
        when(mockSpravceTahu.getHrajiciHraci()).thenReturn(Arrays.asList(hrac1, hrac2));

        pravidla.spustitPrvniTah(mockSpravceTahu);

        verify(mockSpravceTahu, never()).dalsiHracPodleRole(any());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Predicate<Hrac>> predicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        verify(mockSpravceTahu).dalsiHracPodlePodminky(predicateCaptor.capture());

        Predicate<Hrac> predicate = predicateCaptor.getValue();
        // Podmínka musí být pravdivá buď pro hrac1, nebo pro hrac2, ale ne pro oba
        boolean matchesHrac1 = predicate.test(hrac1);
        boolean matchesHrac2 = predicate.test(hrac2);

        assertTrue(matchesHrac1 || matchesHrac2, "Podmínka musí vybrat jednoho z hráčů");
        assertNotEquals(matchesHrac1, matchesHrac2, "Podmínka nesmí vybrat oba hráče zároveň");
    }

    @Test
    public void testUkonceniHryVeDvouHracichVitezstviPrezivsiho() {
        Hrac hrac1 = mock(Hrac.class);
        Hrac hrac2 = mock(Hrac.class);

        when(hrac1.getRole()).thenReturn(Role.ODPADLIK);
        when(hrac2.getRole()).thenReturn(Role.ODPADLIK);
        when(hrac1.getJmeno()).thenReturn("Hrac 1");
        when(hrac2.getJmeno()).thenReturn("Hrac 2");

        when(hrac1.getKarty()).thenReturn(new ArrayList<>());
        when(hrac2.getKarty()).thenReturn(new ArrayList<>());
        when(hrac1.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(hrac2.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(hrac1.getEfekty()).thenReturn(new ArrayList<>());
        when(hrac2.getEfekty()).thenReturn(new ArrayList<>());

        when(mockHra.getHraci()).thenReturn(Arrays.asList(hrac1, hrac2));

        // Hráč 1 zemře, Hráč 2 zůstává živý
        when(hrac1.jeZivy()).thenReturn(false);
        when(hrac2.jeZivy()).thenReturn(true);

        pravidla.dosliZivoty(hrac1);

        // Ověříme, že výsledky byly odeslány
        ArgumentCaptor<Hrac[][]> vysledkyCaptor = ArgumentCaptor.forClass(Hrac[][].class);
        verify(mockKomunikator).posliVysledky(vysledkyCaptor.capture());
        verify(mockKomunikator).posliKonecHry();

        Hrac[][] vysledky = vysledkyCaptor.getValue();
        assertEquals(2, vysledky.length, "Musí být 2 skupiny pořadí (1. a 2. místo)");

        // 1. místo: hrac2 (živý)
        assertEquals(1, vysledky[0].length);
        assertEquals(hrac2, vysledky[0][0]);

        // 2. místo: hrac1 (mrtvý)
        assertEquals(1, vysledky[1].length);
        assertEquals(hrac1, vysledky[1][0]);
    }

    @Test
    public void testUkonceniHryStandardniSmrtBandityHraNeconci() {
        Hrac serif = mock(Hrac.class);
        Hrac bandita = mock(Hrac.class);
        Hrac odpadlik = mock(Hrac.class);

        when(serif.getRole()).thenReturn(Role.SERIF);
        when(bandita.getRole()).thenReturn(Role.BANDITA);
        when(odpadlik.getRole()).thenReturn(Role.ODPADLIK);

        when(serif.getJmeno()).thenReturn("Serif");
        when(bandita.getJmeno()).thenReturn("Bandita");
        when(odpadlik.getJmeno()).thenReturn("Odpadlik");

        when(bandita.getKarty()).thenReturn(new ArrayList<>());
        when(bandita.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(bandita.getEfekty()).thenReturn(new ArrayList<>());

        when(mockHra.getHraci()).thenReturn(Arrays.asList(serif, bandita, odpadlik));

        // Bandita zemře, šerif a odpadlík žijí
        when(bandita.jeZivy()).thenReturn(false);
        when(serif.jeZivy()).thenReturn(true);
        when(odpadlik.jeZivy()).thenReturn(true);

        pravidla.dosliZivoty(bandita);

        // Hra nesmí skončit, protože šerif a odpadlík ještě bojují
        verify(mockKomunikator, never()).posliKonecHry();
        verify(mockKomunikator, never()).posliVysledky(any());
    }

    @Test
    public void testUkonceniHryStandardniSerifVyhrava() {
        Hrac serif = mock(Hrac.class);
        Hrac bandita = mock(Hrac.class);
        Hrac odpadlik = mock(Hrac.class);

        when(serif.getRole()).thenReturn(Role.SERIF);
        when(bandita.getRole()).thenReturn(Role.BANDITA);
        when(odpadlik.getRole()).thenReturn(Role.ODPADLIK);

        when(serif.getJmeno()).thenReturn("Serif");
        when(bandita.getJmeno()).thenReturn("Bandita");
        when(odpadlik.getJmeno()).thenReturn("Odpadlik");

        when(odpadlik.getKarty()).thenReturn(new ArrayList<>());
        when(odpadlik.getVylozeneKarty()).thenReturn(new ArrayList<>());
        when(odpadlik.getEfekty()).thenReturn(new ArrayList<>());

        when(mockHra.getHraci()).thenReturn(Arrays.asList(serif, bandita, odpadlik));

        // Bandita už byl mrtvý, teď zemře odpadlík, šerif zůstává živý
        when(bandita.jeZivy()).thenReturn(false);
        when(odpadlik.jeZivy()).thenReturn(false);
        when(serif.jeZivy()).thenReturn(true);

        pravidla.dosliZivoty(odpadlik);

        // Šerif vyhrává
        verify(mockKomunikator).posliKonecHry();
        ArgumentCaptor<Hrac[][]> vysledkyCaptor = ArgumentCaptor.forClass(Hrac[][].class);
        verify(mockKomunikator).posliVysledky(vysledkyCaptor.capture());

        Hrac[][] vysledky = vysledkyCaptor.getValue();
        // 1. místo: šerif
        assertEquals(serif, vysledky[0][0]);
    }
}
