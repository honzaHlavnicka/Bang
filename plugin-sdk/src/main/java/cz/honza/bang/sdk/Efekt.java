package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public interface Efekt {
    default void naZacatekTahu(Hra hra, Hrac hrac) {}
    default void naKonecTahu(Hra hra, Hrac hrac) {}
    default void poZtrateZivota(Hra hra, Hrac hrac) {}
    default void poOdehraniKarty(Hra hra, Hrac hrac) {}
    default void kdyzNemaKarty(Hra hra, Hrac hrac) {}
    default void poZabitiKohokoliv(Hrac ja,Hrac zabity){}
    
    void odebrani(Hrac odKoho);
    void prirazeni(Hrac komu);
    
}
