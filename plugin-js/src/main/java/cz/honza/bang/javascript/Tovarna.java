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
 * Továrna pro vytváření bezpečných JavaScriptových kontextů.
 *
 * @author honza
 */
public class Tovarna {

    private static final Engine SDILENY_ENGINE = Engine.newBuilder()
            // Zde můžeme přidat optimalizace pro Engine
            .build();

    private static final HostAccess MOJE_PRAVIDLA = vytvorPravidla();

    private static HostAccess vytvorPravidla() {
        HostAccess.Builder b = HostAccess.newBuilder()
                .allowAccessAnnotatedBy(PovolenePluginu.class)
                .allowAllImplementations(true);
        
        // Povolíme bezpečné metody pro asynchronní programování a kolekce
        try {
            // Asynchronní operace
            pridejMetodyTridy(b, java.util.concurrent.CompletableFuture.class);
            pridejMetodyTridy(b, java.util.concurrent.CompletionStage.class);
            
            // Kolekce a iterace (Rozhraní i základní implementace)
            pridejMetodyTridy(b, java.util.Collection.class);
            pridejMetodyTridy(b, java.util.List.class);
            pridejMetodyTridy(b, java.util.ArrayList.class);
            pridejMetodyTridy(b, java.util.Map.class);
            pridejMetodyTridy(b, java.util.HashMap.class);
            pridejMetodyTridy(b, java.lang.Iterable.class);
            pridejMetodyTridy(b, java.util.Iterator.class);
            
            // Funkcionální rozhraní (pro callbacky)
            pridejMetodyTridy(b, java.util.function.Consumer.class);
            pridejMetodyTridy(b, java.util.function.Function.class);
            pridejMetodyTridy(b, java.util.function.BiConsumer.class);
            pridejMetodyTridy(b, java.util.function.BiFunction.class);
            
            // Základní metody objektu
            b.allowAccess(Object.class.getMethod("toString"));
            b.allowAccess(Object.class.getMethod("equals", Object.class));
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Kritická chyba při nastavování bezpečnosti JS: Metoda nenalezena", e);
        }
        
        return b.build();
    }

    /**
     * Pomocná metoda, která povolí všechny veřejné metody dané třídy/rozhraní.
     */
    private static void pridejMetodyTridy(HostAccess.Builder builder, Class<?> clazz) {
        for (java.lang.reflect.Method m : clazz.getMethods()) {
            builder.allowAccess(m);
        }
    }

    /**
     * Vytvoří a vrátí zcela izolovaný kontext.
     *
     * @param jazyky Jazyky, které má kontext podporovat (např. "js", "python").
     */
    public static Context vytvorBezpecnyKontext(String... jazyky) {

        return Context.newBuilder(jazyky)
                .engine(SDILENY_ENGINE)
                .allowAllAccess(false) // Základní zákaz (síť, disk apod.)
                .allowHostAccess(MOJE_PRAVIDLA)
                .resourceLimits(ResourceLimits.newBuilder() // ochrana proti nekonečným cyklům
                        .statementLimit(10000, null)
                        .build())
                .option("js.ecmascript-version", "2022")
                .build();
    }
    
    /**
     * Vrátí třídu dědící z Karta, která bude obsahovat logyku z js objektu. 
     */
    public static Karta vytvorKartuZJs(Hra hra, Balicek<Karta> balicek, Value jsObjekt) {

        boolean maOdehrat = jsObjekt.hasMember("odehrat");
        boolean maVylozit = jsObjekt.hasMember("vylozit");

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
