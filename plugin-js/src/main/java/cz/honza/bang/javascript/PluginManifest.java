/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.javascript;

/**
 *
 * @author honza
 */
public record PluginManifest (
        String nazev,
        String autor,
        String jazyk,
        String popis,
        String URLPravidel,
        String verze,
        String spousteciSoubor
        ) {
}