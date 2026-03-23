package cz.honza.bang.sdk;


import cz.honza.bang.sdk.Karta;

/**
 * Nějaké užitečné karty, které se mohou hodit do dialogů při požadování karet od hráče jako zástupce něčeho abstraktního, ale NEměli 
 * by se dávat do balíčku, hrát apod.
 * 
 * @author honza
 */
public class ZastupnaKarta extends Karta {
    private final String obrazek;
    private final String jmeno;

    private ZastupnaKarta(String obrazek, String jmeno) {
        super(null, null);
        this.obrazek = obrazek;
        this.jmeno = jmeno;
    }
    
    private static final ZastupnaKarta nahodna;
    private static final ZastupnaKarta zivot;
    private static final ZastupnaKarta smrt;
    
    static{
        nahodna = new ZastupnaKarta("nahodna","Reprezentace náhodné karty");
        zivot = new ZastupnaKarta("jedenZivot", "Reprezentace života");
        smrt = new ZastupnaKarta("smrt", "Reprezentace smrti");
    }
    
    public static ZastupnaKarta getNahodna(){
        return nahodna;
    }
    public static ZastupnaKarta getZivot(){
        return zivot;
    }
    public static ZastupnaKarta getSmrt(){
        return smrt;
    }
    
            
            
    @Override
    public String getObrazek() {
        return obrazek;
    }

    @Override
    public String getJmeno() {
        return jmeno;
    }
    
}
