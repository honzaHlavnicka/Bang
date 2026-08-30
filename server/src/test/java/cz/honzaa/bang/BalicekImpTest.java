package cz.honzaa.bang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BalicekImpTest {

    private BalicekImp<String> balicek;

    @BeforeEach
    public void setUp() {
        balicek = new BalicekImp<>();
    }

    @Test
    public void testVlozeniALizaniKaret() {
        balicek = new BalicekImp<>(java.util.List.of("Karta1", "Karta2", "Karta3"));
        assertEquals(3, balicek.pocet(), "Balíček by měl obsahovat 3 karty.");

        String liznuta = balicek.lizni();
        assertNotNull(liznuta, "Měla by se líznout nějaká karta.");
        assertEquals(2, balicek.pocet(), "Po líznutí by měly zbýt 2 karty.");
    }

    @Test
    public void testLizaniZprazdnehoBalickuEdgeCase() {
        // Balíček je teď úplně prázdný
        String liznuta = balicek.lizni();
        assertNull(liznuta, "Při líznutí z prázdného balíčku by měla metoda vrátit null.");
    }
}
