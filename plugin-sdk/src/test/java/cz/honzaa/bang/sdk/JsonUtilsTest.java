package cz.honzaa.bang.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {

    @Test
    @DisplayName("escapeJson správně ošetřuje uvozovky, lomítka, bílé znaky a null")
    public void testEscapeJson() {
        assertEquals("", JsonUtils.escapeJson(null));
        assertEquals("", JsonUtils.escapeJson(""));
        assertEquals("Ahoj světe, (!)", JsonUtils.escapeJson("Ahoj světe, (!)"));
        assertEquals("Text s \\\"uvozovkami\\\"", JsonUtils.escapeJson("Text s \"uvozovkami\""));
        assertEquals("C:\\\\Cesta\\\\K\\\\Souboru", JsonUtils.escapeJson("C:\\Cesta\\K\\Souboru"));
        assertEquals("Radek1\\nRadek2", JsonUtils.escapeJson("Radek1\nRadek2"));
        assertEquals("Radek1\\r\\nRadek2", JsonUtils.escapeJson("Radek1\r\nRadek2"));
        assertEquals("Tab\\tulator", JsonUtils.escapeJson("Tab\tulator"));
        assertEquals("Back\\bspace", JsonUtils.escapeJson("Back\bspace"));
        assertEquals("Form\\ffeed", JsonUtils.escapeJson("Form\ffeed"));
        assertEquals("Unicode: \\u0000\\u001f", JsonUtils.escapeJson("Unicode: \u0000\u001F"));
        assertEquals("<script>alert('XSS')</script>", JsonUtils.escapeJson("<script>alert('XSS')</script>"));
    }
}
