package cz.honza.bang.javascript;

import cz.honza.bang.sdk.Chyba;
import cz.honza.bang.sdk.HerniPlugin;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
 * @author honza
 */
public class PolyglotPlugin implements HerniPlugin{
    private final String jmeno;
    private final String popis;
    private final String URLPravidel;
    private final PluginManifest manifest;
    
    private final Source zdrojak;

    public PolyglotPlugin(PluginManifest manifest, Source zdrojak) {
        this.jmeno = manifest.nazev();
        this.popis = manifest.popis();
        this.URLPravidel = manifest.URLPravidel();
        this.zdrojak = zdrojak;
        this.manifest = manifest;
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
