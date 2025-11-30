/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;

import cz.honza.bang.Hra;

/**
 *
 * @author honza
 */
public interface HerniPlugin {
    public String getJmeno();
    public String getPopis();
    public HerniPravidla vytvor(Hra hra);
}
