package cz.honza.bang.javascript;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class PluginJs {
    public static void main(String[] args) {
        System.out.println("Startuji JS Sandbox...");

        // Vytvoříme naši krabičku
        try (Context context = Tovarna.vytvorBezpecnyKontext("js")) {
            
            // Jednoduchý JS kód, který nadefinuje funkci a hned ji zavolá
            String jsKod = "function pozdrav(jmeno) { return 'Ahoj ' + jmeno + ', hlásí se JS plugin!'; }";
            context.eval("js", jsKod);

            // Zkusíme funkci zavolat z Javy
            Value funkcePozdrav = context.getBindings("js").getMember("pozdrav");
            String vysledek = funkcePozdrav.execute("Honzo").asString();

            System.out.println("Výsledek z JavaScriptu: " + vysledek);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}