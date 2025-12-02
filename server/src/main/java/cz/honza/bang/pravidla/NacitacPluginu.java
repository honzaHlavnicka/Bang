/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.pravidla;

import cz.honza.bang.sdk.HerniPlugin;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.Modifier;

/**
 *
 * @author honza
 */
public class NacitacPluginu {

    /**
     * Vrátí všechny pluginy nacházející se v dané složce. Prohledá složku a najde všechny <code>HerniPlugin</code> v každěm JARu.
     * @param cestaKeSlozce cesta ke složce, ve které se nachází pluginy.
     * @return Seznam všech načetlích pluginů
     * @throws Exception
     */
    public static List<HerniPlugin> nactiPluginy(Path cestaKeSlozce) throws Exception {
        List<HerniPlugin> pluginy = new ArrayList<>();
        
        //Nemůžeme prohledávat neexistující složku, naštěstí v ní nic být ani nemůže.
        if (!Files.exists(cestaKeSlozce)) {
            System.out.println("NEnalezena složka pro pluginy " + cestaKeSlozce.toString());
            return pluginy;
        }
        System.out.println("Nalezena složka pro pluginy " + cestaKeSlozce.toString());

        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cestaKeSlozce, "*.jar")) {
            for (Path cestaKJARu : stream) {
                pluginy.addAll(nactiPluginyZJARu(cestaKJARu));
            }
        }

        return pluginy;
        
    }
    
    public static List<HerniPlugin> nactiPluginyZJARu(Path cesta) throws Exception{
        List<HerniPlugin> pluginy = new ArrayList<>();
        
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{cesta.toUri().toURL()},
                NacitacPluginu.class.getClassLoader()
        );
        
        try (JarFile jar = new JarFile(cesta.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry polozkaJARu = entries.nextElement();

                if (polozkaJARu.isDirectory()) {
                    continue;
                }
                if (!polozkaJARu.getName().endsWith(".class")) {
                    continue;
                }

                String nazevTridy = polozkaJARu.getName() //Název třídy odpovídá cestě, ale místo lomítek používá tečky a neobsahuije .class příponu
                        .replace("/", ".")
                        .replace(".class", "");

                Class<?> c = classLoader.loadClass(nazevTridy);

                if (HerniPlugin.class.isAssignableFrom(c)
                        && !c.isInterface()
                        && !Modifier.isAbstract(c.getModifiers())) {

                    HerniPlugin plugin = (HerniPlugin) c.getDeclaredConstructor().newInstance();
                    pluginy.add(plugin);
                }
            }
        }
        return pluginy;
    }
}
