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
public interface VylozitelnaKarta{
    public boolean vylozit(Hrac predKoho,Hrac kym);
    public Efekt getEfekt();
    public void spalitVylozenou();
}
