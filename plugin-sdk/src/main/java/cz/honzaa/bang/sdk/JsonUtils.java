package cz.honzaa.bang.sdk;

/**
 * Pomocná třída pro práci s JSON.
 * 
 * @author honza
 */
public final class JsonUtils {

    private JsonUtils() {}

    /**
     * Escapuje text pro bezpečné vložení do JSON. Neescapuje html.
     * Mělo by se vždy použít, pokud se generuje JSON na základě vstupů
     * od uživatele.
     * 
     * @param text Nebezpečný text
     * @return Escapovaný text
     */
    @PovolenePluginu
    public static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32 || c == 127) {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
