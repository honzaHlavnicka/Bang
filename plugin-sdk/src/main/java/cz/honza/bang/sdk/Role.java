package cz.honza.bang.sdk;

/**
 * Role. Položka kterou si může plugin hráčům pro vlastní potřebu jakkoliv přiřazovat.
 * Každému hráči se ukazuje jeho vlastní role, ale ne role ostatních.
 * 
 * Může se jedna instance použít vícekrát. Ideální je použít enum.
 * 
 * Určena pro implementaci od autora pluginu.
 * @author honza
 */
public interface Role {
    /**
     * Identifikátor role, zároven jmno souboru s obázkem.
     * @return 
     */
    String name();    
}
