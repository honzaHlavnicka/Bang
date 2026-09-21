package cz.honzaa.bang.javascript;

import cz.honzaa.bang.sdk.Chyba;
import cz.honzaa.bang.sdk.HerniPlugin;
import cz.honzaa.bang.sdk.HerniPravidla;
import cz.honzaa.bang.sdk.Hra;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author honza
 */
public class PolyglotPlugin implements HerniPlugin{
    private final String jmeno;
    private final String popis;
    private final String URLPravidel;
    private final PluginManifest manifest;
    private final Path slozkaPluginu;
    
    private final Source zdrojak;

    public PolyglotPlugin(PluginManifest manifest, Source zdrojak, Path slozkaPluginu) {
        this.jmeno = manifest.nazev();
        this.popis = manifest.popis();
        this.URLPravidel = manifest.URLPravidel();
        this.zdrojak = zdrojak;
        this.manifest = manifest;
        this.slozkaPluginu = slozkaPluginu;
    }

    public PolyglotPlugin(PluginManifest manifest, Source zdrojak) {
        this(manifest, zdrojak, null);
    }
    
    @Override
    public String getPreklady(String jazyk) {
        if (slozkaPluginu != null) {
            String[] hledaneJazyky = new String[]{
                (jazyk != null && !jazyk.isEmpty()) ? jazyk.toLowerCase() : "en",
                "en",
                "cs"
            };
            for (String lang : hledaneJazyky) {
                Path localeFile = slozkaPluginu.resolve("locales").resolve(lang + ".json");
                if (Files.exists(localeFile)) {
                    try {
                        return Files.readString(localeFile).trim();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return HerniPlugin.super.getPreklady(jazyk);
    }
    
    
    
    
    @Override
    public String getJmeno() {
        return jmeno;
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public String getURLPravidel() {
        return URLPravidel;
    }

    @Override
    public HerniPravidla vytvor(Hra hra) {
        Context kontextProTutoHru = Tovarna.vytvorBezpecnyKontext(manifest.jazyk());
        Value jsGlobalniProstor = kontextProTutoHru.getBindings(manifest.jazyk());
        jsGlobalniProstor.putMember("Nastroje", new NastrojePluginu());
        jsGlobalniProstor.putMember("Chyba", Chyba.class);
        kontextProTutoHru.eval(zdrojak);
        Value objektPravidel = jsGlobalniProstor.getMember("PravidlaPluginu");

        if (objektPravidel == null) {
            throw new RuntimeException("Kritická chyba pluginu: Ve skriptu chybí hlavní objekt 'PravidlaPluginu'!");
        }

        return new PoligotHerniPravidla(hra, objektPravidel, kontextProTutoHru);
    }
    
}
