
# Dokumentace protokolu bangu
## 0. základní fungování protokolu
Zprávy se posílají websocketem jako String. jejich formát je buďto:

`typ:payload`

nebo

`typ`
## 1. Průběh a fáze
1) připojení pomocí websocketu
2) server pošle `welcome`
3) zahájení fáze před hrou
4) klient se připojí ke hře
5) fáze hra

## 2. rozpis fází
### 2.1 Fáze před hrou
#### 2.1.1 klient -> server
##### `serverInfo:` 
Data o serveru v html. Nemělo by se používat v normálním klientovy, ale pro řízení serveru.
**payload:** heslo
vytvoří

##### `infoHer`
Vrátí základní infromace o serveru a hry, které aktuálně server podporuje.
**return** `serverDataHTML` 

#### 2.2.1 server -> klient
##### `serverInfo:` 
Data o serveru v html. 
**payload:** html

##### `infoHer`
Dává základní informace o serveru a hry, které aktuálně server podporuje.
**payload** 
```JSON
{"verze":"x.x.x",
"hry":[{
		"nazev":"nazev hry",
		"popis":"popis hry",
		"id":číslo, podle kterého se pak hra vybere.
	}]
} 
```
### 2.2 Fáze při hře
