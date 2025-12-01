/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author honza
 */
public enum Role implements cz.honza.bang.sdk.Role {
    SERIF,BANDITA,ODPADLIK,POMOCNIK;
    private static Role[] poradiZiskavaniRoli = new Role[]{Role.SERIF,Role.BANDITA,Role.ODPADLIK,Role.BANDITA,Role.POMOCNIK,Role.BANDITA,Role.POMOCNIK};
    
    /**
     * Vytvoří pole obsahující role pro určitý počet hráčů v bangu //TODO:přesunout do pravidel bangu
     * @param kolik hráčů hraje = kolik rolí je potřeba
     * @return pole rolí.
     */
    public static List<Role> poleRoliBangu(int kolik){
        List<Role> role = new ArrayList<>(kolik);
        for (int i = 0; i < kolik; i++) {
            role.add(poradiZiskavaniRoli[i]);
        }
        
        System.out.println(role.toString());
        
        return role;
    }
}
