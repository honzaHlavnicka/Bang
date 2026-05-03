package cz.matous.milostny_dopis.karty;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.HratelnaKarta;
import cz.honza.bang.sdk.Karta;

/**
 * Hraběnka (hodnota 8, 1 kus)
 *
 * Hraběnka nemá žádný efekt při zahraní.
 * Musíte ji zahrát, pokud máte zároveň v ruce Krále nebo Prince.
 * (Toto pravidlo hlídá museZahrat() v MilostnyDopisPravidla.)
 */
public class Hrabenka extends Karta implements HratelnaKarta {

    public Hrabenka(Hra hra, Balicek<Karta> balicek) { super(hra, balicek); }

    @Override public String getJmeno()   { return "Hrabenka"; }
    @Override public String getObrazek() { return "hrabenka"; }

    @Override
    public boolean odehrat(Hrac kym) {
        // Žádný efekt → poOdehrani() posune tah automaticky
        return true;
    }
}