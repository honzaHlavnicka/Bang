package cz.honzaa.bang.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ZpravoveUtilsTest {

    @Test
    @DisplayName("lokalizuj bez parametrů správně doplňuje prefix $ a nezdvojuje ho")
    public void testLokalizujWithoutParams() {
        assertEquals("", ZpravoveUtils.lokalizuj(null));
        assertEquals("", ZpravoveUtils.lokalizuj(""));
        assertEquals("$bang.dialog.shoot_target", ZpravoveUtils.lokalizuj("bang.dialog.shoot_target"));
        assertEquals("$bang.dialog.shoot_target", ZpravoveUtils.lokalizuj("$bang.dialog.shoot_target"));
    }

    @Test
    @DisplayName("lokalizuj s varargs páry klíč-hodnota automaticky escapuje parametry")
    public void testLokalizujWithVarargs() {
        String result = ZpravoveUtils.lokalizuj("bang.dialog.shoot_target", "name", "Honza \"Bomba\"");
        assertEquals("$bang.dialog.shoot_target:{\"name\":\"Honza \\\"Bomba\\\"\"}", result);

        String multi = ZpravoveUtils.lokalizuj("bang.dialog.duel_turn", "target", "Bob\nNewLine", "attacker", "Alice");
        assertEquals("$bang.dialog.duel_turn:{\"target\":\"Bob\\nNewLine\",\"attacker\":\"Alice\"}", multi);
    }

    @Test
    @DisplayName("lokalizuj s mapou parametrů zachovává a správně serializuje hodnoty")
    public void testLokalizujWithMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Joe");
        map.put("count", 3);
        map.put("active", true);

        String result = ZpravoveUtils.lokalizuj("test.key", map);
        assertEquals("$test.key:{\"name\":\"Joe\",\"count\":3,\"active\":true}", result);
    }

    @Test
    @DisplayName("lokalizuj vyhodí výjimku při lichém počtu varargs parametrů")
    public void testLokalizujOddVarargsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ZpravoveUtils.lokalizuj("test.key", "singleKey");
        });
    }
}
