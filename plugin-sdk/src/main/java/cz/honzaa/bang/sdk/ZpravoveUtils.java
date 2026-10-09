package cz.honzaa.bang.sdk;

import java.util.Map;

/**
 * Pomocná třída pro formátování a bezpečnou lokalizaci zpráv a dialogů odesílaných ze serveru.
 * Zajišťuje bezpečné escapování JSON a jednodušší sestavování řetězců pro překlad.
 * 
 * @author honza
 */
public final class ZpravoveUtils {
    private ZpravoveUtils() {}

    /**
     * Vytvoří lokalizační řetězec bez parametrů ve formátu {@code $klic}.
     * Pokud již klíč začíná na '$', prefix se nezdvojuje.
     * 
     * @param key Překladový klíč (např. "bang.dialog.out_of_lives" nebo "$bang.dialog.out_of_lives")
     * @return Zformátovaný lokalizační klíč
     */
    @PovolenePluginu
    public static String lokalizuj(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        return key.startsWith("$") ? key : "$" + key;
    }

    /**
     * Vytvoří lokalizační řetězec s parametry z mapy ve tvaru {@code $klic:{"klic":"hodnota", ...}}.
     * Všechny klíče i hodnoty jsou bezpečně escapovány pro JSON.
     * 
     * @param key Překladový klíč
     * @param params Mapa parametrů
     * @return Zformátovaný lokalizační klíč s JSON parametry
     */
    @PovolenePluginu
    public static String lokalizuj(String key, Map<String, ?> params) {
        String baseKey = ZpravoveUtils.lokalizuj(key);
        if (params == null || params.isEmpty()) {
            return baseKey;
        }

        StringBuilder sb = new StringBuilder(baseKey);
        sb.append(":{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(JsonUtils.escapeJson(entry.getKey())).append("\":");
            appendJSONHodnotu(sb, entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Vytvoří lokalizační řetězec s parametry bez nutnosti vytváření mapy.
     * Aletrnativa k {@link #lokalizuj(java.lang.String, java.util.Map) }
     * Všechny klíče i hodnoty jsou bezpečně escapovány pro JSON.
     * 
     * @param prekladovyKlic Překladový klíč
     * @param dvojiceKlicHodnota Střídající se klíče a hodnoty (key1, val1, key2, val2, ...)
     * @return Zformátovaný lokalizační klíč s JSON parametry
     * @see #lokalizuj(java.lang.String, java.util.Map) 
     */
    @PovolenePluginu
    public static String lokalizuj(String prekladovyKlic, Object... dvojiceKlicHodnota) {
        String baseKey = ZpravoveUtils.lokalizuj(prekladovyKlic);
        if (dvojiceKlicHodnota == null || dvojiceKlicHodnota.length == 0) {
            return baseKey;
        }
        if (dvojiceKlicHodnota.length % 2 != 0) {
            throw new IllegalArgumentException("Počet argumentů keyValuePairs musí být sudý (páry klíč-hodnota).");
        }

        StringBuilder sb = new StringBuilder(baseKey);
        sb.append(":{");
        for (int i = 0; i < dvojiceKlicHodnota.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            String paramKey = String.valueOf(dvojiceKlicHodnota[i]);
            Object paramVal = dvojiceKlicHodnota[i + 1];
            sb.append("\"").append(JsonUtils.escapeJson(paramKey)).append("\":");
            appendJSONHodnotu(sb, paramVal);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Pomocná metoda, která připojí na konec sb Object podle toho, jaky typ objektu to je
     * a objekt escapuje.
     * 
     * @param sb StringBuilder, kam se přidá JSON
     * @param value hodnta k připojení.
     */
    private static void appendJSONHodnotu(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("\"\"");
        } else if (value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Number) {
            if (value instanceof Double || value instanceof Float) {
                double d = ((Number) value).doubleValue();
                if (Double.isNaN(d) || Double.isInfinite(d)) {
                    sb.append("\"").append(d).append("\""); // NaN/Infinity bezpečně do uvozovek
                    return;
                }
            }
            sb.append(value);
        } else {
            sb.append("\"").append(JsonUtils.escapeJson(String.valueOf(value))).append("\"");
        }
    }
}
