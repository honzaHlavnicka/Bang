/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.sdk;

/**
 * HerníPlugin je základní třída, kterou musí každý plugin implementovat.
 * Pokud ji impplementuje a umístí jar soubor do složky /pluginy/ vůči místa spuštění serveru,
 * tak bude automaticky načten a nabízen hráčům.
 * 
 * Na jeden server se vytváří jedna instance tohoto objektu. Nemělo by být potřeba, aby třída něco složitého
 * vykonávala, hodnoty může vracet přímo.
 * 
 * Může se hodit mít více pluginů, které budou vytvářet jedny herní pravidla s jiným parametrem pro různé varianty hry,
 * bez potřeby psát celou logiku vícekrát.
 * 
 * Plugin MUSÍ mít konstruktor BEZ parametrů, jinak nebude načten.
 * 
 * Plugin je lidově jedna hra, ne konkrétní, například Bang, Uno, Prší... 
 * 
 * Určena pro implementaci od autora pluginu.
 * @author honza
 */
public interface HerniPlugin {
    /**
     * Jméno hry, které se ukáže uživatelům při výběru.
     * @return 
     */
    public String getJmeno();
    /**
     * Středně dlouhý popis hry. Ne všechyn pravidla, ale aby uživatel pochopil o co se jedná,
     * měl by na hru nalákat před vytvořením
     * @return 
     */
    public String getPopis();
    /**
     * Odkaz na pravidla hry (pdf, html, ne třída PravidlaHry) umístěné někde na internetu.
     * @return 
     */
    public String getURLPravidel();
    
    /**
     * Nejduležitější metoda, která vytvoří a vrátí {@link HerniPravidla Herní pravidla}. Vytvořené pravidla, ani parametr hra by se
     * NEMĚLI nikam UKLÁDAT, protože by na serveru po nějaké době došlo k zaplnění operační paměti.
     * 
     * Přestože to není vyžadováno, tak by se mohlo hodit herním pravidlům předat parametr hra, aby se mohl v budoucnu odkazovat na metody metody enginu.
     * @param hra Hra, ke které herní pravidla budou patřit. Hra a pravidla jsou napevno svázané a nikdy nebudou vyměněny.
     * @return 
     */
    public HerniPravidla vytvor(Hra hra);

    /**
     * Vrátí JSON s překlady pro zadaný jazyk (např. "cs", "en").
     * Výchozí implementace načte locales/{jazyk}.json z resources pluginu, s fallbackem na en.json a cs.json.
     * @param jazyk Kód jazyka (např. "cs", "en")
     * @return JSON řetězec s překlady nebo "{}" pokud není nalezen
     */
    @PovolenePluginu
    default String getPreklady(String jazyk) {
        String[] hledaneJazyky = new String[]{
            (jazyk != null && !jazyk.isEmpty()) ? jazyk.toLowerCase() : "en",
            "en",
            "cs"
        };
        
        for (String lang : hledaneJazyky) {
            String cesta = "/locales/" + lang + ".json";
            try (java.io.InputStream is = getClass().getResourceAsStream(cesta)) {
                if (is != null) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        return sb.toString().trim();
                    }
                }
            } catch (Exception ignored) {
            }

            String cestaBezLomitka = "locales/" + lang + ".json";
            ClassLoader cl = getClass().getClassLoader();
            if (cl != null) {
                try (java.io.InputStream is = cl.getResourceAsStream(cestaBezLomitka)) {
                    if (is != null) {
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                            return sb.toString().trim();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return "{}";
    }
}
