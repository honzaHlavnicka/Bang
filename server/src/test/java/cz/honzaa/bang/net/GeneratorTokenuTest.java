package cz.honzaa.bang.net;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTokenuTest {

    @Test
    public void testFormatADelkaTokenu() {
        String token = GeneratorTokenu.NovytokenHrace();
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // 32 bajtů v URL-safe Base64 bez paddingu má délku 43 znaků
        assertEquals(43, token.length(), "Token by měl mít přesně 43 znaků (32 bajtů Base64 bez paddingu)");
        // Musí obsahovat pouze URL-safe znaky: A-Z, a-z, 0-9, '-', '_'
        assertTrue(token.matches("^[A-Za-z0-9_-]+$"), "Token obsahuje nepovolené znaky: " + token);
    }

    @Test
    public void testUnikatnostTokenuBezKolizi() {
        int pocet = 10_000;
        Set<String> tokeny = new HashSet<>(pocet);

        for (int i = 0; i < pocet; i++) {
            String token = GeneratorTokenu.NovytokenHrace();
            boolean pridano = tokeny.add(token);
            assertTrue(pridano, "Nastala kolize tokenu: " + token);
        }

        assertEquals(pocet, tokeny.size(), "Všechny vygenerované tokeny musí být unikátní.");
    }
}
