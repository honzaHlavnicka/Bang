package cz.honzaa.bang.sdk;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Rozhraní pro implementaci herního bota (AI hráče).
 * Každý herní plugin může poskytnout vlastní implementaci bota přes HerniPravidla.vytvorBota(Hra, Hrac).
 */
public interface HerniBot {

    /**
     * Zavolá se, když je bot na tahu.
     * @param hra instance hry
     * @param ja hráč reprezentující tohoto bota
     */
    @PovolenePluginu
    void naTahu(Hra hra, Hrac ja);

    /**
     * Zavolá se, když hra vyžaduje výběr karet, spouští to {@link KomunikatorHry#pozadejOKarty(Hrac, List, String, int, int)}.
     * @param hra instance hry
     * @param ja hráč reprezentující bota
     * @param karty karty nabízené k výběru
     * @param nadpis popis výběru
     * @param min minimální počet karet k výběru
     * @param max maximální počet karet k výběru
     * @return CompletableFuture s ID vybrané karty (nebo čárkami oddělenými ID pro vícenásobný výběr)
     */
    @PovolenePluginu
    default CompletableFuture<String> pozadavekNaKarty(Hra hra, Hrac ja, List<Karta> karty, String nadpis, int min, int max) {
        if (karty == null || karty.isEmpty()) {
            return CompletableFuture.completedFuture("");
        }
        return CompletableFuture.completedFuture(String.valueOf(karty.get(0).getId()));
    }

    /**
     * Zavolá se, když hra vyžaduje výběr cílového hráče.
     * Výchozí implementace vrátí 1. hráče kromě this. To neení moc užitečné. 
     * 
     * @param hra instance hry
     * @param ja hráč reprezentující bota
     * @param hraci seznam hráčů na výběr
     * @param nadpis popis výběru
     * @param min minimální počet
     * @param max maximální počet
     * @return CompletableFuture s ID vybraného hráče, popřípadě hráčů odděených čárkami.
     */
    @PovolenePluginu
    default CompletableFuture<String> pozadavekNaHrace(Hra hra, Hrac ja, List<Hrac> hraci, String nadpis, int min, int max) {
        if (hraci == null || hraci.isEmpty()) {
            return CompletableFuture.completedFuture("");
        }
        for (Hrac h : hraci) {
            if (h != null && !h.equals(ja)) {
                return CompletableFuture.completedFuture(String.valueOf(h.getId()));
            }
        }
        return CompletableFuture.completedFuture(String.valueOf(hraci.get(0).getId()));
    }

    /**
     * Zavolá se, když hra vyžaduje výběr z textových možností (např. volba barvy u svrška, ano/ne).
     * @param hra instance hry
     * @param ja hráč reprezentující bota
     * @param moznosti seznam textových možností
     * @param nadpis popis volby
     * @return CompletableFuture s indexem vybrané možnosti ("0", "1", ...)
     */
    @PovolenePluginu
    default CompletableFuture<String> pozadavekNaMoznosti(Hra hra, Hrac ja, List<String> moznosti, String nadpis) {
        return CompletableFuture.completedFuture("0");
    }

    /**
     * Zavolá se, když hra vyžaduje zadání textu.
     */
    @PovolenePluginu
    default CompletableFuture<String> pozadavekNaText(Hra hra, Hrac ja, String nadpis, String placeholder) {
        return CompletableFuture.completedFuture("");
    }
    
    /**
     * Vrátí dobu, kterou má boot čekat mezi akcemi.
     * @return čas v milisekundách
     */
    @PovolenePluginu
    default long casMeziTahy(){
        return 300L;
    }
    
    /**
     * Odchytává zprávu, kterou by se pokusil poslat server
     * konkrétnímu klientovi. Většinou by nemělo být potřeba, protože
     * posílá často věci, které jdou číst přímo ze serveru, ale na nějaké
     * nízkoúrovnov prijimani událostí se může hodit. Neposílá se pokud 
     * je zpráva určenáá všem.
     * 
     * <p>
     * Nikdy nesmí dělat akci, která něco posílá hráči, bez podmínky
     * na konkrétní událost, protože by došlo k zacyklení a plugin
     * spadne.
     * <p>
     * Protokol, ve kterém se obsah posílá není definován v plugin-sdk
     * a záleží na konkrétní implementaci serveru, takže použitím této
     * metody se ztratí nějaká modularita. Současný výchozí server
     * (říjen 2026) má dokumentci na
     * https://github.com/honzaHlavnicka/Bang/tree/master/docs/protocol
     * a funguje způsobem "typZpravy:obsah", obsah je budto json, nebo
     * hodnoty oddelene carkami, nebo cokoliv jineho.
     * <p>
     * // TODO: udělat aby se posílala i když je pro všechny.
     * 
     * @param ja Hráč, kterému je zprráva určena
     * @param obsah Obsah zprávy podle protokolu.
     */
    @PovolenePluginu
    default void poslanaZprava(Hrac ja, String obsah){}
}
