package cz.honzaa.bang;

import cz.honzaa.bang.sdk.HerniPravidla;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.KomunikatorHry;
import cz.honzaa.bang.sdk.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpravceTahuImpTest {

    private Hra mockHra;
    private KomunikatorHry mockKomunikator;
    private HerniPravidla mockPravidla;

    private HracImp hrac1;
    private HracImp hrac2;
    private HracImp hrac3;
    private HracImp hrac4;

    private enum TestRole implements Role {
        ROLE_A, ROLE_B, ROLE_C
    }

    @BeforeEach
    public void setUp() {
        mockHra = mock(Hra.class);
        mockKomunikator = mock(KomunikatorHry.class);
        mockPravidla = mock(HerniPravidla.class);

        when(mockHra.getKomunikator()).thenReturn(mockKomunikator);
        when(mockHra.getHerniPravidla()).thenReturn(mockPravidla);

        hrac1 = new HracImp(mockHra);
        hrac1.setJmeno("Hrac 1");

        hrac2 = new HracImp(mockHra);
        hrac2.setJmeno("Hrac 2");

        hrac3 = new HracImp(mockHra);
        hrac3.setJmeno("Hrac 3");

        hrac4 = new HracImp(mockHra);
        hrac4.setJmeno("Hrac 4");
    }

    @Test
    public void testZakladniPruchodTahuKruhem() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        List<Hrac> hrajici = spravce.getHrajiciHraci();
        assertEquals(3, hrajici.size());
        assertEquals(hrac1, hrajici.get(0));
        assertEquals(hrac2, hrajici.get(1));
        assertEquals(hrac3, hrajici.get(2));

        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.getNaTahu());

        assertEquals(hrac2, spravce.dalsiHrac());
        assertEquals(hrac2, spravce.getNaTahu());

        assertEquals(hrac3, spravce.dalsiHrac());
        assertEquals(hrac3, spravce.getNaTahu());

        // Po hrac3 se musí vrátit k hrac1 (kruh)
        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.getNaTahu());
    }

    @Test
    public void testZmenaSmeruHryPredZahajenim() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        // Změníme směr hry ihned
        spravce.zmenaSmeru();

        // Pořadí hrajících hráčů je obrácené
        List<Hrac> hrajici = spravce.getHrajiciHraci();
        assertEquals(3, hrajici.size());
        assertEquals(hrac3, hrajici.get(0));
        assertEquals(hrac2, hrajici.get(1));
        assertEquals(hrac1, hrajici.get(2));

        // Tahy jdou odzadu: hrac3, hrac2, hrac1, pak znovu hrac3
        assertEquals(hrac3, spravce.dalsiHrac());
        assertEquals(hrac2, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac3, spravce.dalsiHrac());
    }

    @Test
    public void testZmenaSmeruHryVPrubehu() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac2, spravce.dalsiHrac());

        // Po hrac2 změníme směr
        spravce.zmenaSmeru();

        // Fronta nyní vybírá z opačné strany
        assertEquals(hrac2, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac3, spravce.dalsiHrac());
    }

    @Test
    public void testEsoPreskociDalsihoHracePoSmeru() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        assertEquals(hrac1, spravce.dalsiHrac());

        // Eso přeskočí hrac2
        Hrac preskoceny = spravce.eso();
        assertEquals(hrac2, preskoceny, "Eso by mělo vrátit hráče 2, který byl přeskočen");
        assertEquals(hrac1, spravce.getNaTahu(), "Aktuální hráč na tahu se nesmí změnit");

        // Další tah musí patřit hrac3
        assertEquals(hrac3, spravce.dalsiHrac());
    }

    @Test
    public void testEsoPreskociDalsihoHraceProtiSmeru() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3, hrac4));

        spravce.zmenaSmeru();
        assertEquals(hrac4, spravce.dalsiHrac());

        // V opačném směru je dalším hráčem hrac3
        Hrac preskoceny = spravce.eso();
        assertEquals(hrac3, preskoceny);

        // Další na tahu po přeskočení hrac3 je hrac2
        assertEquals(hrac2, spravce.dalsiHrac());
    }

    @Test
    public void testNasobicTahu() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2));

        assertEquals(hrac1, spravce.dalsiHrac());

        // Nastavíme násobič tahu na 3 (hráč má celkem 3 tahy)
        spravce.setNasobicTahu(3);

        // Druhý tah hrac1
        assertEquals(hrac1, spravce.dalsiHrac());
        // Třetí tah hrac1
        assertEquals(hrac1, spravce.dalsiHrac());

        // Čtvrtý tah už přejde na hrac2
        assertEquals(hrac2, spravce.dalsiHrac());
    }

    @Test
    public void testVyraditAVratitHrace() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        // Vyřadíme hrac2
        spravce.vyraditHrace(hrac2);

        List<Hrac> hrajici = spravce.getHrajiciHraci();
        assertEquals(2, hrajici.size());
        assertFalse(hrajici.contains(hrac2));

        assertEquals(hrac1, spravce.dalsiHrac());
        // hrac2 je vyřazen, měl by se přeskočit rovnou na hrac3
        assertEquals(hrac3, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.dalsiHrac());

        // Nyní hrac2 vrátíme do hry
        spravce.vratitHrace(hrac2);
        assertTrue(spravce.getHrajiciHraci().contains(hrac2));

        // Následující tah po hrac1 musí být opět hrac2
        assertEquals(hrac2, spravce.dalsiHrac());
    }

    @Test
    public void testDalsiHracPodleRoleAProtoceniFronty() {
        hrac1.priraditRoliNaZacatkuHry(TestRole.ROLE_A);
        hrac2.priraditRoliNaZacatkuHry(TestRole.ROLE_B);
        hrac3.priraditRoliNaZacatkuHry(TestRole.ROLE_C);

        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2, hrac3));

        Hrac naTahu = spravce.dalsiHracPodleRole(TestRole.ROLE_B);
        assertEquals(hrac2, naTahu);

        // Ověříme, že zahájení tahu bylo komunikováno
        verify(mockKomunikator).posliZahajeniTahu(hrac2);

        // Fronta se musela protočit tak, že po hrac2 hraje hrac3 a pak hrac1
        assertEquals(hrac3, spravce.dalsiHrac());
        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac2, spravce.dalsiHrac());
    }

    @Test
    public void testDalsiHracPodlePodminkyKdyzNikdoNesplnuje() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2));

        // Nejprve začne hrac1
        assertEquals(hrac1, spravce.dalsiHrac());

        // Podmínka, kterou nikdo nesplňuje
        Hrac vysledek = spravce.dalsiHracPodlePodminky(h -> false);
        assertEquals(hrac1, vysledek, "Pokud nikdo nesplňuje podmínku, na tahu zůstává stávající hráč");
    }

    @Test
    public void testPridatHrace() {
        SpravceTahuImp spravce = new SpravceTahuImp(List.of(hrac1, hrac2));
        assertEquals(2, spravce.getHrajiciHraci().size());

        spravce.pridatHrace(hrac3);
        assertEquals(3, spravce.getHrajiciHraci().size());

        assertEquals(hrac1, spravce.dalsiHrac());
        assertEquals(hrac2, spravce.dalsiHrac());
        assertEquals(hrac3, spravce.dalsiHrac());
    }

    @Test
    public void testPrazdnaFronta() {
        SpravceTahuImp spravce = new SpravceTahuImp(Collections.emptyList());
        assertTrue(spravce.getHrajiciHraci().isEmpty());
        assertNull(spravce.eso(), "Eso v prázdné frontě musí vrátit null");
        assertNull(spravce.dalsiHrac(), "dalsiHrac v prázdné frontě vrátí null");
    }
}
