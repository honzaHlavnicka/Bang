/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.PovolenePluginu;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.ResourceLimits;
import org.graalvm.polyglot.Value;



/**
 *
 * @author honza
 */

import cz.honza.bang.sdk.PovolenePluginu;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.ResourceLimits;

/**
 * @author honza
 */
public class Tovarna {

    private static final Engine SDILENY_ENGINE = Engine.newBuilder()
            // Zde můžeme přidat optimalizace pro Engine
            .build();

    private static final HostAccess MOJE_PRAVIDLA = HostAccess.newBuilder()
            .allowAccessAnnotatedBy(PovolenePluginu.class)
            .build();

    /**
     * Vytvoří a vrátí zcela izolovaný kontext.
     *
     * @param jazyky Jazyky, které má kontext podporovat (např. "js", "python").
     */
    public static Context vytvorBezpecnyKontext(String... jazyky) {

        // 2. Místo "js" vložíme pole povolených jazyků
        return Context.newBuilder(jazyky)
                .engine(SDILENY_ENGINE)
                .allowAllAccess(false) // Základní zákaz (síť, disk apod.)
                .allowHostAccess(MOJE_PRAVIDLA) // 3. Vložíme naši vybudovanou politiku
                .resourceLimits(ResourceLimits.newBuilder() // ochrana proti nekonečným cyklům
                        .statementLimit(10000, null)
                        .build())
                // Nastavení pro JS. GraalVM ho chytře ignoruje, pokud jazyk není načtený.
                .option("js.ecmascript-version", "2022")
                .build();
    }
    
    /**
     * Vrátí třídu dědící z Karta, která bude obsahovat logyku z js objektu. 
     * Metoda zajistí, že karta bude mít správně přiřazeené rozhraní HratelnaKarta
     * a VylozitelnaKarta.
     * 
     * @param hra hra do které karta patří
     * @param balicek balíček, do kterého karta patří. K ničemu se pravděpodobně nepoužívá, ale Karta ho potřebuje.
     * @param jsObjekt JS objekt reprezentující kartu, obsahující její funkce.
     * @return 
     */
    public static Karta vytvorKartuZJs(Hra hra, Balicek<Karta> balicek, Value jsObjekt) {

        boolean maOdehrat = jsObjekt.hasMember("odehrat");
        boolean maVylozit = jsObjekt.hasMember("vylozit");

        // Zde záleží na pořadí! První musíme zkontrolovat, zda neumí obojí.
        if (maOdehrat && maVylozit) {
            return new PolyglotHybridniKarta(hra, balicek, jsObjekt);
        } else if (maOdehrat) {
            return new PolyglotHratelnaKarta(hra, balicek, jsObjekt);
        } else if (maVylozit) {
            return new PolyglotVylozitelnaKarta(hra, balicek, jsObjekt);
        }

        return new PolyglotKarta(hra, balicek, jsObjekt) {
        };
    }
}
