package cz.honza.bang.pravidla;

import cz.honza.bang.Hra;
import cz.honza.bang.pravidla.PravidlaUNO;
import cz.honza.bang.pravidla.HerniPravidla;
import cz.honza.bang.pravidla.PravidlaBangu;
import cz.honza.bang.pravidla.PravidlaVolna;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author honza
 */
/*
public class SpravceHernichPravidel {

    private static final class InfoOHre {
        private final int id;
        private final String jmeno;
        private final String popis;
        private final Function<Hra, HerniPravidla> tvurce;

        public InfoOHre(int id, String jmeno, String popis, Function<Hra, HerniPravidla> tvurce) {
            this.id = id;
            this.jmeno = jmeno;
            this.popis = popis;
            this.tvurce = tvurce;
        }

        public int id() {
            return id;
        }

        public String jmeno() {
            return jmeno;
        }

        public String popis() {
            return popis;
        }

        public Function<Hra, HerniPravidla> tvurce() {
            return tvurce;
        }
    }

    private static final List<InfoOHre> HRY = List.of(
        new InfoOHre(
                0,
                "Bang!",
                "Karetní hra ze světa Divokého západu, kde hráči mají skryté role a snaží se přežít.",
                PravidlaBangu::new
        ),
        new InfoOHre(
                1,
                "UNO",
                "Rychlá karetní hra, kde se snažíš zbavit všech svých karet tím, že hraješ podle barvy nebo čísla.",
                PravidlaUNO::new
        ),
        new InfoOHre(
                2,
                "Volná hra",
                "Hra, která neblokuje téměř nic a obsahuje někajé náhodné karty",
                PravidlaVolna::new
        ),
        new InfoOHre(
                3,
                "Prší",
                "Česká tradiční hra, ve které se snažíte zbavit všech karet, které můžete zahrát podle stejné barvy nebo hodnoty",
                PravidlaPrsi::new
        )
    );

    public static String getJSONVytvoritelneHry() {
        return HRY.stream()
                .map(h -> String.format(
                        "{\"id\":%d,\"jmeno\":\"%s\",\"popis\":\"%s\"}",
                        h.id(), escapeJSON(h.jmeno()), escapeJSON(h.popis())
                ))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static HerniPravidla vytvorHerniPravidla(int id, Hra hra) {
        return HRY.stream()
                .filter(h -> h.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Neznáme ID hry: " + id))
                .tvurce()
                .apply(hra);
    }

    private static String escapeJSON(String text) {
        return text.replace("\"", "\\\"");
    }
}
*/


public class SpravceHernichPravidel {

    private static final List<HerniPlugin> pluginy = new ArrayList<>();

    static {
        try {
            pluginy.addAll(NacitacPluginu.nactiPluginy(Paths.get("plugins")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJSONVytvoritelneHry() {
        AtomicInteger id = new AtomicInteger(); //int nefunguje a chatGPT doporučil toto

        return pluginy.stream()
                .map(p -> {
                    int myId = id.getAndIncrement();
                    return String.format(
                            "{\"id\":%d,\"jmeno\":\"%s\",\"popis\":\"%s\"}",
                            myId, escapeJSON(p.getJmeno()), escapeJSON(p.getPopis())
                    );
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static HerniPravidla vytvorHerniPravidla(int id, Hra hra) {
        return pluginy.get(id).vytvor(hra);
    }

    private static String escapeJSON(String text) {
        return text.replace("\"", "\\\"");
    }
}
