/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public interface Postava {
    String getJmeno();
    
    /**
     * Jedinečný identifikátor, zároven nazev obrázku
     * @return 
     */
    String name();
    String getPopis();
    int getMaximumZivotu();
    default void pridaniPostavy(Hrac komu){};
    default void odebraniPostavy(Hrac komu){};

}
