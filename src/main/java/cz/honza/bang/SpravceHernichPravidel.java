/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author honza
 */
public class SpravceHernichPravidel {

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
