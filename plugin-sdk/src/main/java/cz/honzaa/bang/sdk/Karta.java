package cz.honzaa.bang.sdk;

/**
 * Objekt karty reprezentuje jednu konkrétní fyzickou kartu ve hře.
 * Karta sama o sobě nic neumí, může být v balíčku, může se lízat a
 * může být spálena. Pokud chce autor pluginu udělat kartu hratelnou,
 * tak jeho třída musí dědit z této třídy a zárověn implementovat 
 * {@link HratelnaKarta hratelnou kartu}. Pokud ji chce vyložitelnou,
 * tak musí implementovat {@link VylozitelnaKarta vyložitelnou kartu}
 * <p>
 * Každá Karta má své vlastní jedinečné id, podle kteerého jde identifikovat.
 * Karta vám také poskytuje přístup k vlastnosti <code>hra</code>, která
 * odkazuje na současnou hru. Jde se z ní dostat ke všem hráčům, komunikátoru
 * a podobně. Karta vám taky poskytuje vlastnost Balicek, která je ale
 * nicneříkající, protože se nastavuje pouze jednou při přípravě lízacího
 * balíčku.
 * 
 * <p>
 * Určena pro dědění od autora pluginu.
 * @author honza
 */
public abstract class Karta{
    protected Hra hra;
    @Deprecated
    protected Balicek<Karta> balicek;
    static private int nextId = 0;
    private final int id;
    
    
    public Karta(Hra hra, Balicek<Karta> balicek) {
        this.hra = hra;
        this.balicek = balicek;
        id = nextId;
        nextId ++;
    }
    
    
    /**
     * Vrátí unikátní id karty.
     * @return 
     */
    @PovolenePluginu
    public final int getId(){
        return id;
    }
    
    /**
     * Escapuje text pro bezpečné vložení do JSONu (ošetření uvozovek, zpětných lomítek a řídicích znaků).
     * Znaky jako < a > zůstávají zachovány.
     * @param text Vstupní text
     * @return Text bezpečný pro JSON hodnotu
     */
    @PovolenePluginu
    public static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32 || c == 127) {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Vrací informace o kratě ve formátu json
     * @return json ve formátu: novaKarta:{"jmeno":jmeno,"obrazek":obrazek,"id":id}
     */
    @Deprecated
    public final String toJSONold(){
        StringBuilder sb = new StringBuilder("novaKarta:{\"jmeno\":\"");
        sb.append(escapeJson(this.getJmeno()));
        sb.append("\",\"obrazek\":\"");
        sb.append(escapeJson(this.getObrazek()));
        sb.append("\",\"id\":");
        sb.append(id);
        sb.append("}");
        return sb.toString();
    }
    
    
    /**
     * Vrací informace o kratě ve formátu json
     *
     * @return json ve formátu: {"jmeno":jmeno,"obrazek":obrazek,"id":id}
     */
    @PovolenePluginu
    public final String toJSON() {
        StringBuilder sb = new StringBuilder("{\"jmeno\":\"");
        sb.append(escapeJson(this.getJmeno()));
        sb.append("\",\"obrazek\":\"");
        sb.append(escapeJson(this.getObrazek()));
        sb.append("\",\"id\":");
        sb.append(id);
        sb.append(",\"vylozitelna\":");
        sb.append(this instanceof VylozitelnaKarta);
        sb.append(",\"hratelna\":");
        sb.append(this instanceof HratelnaKarta);
        sb.append("}");
        return sb.toString();
    }
    
    /**
     *  Akce, které by se měli provést před spálením.
     */
    @PovolenePluginu
    public void predSpalenim(){}
    
    /**
     * Metoda by měla vráti pojmenování obrázku nez přípony. Cesta začíná od všech karet.
     * @return 
     */
    @PovolenePluginu
    public abstract String getObrazek();
    
    /**
     * Mělo by vrátit pro lidi čitelné jméno.
     * @return 
     */
    @PovolenePluginu
    public abstract String getJmeno();
    
    /**
     * Vrátí obrázek, který se nachází zezadu karty.
     * Pokud není překryta, tak obrázek vezme z {@link HerniPravidla#getVychoziZadniObrazek() pravidel hry}
     * @return 
     */
    @PovolenePluginu
    public String getZadniObrazek(){
        return hra.getHerniPravidla().getVychoziZadniObrazek();
    }
}
