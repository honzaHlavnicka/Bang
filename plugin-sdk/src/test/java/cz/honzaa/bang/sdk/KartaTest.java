package cz.honzaa.bang.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class KartaTest {

    private static class TestKartaHratelna extends Karta implements HratelnaKarta {
        public TestKartaHratelna(Hra hra, Balicek<Karta> balicek) {
            super(hra, balicek);
        }

        @Override
        public String getObrazek() {
            return "karta_obrazek.png";
        }

        @Override
        public String getJmeno() {
            return "Testovací Karta";
        }

        @Override
        public boolean odehrat(Hrac kym) {
            return true;
        }
    }

    private static class TestKartaVylozitelna extends Karta implements VylozitelnaKarta {
        public TestKartaVylozitelna(Hra hra, Balicek<Karta> balicek) {
            super(hra, balicek);
        }

        @Override
        public String getObrazek() {
            return "vylozena_karta.png";
        }

        @Override
        public String getJmeno() {
            return "Vyložená \"Super\" Karta";
        }

        @Override
        public boolean vylozit(Hrac predKoho, Hrac kym) {
            return true;
        }

        @Override
        public Efekt getEfekt() {
            return null;
        }

        @Override
        public void spalitVylozenou() {
        }
    }

    @Test
    @DisplayName("escapeJson správně ošetřuje uvozovky, lomítka, bílé znaky a null")
    public void testEscapeJson() {
        assertEquals("", Karta.escapeJson(null));
        assertEquals("", Karta.escapeJson(""));
        assertEquals("Ahoj svete", Karta.escapeJson("Ahoj svete"));
        assertEquals("Text s \\\"uvozovkami\\\"", Karta.escapeJson("Text s \"uvozovkami\""));
        assertEquals("C:\\\\Cesta\\\\K\\\\Souboru", Karta.escapeJson("C:\\Cesta\\K\\Souboru"));
        assertEquals("Radek1\\nRadek2", Karta.escapeJson("Radek1\nRadek2"));
        assertEquals("Radek1\\r\\nRadek2", Karta.escapeJson("Radek1\r\nRadek2"));
        assertEquals("Tab\\tulator", Karta.escapeJson("Tab\tulator"));
        assertEquals("Back\\bspace", Karta.escapeJson("Back\bspace"));
        assertEquals("Form\\ffeed", Karta.escapeJson("Form\ffeed"));
    }

    @Test
    @DisplayName("Každá vytvořená karta má unikátní vzrůstající ID")
    public void testUnikatniId() {
        TestKartaHratelna k1 = new TestKartaHratelna(null, null);
        TestKartaHratelna k2 = new TestKartaHratelna(null, null);

        assertTrue(k2.getId() > k1.getId(), "Druhá karta by měla mít vyšší ID než první");
    }

    @Test
    @DisplayName("toJSON správně serializuje atributy karty včetně typu hratelná/vyložitelná")
    public void testToJSON() {
        TestKartaHratelna hratelna = new TestKartaHratelna(null, null);
        String jsonHratelna = hratelna.toJSON();

        assertTrue(jsonHratelna.contains("\"jmeno\":\"Testovací Karta\""));
        assertTrue(jsonHratelna.contains("\"obrazek\":\"karta_obrazek.png\""));
        assertTrue(jsonHratelna.contains("\"id\":" + hratelna.getId()));
        assertTrue(jsonHratelna.contains("\"hratelna\":true"));
        assertTrue(jsonHratelna.contains("\"vylozitelna\":false"));

        TestKartaVylozitelna vylozitelna = new TestKartaVylozitelna(null, null);
        String jsonVylozitelna = vylozitelna.toJSON();

        assertTrue(jsonVylozitelna.contains("\"jmeno\":\"Vyložená \\\"Super\\\" Karta\""));
        assertTrue(jsonVylozitelna.contains("\"obrazek\":\"vylozena_karta.png\""));
        assertTrue(jsonVylozitelna.contains("\"id\":" + vylozitelna.getId()));
        assertTrue(jsonVylozitelna.contains("\"hratelna\":false"));
        assertTrue(jsonVylozitelna.contains("\"vylozitelna\":true"));
    }

    @Test
    @DisplayName("getZadniObrazek se deleguje na HerniPravidla přes hru")
    public void testGetZadniObrazek() {
        Hra hra = Mockito.mock(Hra.class);
        HerniPravidla pravidla = Mockito.mock(HerniPravidla.class);
        when(hra.getHerniPravidla()).thenReturn(pravidla);
        when(pravidla.getVychoziZadniObrazek()).thenReturn("karty/rub_bang");

        TestKartaHratelna karta = new TestKartaHratelna(hra, null);
        assertEquals("karty/rub_bang", karta.getZadniObrazek());
    }
}
