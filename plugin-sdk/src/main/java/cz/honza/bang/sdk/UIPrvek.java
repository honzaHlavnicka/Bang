package cz.honza.bang.sdk;

/**
 * Prvky UI, které by mohli u klienta existovat.
 * Toto je server, takže by UI neměl řešit, ale konkrétní hry potřebují klientovy říct, jaké prvky potřebují a jaké se mohou skrýt.
 * @author honza
 */
public enum UIPrvek implements Misto{
    ZIVOTY,UKONCENI_TAHU,POSTAVA,ROLE,VYLOZENE_KARTY,ODHAZOVACI_BALICEK,DOBIRACI_BALICEK, OHEN;

    @Override
    public String getIdentifikatorMista() {
        return name();
    }
    
}