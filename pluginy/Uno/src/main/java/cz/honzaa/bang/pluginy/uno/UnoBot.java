package cz.honzaa.bang.pluginy.uno;

import cz.honzaa.bang.sdk.HerniBot;
import cz.honzaa.bang.sdk.HerniPravidla;
import cz.honzaa.bang.sdk.Hra;
import cz.honzaa.bang.sdk.Hrac;
import cz.honzaa.bang.sdk.Karta;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI bot pro hru UNO.
 */
public class UnoBot implements HerniBot {

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
            ja.lizniKontrolovane();
        }
    }

    @Override
    public CompletableFuture<String> pozadavekNaMoznosti(Hra hra, Hrac ja, List<String> moznosti, String nadpis) {
        return CompletableFuture.completedFuture("0");
    }
}
