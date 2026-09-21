/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honzaa.bang.pluginy.prsi;

/**
 *
 * @author honza
 */
public enum PrsiBarva {
    CERVENE("cervene","červené"),ZELENE("zelene","zelené"),KULE("kule","kule"),ZALUDY("zalud", "žaludy");
    String imagePrefix;
    String nazev;
    private PrsiBarva(String imagePrefix, String nazev){
        this.imagePrefix = imagePrefix;
        this.nazev = nazev;
    }
    public String getImagePrefix(){
        return imagePrefix;
    }

    public String getNazev() {
        return nazev;
    }

    public String getTranslationKey() {
        switch (this) {
            case CERVENE: return "prsi.color_cervene";
            case ZELENE: return "prsi.color_zelene";
            case KULE: return "prsi.color_kule";
            case ZALUDY: return "prsi.color_zaludy";
            default: return "prsi.color_cervene";
        }
    }
}
