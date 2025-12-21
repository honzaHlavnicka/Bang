/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public interface HerniPlugin {
    public String getJmeno();
    public String getPopis();
    public String getURLPravidel();
    public HerniPravidla vytvor(Hra hra);
}
