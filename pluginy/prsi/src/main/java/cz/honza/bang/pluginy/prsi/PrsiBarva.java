/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pluginy.prsi;

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
}
