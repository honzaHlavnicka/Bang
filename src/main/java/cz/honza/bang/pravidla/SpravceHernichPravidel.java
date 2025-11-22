/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;

import cz.honza.bang.Hra;
import cz.honza.bang.pravidla.PravidlaUNO;
import cz.honza.bang.pravidla.HerniPravidla;
import cz.honza.bang.pravidla.PravidlaBangu;
import cz.honza.bang.pravidla.PravidlaVolna;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author honza
 */
public class SpravceHernichPravidel {
    private record InfoOHre(int id, String jmeno, String popis, Function<Hra, HerniPravidla> tvurce){}

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
            new InfoOHre(2,
                    "Volná hra",
                    "Hra, která neblokuje téměř nic a obsahuje někajé náhodné karty",
                    PravidlaVolna::new 
            ),
            new InfoOHre(3,
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
