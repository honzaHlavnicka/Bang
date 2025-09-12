/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

/**
 *
 * @author honza
 */
public enum Role {
    SERIF,BANDITA,ODPADLIK,POMOCNIK;
    private static Role[] poradiZiskavaniRoli = new Role[]{Role.SERIF,Role.BANDITA,Role.ODPADLIK,Role.BANDITA,Role.POMOCNIK,Role.BANDITA,Role.POMOCNIK};
    public static Role[] poleRoli(int kolik){
        Role[] role = new Role[kolik];
        for (int i = 0; i < kolik; i++) {
            role[i] = poradiZiskavaniRoli[i];
        }
        
        return role;
    }
}
