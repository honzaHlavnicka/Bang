
package cz.honza.bang.sdk;


/**
 * Tento interface by měla implementovat pouze třída, která dědí z {@link Karta karty}.
 * Přidáním tohoto interfacu se umožní kartu hráčům zahrát. Před zahráním proběhnou
 * nějaké kontroli pravidel hry a podobně a zavolá se {@link #odehrat(cz.honza.bang.sdk.Hrac)}.
 * <p>
 * Určena pro implementaci od autora pluginu.
 * @author honza
 */
public interface HratelnaKarta {
    /**
     * Metoda, která se zavolá při pokusu o odehrání karty.
     * V době co se volá už byli zkontrolovány pravidla hry
     * a jiné výjimky. Tato metoda by měla provést požadovanou
     * akci karty a vrátit true, jakože karta byla zahrána úspěšně.
     * Pokud se karta v současném kontextu zahrát nemůže, tak se
     * vrátí false. Po vrácení false se karta hráčovi vrátí do ruky.
     * 
     * @param kym Kdo kartu zahrál
     * @return byla karta odehrána?
     */
    public boolean odehrat(Hrac kym);
}
