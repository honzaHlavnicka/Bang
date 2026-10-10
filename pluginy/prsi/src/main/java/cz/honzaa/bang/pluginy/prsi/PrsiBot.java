package cz.honzaa.bang.pluginy.prsi;

import cz.honzaa.bang.sdk.HerniBot;
import cz.honzaa.bang.sdk.HerniPravidla;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AI bot pro hru Prší.
 */
public class PrsiBot implements HerniBot {

    @Override
    public void naTahu(Hra hra, Hrac ja) {

        HerniPravidla pravidla = hra != null ? hra.getHerniPravidla() : null;
        if (pravidla == null) {
            return;
        }

        List<Karta> karty = ja.getKarty();
        Karta kZahrani = null;

        for (Karta k : karty) {
            if (pravidla.muzeZahrat(k, ja)) {
                kZahrani = k;
                break;
            }
        }

        if (kZahrani != null) {
            ja.odehranaKarta(String.valueOf(kZahrani.getId()));
        } else {
            // Nemůže nic zahrát -> lízne si
            ja.lizniKontrolovane();
        }
    }

    @Override
    public CompletableFuture<String> pozadavekNaMoznosti(Hra hra, Hrac ja, List<String> moznosti, String nadpis) {
        if (moznosti == null || moznosti.isEmpty()) {
            return CompletableFuture.completedFuture("0");
        }

        // Pokud vybírá barvu po svršku, vybere barvu, které má nejvíc v ruce
        if (ja != null && ja.getKarty() != null && !ja.getKarty().isEmpty()) {
            Map<String, Integer> poctyBarev = new HashMap<>();
            for (Karta k : ja.getKarty()) {
                if (k instanceof PrsiKarta) {
                    PrsiBarva b = ((PrsiKarta) k).getBarva();
                    if (b != null) {
                        poctyBarev.put(b.name().toLowerCase(), poctyBarev.getOrDefault(b.name().toLowerCase(), 0) + 1);
                    }
                }
            }

            int bestIndex = 0;
            int maxCount = -1;
            for (int i = 0; i < moznosti.size(); i++) {
                String m = moznosti.get(i).toLowerCase();
                int count = 0;
                for (Map.Entry<String, Integer> entry : poctyBarev.entrySet()) {
                    if (m.contains(entry.getKey())) {
                        count += entry.getValue();
                    }
                }
                if (count > maxCount) {
                    maxCount = count;
                    bestIndex = i;
                }
            }
            return CompletableFuture.completedFuture(String.valueOf(bestIndex));
        }

        return CompletableFuture.completedFuture("0");
    }
}
