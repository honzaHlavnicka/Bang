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
    CERVENE("cervene"),ZELENE("zelene"),KULE("kule"),ZALUDY("zalud");
    String imagePrefix;
    private PrsiBarva(String imagePrefix){
        this.imagePrefix = imagePrefix;
    }
    public String getImagePrefix(){
        return imagePrefix;
    }
}
