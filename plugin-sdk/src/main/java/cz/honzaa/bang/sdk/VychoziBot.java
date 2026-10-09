package cz.honzaa.bang.sdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Výchozí obecná implementace bota pro libovolnou hru.
 */
public class VychoziBot implements HerniBot {

    @Override
    public void naTahu(Hra hra, Hrac ja) {
        if (ja == null || !ja.jeNaTahu() || !ja.jeZivy()) {
            return;
        }

        HerniPravidla pravidla = hra != null ? hra.getHerniPravidla() : null;
        if (pravidla == null) {
            ja.konecTahu();
            return;
        }

        // 1. Zkusit projít karty v ruce a odehrát hratelné karty
        boolean zahralNeco;
        int maxIteraci = 10;
        do {
            zahralNeco = false;
            List<Karta> karty = new ArrayList<>(ja.getKarty());
            for (Karta karta : karty) {
                if (karta instanceof HratelnaKarta && pravidla.muzeZahrat(karta, ja)) {
                    ja.odehranaKarta(String.valueOf(karta.getId()));
                    zahralNeco = true;
                    break;
                }
            }
            maxIteraci--;
        } while (zahralNeco && ja.jeNaTahu() && maxIteraci > 0);

        // 2. Pokud je stále na tahu a nic nezahrál, zkusit líznout
        if (ja.jeNaTahu()) {
            if (pravidla.hracChceLiznout(ja)) {
                return;
            }
        }

        // 3. Pokud je stále na tahu, ukončit tah
        if (ja.jeNaTahu()) {
            // Pokud pravidla dovolují ukončit tah
            if (pravidla.hracChceUkoncitTah(ja)) {
                return;
            }

            // Pokud nemůže ukončit tah (např. v Bang musí spálit přebytečné karty)
            while (ja.getKarty().size() > ja.getZivoty() && !ja.getKarty().isEmpty() && ja.jeNaTahu()) {
                Karta naSpaleni = ja.getKarty().get(0);
                ja.spalitKartu(String.valueOf(naSpaleni.getId()));
            }
            ja.konecTahu();
        }
    }
}
