package cz.honzaa.bang;

import cz.honzaa.bang.pravidla.SpravceHernichPravidel;
import cz.honzaa.bang.sdk.HerniPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrekladyTest {

    @BeforeAll
    public static void setUp() {
        SpravceHernichPravidel.pregeneruj();
    }

    @Test
    public void testNacteniPluginuAPrekladu() {
        System.out.println("JSON her: " + SpravceHernichPravidel.getJSONVytvoritelneHry());
        for (int i = 0; i < 5; i++) {
            HerniPlugin plugin = SpravceHernichPravidel.getPlugin(i);
            if (plugin != null) {
                System.out.println("Plugin " + i + ": " + plugin.getJmeno());
                String cs = plugin.getPreklady("cs");
                String en = plugin.getPreklady("en");
                System.out.println("  cs: " + cs);
                System.out.println("  en: " + en);
                assertNotNull(cs);
                assertNotNull(en);
                
                HraImp hra = HraImp.vytvor(null, i);
                assertEquals(cs, hra.getPreklady("cs"));
                assertEquals(en, hra.getPreklady("en"));
            }
        }
    }

    @Test
    public void testChybaPrekladoveKlice() {
        for (cz.honzaa.bang.sdk.Chyba chyba : cz.honzaa.bang.sdk.Chyba.values()) {
            assertTrue(chyba.getZprava().startsWith("$error."), "Chyba " + chyba.name() + " by měla mít překladový klíč začínající na $error.");
        }
    }
}
