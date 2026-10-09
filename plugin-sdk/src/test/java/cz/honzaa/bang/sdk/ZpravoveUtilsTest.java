package cz.honzaa.bang.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ZpravoveUtilsTest {

    @Test
    @DisplayName("localize bez parametrů správně doplňuje prefix $ a nezdvojuje ho")
    public void testLocalizeWithoutParams() {
        assertEquals("", ZpravoveUtils.lokalizuj(null));
        assertEquals("", ZpravoveUtils.lokalizuj(""));
        assertEquals("$bang.dialog.shoot_target", ZpravoveUtils.lokalizuj("bang.dialog.shoot_target"));
        assertEquals("$bang.dialog.shoot_target", ZpravoveUtils.lokalizuj("$bang.dialog.shoot_target"));
    }

    @Test
    @DisplayName("localize s varargs páry klíč-hodnota automaticky escapuje parametry")
    public void testLocalizeWithVarargs() {
        String result = ZpravoveUtils.lokalizuj("bang.dialog.shoot_target", "name", "Honza \"Bomba\"");
        assertEquals("$bang.dialog.shoot_target:{\"name\":\"Honza \\\"Bomba\\\"\"}", result);

        String multi = ZpravoveUtils.lokalizuj("bang.dialog.duel_turn", "target", "Bob\nNewLine", "attacker", "Alice");
        assertEquals("$bang.dialog.duel_turn:{\"target\":\"Bob\\nNewLine\",\"attacker\":\"Alice\"}", multi);
    }

    @Test
    @DisplayName("localize s mapou parametrů zachovává a správně serializuje hodnoty")
    public void testLocalizeWithMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Joe");
        map.put("count", 3);
        map.put("active", true);

        String result = ZpravoveUtils.lokalizuj("test.key", map);
        assertEquals("$test.key:{\"name\":\"Joe\",\"count\":3,\"active\":true}", result);
    }

    @Test
    @DisplayName("localize vyhodí výjimku při lichém počtu varargs parametrů")
    public void testLocalizeOddVarargsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ZpravoveUtils.lokalizuj("test.key", "singleKey");
        });
    }

    @Test
    @DisplayName("format alias funguje shodně jako localize")
    public void testFormatAlias() {
        assertEquals(ZpravoveUtils.lokalizuj("bang.test"), ZpravoveUtils.lokalizuj("bang.test"));
        assertEquals(ZpravoveUtils.lokalizuj("bang.test", "k", "v"), ZpravoveUtils.lokalizuj("bang.test", "k", "v"));
    }
}
