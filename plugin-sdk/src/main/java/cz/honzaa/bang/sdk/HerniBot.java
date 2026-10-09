package cz.honzaa.bang.sdk;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Rozhraní pro implementaci herního bota (AI hráče).
 * Každý herní plugin může poskytnout vlastní specializovanou implementaci bota přes HerniPravidla.vytvorBota(Hra, Hrac).
 */
public interface HerniBot {

    /**
     * Zavolá se, když je bot na tahu a má provést herní akci (např. odehrát kartu, líznout nebo ukončit tah).
     * @param hra instance hry
     * @param ja hráč reprezentující tohoto bota
     */
    @PovolenePluginu
    void naTahu(Hra hra, Hrac ja);

    /**
     * Zavolá se, když hra vyžaduje výběr karet (např. Hokynářství, Cat Balou, Kit Carlson, reakce na Bang).
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
     * Zavolá se, když hra vyžaduje výběr cílového hráče (např. Bang, Panika, Duel).
     * @param hra instance hry
     * @param ja hráč reprezentující bota
     * @param hraci seznam hráčů na výběr
     * @param nadpis popis výběru
     * @param min minimální počet
     * @param max maximální počet
     * @return CompletableFuture s ID vybraného hráče
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
}
