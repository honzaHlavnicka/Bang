package cz.honza.bang.pravidla;

import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.HraImp;
import cz.honza.bang.sdk.HerniPravidla;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author honza
 */



public class SpravceHernichPravidel {
    private static final Logger logger = LoggerFactory.getLogger(SpravceHernichPravidel.class);

    private static final List<HerniPlugin> pluginy = new ArrayList<>();

    static {
        //pregeneruj();
    }

    public static String getJSONVytvoritelneHry() {
        return IntStream.range(0, pluginy.size())
                .mapToObj(i -> {
                    HerniPlugin p = pluginy.get(i);
                    return String.format(
                            "{\"id\":%d,\"jmeno\":\"%s\",\"popis\":\"%s\",\"url\":\"%s\"}",
                            i, escapeJSON(p.getJmeno()), escapeJSON(p.getPopis()), escapeJSON(p.getURLPravidel())
                    );
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static HerniPravidla vytvorHerniPravidla(int id, HraImp hra) {
        return pluginy.get(id).vytvor(hra);
    }

    private static String escapeJSON(String text) {
        return text.replace("\"", "\\\"");
    }
    
    public static void pregeneruj(){
        try {
            pluginy.clear();
            
            // Zkus hledat pluginy v různých cestách
            Path[] cesty = {
                Paths.get("pluginy"),           // Relativní vůči pracovnímu adresáři
                Paths.get("./pluginy"),         // Explicitní aktuální složka
                Paths.get("../pluginy"),        // Nadřazená složka (pro build/)
            };
            
            for (Path cesta : cesty) {
                if (java.nio.file.Files.exists(cesta)) {
                    pluginy.addAll(NacitacPluginu.nactiPluginy(cesta));
                    logger.info("Pluginy načteny z: {}, počet: {}", cesta.toAbsolutePath(), pluginy.size());
                    break;
                }
            }
            
            if (pluginy.isEmpty()) {
                logger.warn("Žádné pluginy nenalezeny! Prohledávané cesty:");
                for (Path cesta : cesty) {
                    logger.warn("  - {}", cesta.toAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Chyba při přegenerování pluginů", e);
        }
    }
}
