# fáze před hrou
Tato fáze začíná, když se načte stránka

# Pořadí akcí
1) server pošle `welcome`, čímž potvrdí spojení
2) klient pošle `infoHer`, tím si zažádá informace co může dělat
3) server odpoví `infoher:<json>`.
4) poté by si měl uživatel vybrat hru a klient poslat buď `novaHra:<typHry:number>` nebo `pripojeniKeHre
