/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.net;

/**
 *
 * @author honza
 */
import java.security.SecureRandom;
import java.util.Base64;

public class GeneratorTokenu {

    private static final SecureRandom random = new SecureRandom();

    public static String NovytokenHrace() {
        byte[] bytes = new byte[32]; // 256 bitů
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
