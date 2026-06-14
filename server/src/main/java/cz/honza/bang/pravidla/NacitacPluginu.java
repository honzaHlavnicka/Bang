package cz.honza.bang.pravidla;

import cz.honza.bang.sdk.HerniPlugin;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.Modifier;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.honza.bang.javascript.PluginManifest;
import cz.honza.bang.javascript.PolyglotPlugin;
import org.graalvm.polyglot.Source;


/**
 *
 * @author honza
 */
public class NacitacPluginu {
    private static final Logger logger = LoggerFactory.getLogger(NacitacPluginu.class);

    /**
     * Vrátí všechny pluginy nacházející se v dané složce. Prohledá složku a najde všechny <code>HerniPlugin</code> v každěm JARu.
     * @param cestaKeSlozce cesta ke složce, ve které se nachází pluginy.
     * @return Seznam všech načetlích pluginů
     * @throws Exception
     */
    public static List<HerniPlugin> nactiPluginy(Path cestaKeSlozce) throws Exception {
        List<HerniPlugin> pluginy = new ArrayList<>();
        
        //Nemůžeme prohledávat neexistující složku, naštěstí v ní nic být ani nemůže.
        if (!Files.exists(cestaKeSlozce)) {
            logger.warn("Nenalezena složka pro pluginy: {}", cestaKeSlozce.toAbsolutePath());
            return pluginy;
        }
        logger.info("Nalezena složka pro pluginy: {}", cestaKeSlozce.toAbsolutePath());

        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cestaKeSlozce, "*.jar")) {
            for (Path cestaKJARu : stream) {
                pluginy.addAll(nactiPluginyZJARu(cestaKJARu));
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cestaKeSlozce)) {
            for (Path prvek : stream) {
                
                if (Files.isDirectory(prvek)) {
                    logger.debug("Našel jsem složku");
                    Path potencialniManifest = prvek.resolve("plugin.json");

                    if (Files.exists(potencialniManifest)) {
                        logger.debug("Načítám plugin.json");
                        pluginy.addAll(nactiPluginyZManifestu(potencialniManifest));
                    }
                }
            }
        }
        
        

        return pluginy;
        
    }
    
    public static List<HerniPlugin> nactiPluginyZJARu(Path cesta) throws Exception{
        List<HerniPlugin> pluginy = new ArrayList<>();
        
    try (URLClassLoader classLoader = new URLClassLoader(
        new URL[]{cesta.toUri().toURL()},
        NacitacPluginu.class.getClassLoader()
    ); JarFile jar = new JarFile(cesta.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry polozkaJARu = entries.nextElement();

                if (polozkaJARu.isDirectory()) {
                    continue;
                }
                if (!polozkaJARu.getName().endsWith(".class")) {
                    continue;
                }

                String nazevTridy = polozkaJARu.getName() //Název třídy odpovídá cestě, ale místo lomítek používá tečky a neobsahuije .class příponu
                        .replace("/", ".")
                        .replace(".class", "");

                Class<?> c = classLoader.loadClass(nazevTridy);

                if (HerniPlugin.class.isAssignableFrom(c)
                        && !c.isInterface()
                        && !Modifier.isAbstract(c.getModifiers())) {

                    HerniPlugin plugin = (HerniPlugin) c.getDeclaredConstructor().newInstance();
                    pluginy.add(plugin);
                }
            }
        }
        return pluginy;
    }
    
    
    
    private static List<HerniPlugin> nactiPluginyZManifestu(Path cesta) {
        List<HerniPlugin> pluginy = new ArrayList<>();

        try {
            // otevření souuboru
            String jsonText = Files.readString(cesta);

            // kontrola, zda je manifest.json (parsování JSONu přes org.json)
            JSONObject json = new JSONObject(jsonText);

            // načtení do record
            PluginManifest manifest = new PluginManifest(
                json.getString("nazev"),
                json.optString("autor", "Neznámý autor"),
                json.optString("jazyk", "js"),
                json.optString("popis", json.getString("nazev")),
                json.optString("URLPravidel", "Neznámý název"),
                json.optString("verze", "1.0.0"),
                json.optString("spousteciSoubor","main.js")
            );
            
            Path cestaKSkriptu = cesta.getParent().resolve(manifest.spousteciSoubor());
            
            if (!Files.exists(cestaKSkriptu)) {
                logger.error("Vstupní skript {} pro plugin {} nebyl nalezen vedle manifestu!", 
                             manifest.spousteciSoubor(), manifest.nazev());
                return pluginy; // Vracíme prázdný list, plugin neprošel validací
            }

            String zdrojovyKod = Files.readString(cestaKSkriptu);
            
            Source zdroj = Source.newBuilder(
                    manifest.jazyk(),
                    zdrojovyKod,
                    manifest.spousteciSoubor()
            ).build();
            
            
            // Instancování polyglot pluginu
            HerniPlugin plugin = new PolyglotPlugin(manifest, zdroj);
            pluginy.add(plugin);
            
            logger.info("Načten skriptovaný plugin: {} (v{})", manifest.nazev(), manifest.verze());

        } catch (Exception e) {
            logger.error("Chyba při načítání pluginu z manifestu {}: {}", cesta.getFileName(), e.getMessage());
        }
        
        return pluginy;
    }
    
}
