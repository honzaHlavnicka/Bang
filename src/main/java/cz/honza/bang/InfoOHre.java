/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;
import java.util.function.Function;
/**
 *
 * @author honza
 */
public record InfoOHre(int id,String jmeno,String popis,Function<Hra,HerniPravidla> tvurce) {}
