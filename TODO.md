# TODO – Přehled chyb, problémů a porušení best-practices

Tento soubor obsahuje přehled nalezených problémů v projektu, seřazených podle závažnosti.
Soubory jsou uváděny relativně ke kořeni projektu.

---

## 🔴 BEZPEČNOST (kritické)


## 🟠 CHYBY V LOGICE (bugs)


### C2 – `message.startsWith("novaHra")` zachytí i zprávu `novaHraSHracema:`
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádek 92

```java
if (message.startsWith("novaHra") || message.startsWith("pripojeniKeHre:111")) {
```

Zpráva `novaHraSHracema:<id>` (určená pro reset hry) začíná `"novaHra"`, a proto se zachytí v této větvi místo v `prislaZprava()`. Výsledkem je, že pokus o reset hry způsobí chybu `UZ_PRIPOJEN` (pokud je hráč ve hře) nebo vytvoří novou hru (pokud není).



### C4 – `SpravceTahuImp.dalsiHrac()` používá rekurzi bez limitu hloubky
**Soubor:**
- `server/src/main/java/cz/honza/bang/SpravceTahuImp.java` – řádky 76–113

```java
if (!tah.docasneZruseny) {
    ...
    return naTahu;
} else {
    return dalsiHrac(); // rekurzivní volání
    //TODO: udelat limit poctu hracu treba 30, aby nemohlo nastata preteceni zasobniku
}
```

Pokud jsou všichni hráči vyřazeni (nebo kvůli jiné chybě), metoda se rekurzivně volá donekonečna a způsobí `StackOverflowError`. Komentář přiznává problém.

---


### C7 – `vylozitKartu` v `HracImp`: možný `NullPointerException` při neexistujícím hráči
**Soubor:**
- `server/src/main/java/cz/honza/bang/HracImp.java` – řádky 394–397

```java
Hrac predKoho = (HracImp) hra.getHrac(idPredKoho);
// predKoho může být null, pokud hráč s daným id neexistuje
if(hra.getHerniPravidla().muzeVylozit(this, vylozena)){
    if(vylozena.vylozit(this, predKoho)){ // NullPointerException!
```

`hra.getHrac()` vrátí `null`, pokud hráč s daným ID neexistuje, a tato hodnota se hned předá dál bez kontroly.

---

### C8 – `smazatHruAVyrobytNovou` v `KomunikatorHryImp` nedokončuje reset správně
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` – metoda `smazatHruAVyrobytNovou`

Metoda vytvoří nové mapy hráčů a WebSocket spojení, ale:
1. Nová mapa `novyHraciPodlIdentifikatoru` se ani nevytvoří – místo ní se průběžně mění stará `hraciPodlIdentifikatoru`.
2. Po resetu se hráčům neposílají žádné informace o nové hře.
3. Metoda je nedokončena (viz komentář na konci: `//poslat informace o změně hráčům`).

---


### C10 – `podleniIdCekaciOdpovedi` není thread-safe
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` – řádky 26, 256–259

```java
private int podleniIdCekaciOdpovedi = 0; // plain int, ne AtomicInteger
...
podleniIdCekaciOdpovedi++;
Integer id = podleniIdCekaciOdpovedi;
```

Pokud jsou dvě karty odehrány současně (ve dvou vláknech), může dojít ke kolizi ID čekajících odpovědí. Měl by se použít `AtomicInteger`.

---

## 🟡 BEST PRACTICES / KVALITA KÓDU

### K1 – `System.out.println` místo logovacího frameworku
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` (10+ výskytů)
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` (3 výskyty)
- `server/src/main/java/cz/honza/bang/HracImp.java` (1 výskyt)
- `server/src/main/java/cz/honza/bang/SpravceTahuImp.java` (1 výskyt)
- `server/src/main/java/cz/honza/bang/HraImp.java` (1 výskyt)

Místo `System.out.println` by měl být použit logovací framework (SLF4J + Logback nebo Log4j2). Přináší to: log levels, filtrování, výstup do souboru, timestamp a možnost vypnout debug logy v produkci.

---

### K2 – Ruční sestavování JSON řetězců místo použití knihovny
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – metoda `posliChybu` (řádek 254)
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` – metoda `posliChybu` (řádek 195)
- `server/src/main/java/cz/honza/bang/HracImp.java` – metody `toJSON` a `vyberZPostav`
- `server/src/main/java/cz/honza/bang/HraImp.java` – metoda `nactiHru`

Projekt má v `pom.xml` závislost na `org.json`, ale místo ní se JSON sestavuje pomocí `StringBuilder` a ručního escapování. To je náchylné na chyby – pokud jméno hráče nebo popis chyby obsahuje uvozovky, vznikne nevalidní JSON. Metoda `escapeJSON` v `SpravceHernichPravidel` ošetřuje jen uvozovky, ignoruje ostatní speciální znaky (`\n`, `\r`, `\`, …).

---

### K3 – Statický čítač `HracImp.nextId` – bezpečnostní a provozní problémy
**Soubor:**
- `server/src/main/java/cz/honza/bang/HracImp.java` – řádky 36, 43–44

```java
private static int nextId = 0;
...
id = nextId;
nextId++;
```

1. Není thread-safe (měl by být `AtomicInteger`).
2. V dlouhodobě běžícím serveru se hodnota přetočí přes `Integer.MAX_VALUE` zpět na záporná čísla.
3. ID jsou globální napříč hrami – hráč v jedné hře má ID, které závisí na počtu hráčů ve všech dříve spuštěných hrách.

---

### K5 – `BalicekImp` používá raw typ `@Deprecated` metodu
**Soubor:**
- `server/src/main/java/cz/honza/bang/BalicekImp.java` – metoda `toDeque()` (řádek 136)

Metoda je označena `@Deprecated` a vrací přímý odkaz na vnitřní kolekci (porušuje encapsulation). Pokud tato metoda existuje pouze pro testovací účely, měla by být odstraněna.

---


### K11 – Pole `HraImp.obrazekZadniStrany` je deklarováno, ale nikde nepoužíváno
**Soubor:**
- `server/src/main/java/cz/honza/bang/HraImp.java` – řádek 43

```java
private String obrazekZadniStrany;
```

Pole je deklarováno, ale nikdy nenastaveno ani nečteno.

---


### K13 – Překlepy v názvech TypeScript typů/proměnných
**Soubor:**
- `klient_react/bang/src/modules/GameContext.ts`

```typescript
startedConection: boolean;       // správně: startedConnection
gameStateMessege?: string;       // správně: gameStateMessage
gameStateMessegeFull?: string;   // správně: gameStateMessageFull
```



## 🔵 NEDOKONČENÉ FUNKCE (TODO v kódu)

Následující funkce jsou v kódu označeny jako nedokončené a nefungují správně:

| Soubor | Řádek | Popis |
|--------|-------|-------|
| `pluginy/bang/src/main/java/.../BarelEfekt.java` | 29, 37 | Efekt barelu není implementován podle pravidel |
| `pluginy/bang/src/main/java/.../Bang.java` | 39–40 | Chybí kontrola barelu a dotaz na Vedle |
| `pluginy/bang/src/main/java/.../PravidlaBangu.java` | 96 | Chybí poslání karet po smrti hráče klientům |
| `pluginy/bang/src/main/java/.../CatBalou.java` | 49, 89 | Chybí ošetření výjimek, duplicitní kód (DRY) |
| `pluginy/bang/src/main/java/.../Role.java` | 21 | Logika rolí by měla být přesunuta do `PravidlaBangu` |
| `pluginy/Uno/src/main/java/.../unoZmenaBarvy.java` | 58 | Nelze hrát další kartu, dokud není splněn slib (změna barvy) |
| `pluginy/prsi/src/main/java/.../PrsiSvrsek.java` | 61 | Stejný problém jako UNO – slib není implementován |
| `server/src/main/java/.../HraImp.java` | 105 | Výběr z postav pro hry, které ho potřebují |
| `server/src/main/java/.../HracImp.java` | 220 | Přetypování při výběru postavy |
| `server/src/main/java/.../HracImp.java` | 342 | Duplicitní kód (DRY) v `spalitKartu` |
| `server/src/main/java/.../HracImp.java` | 497 | `vzdalenostKCista` ignoruje efekty |
| `server/src/main/java/.../SpravceTahuImp.java` | 112 | Chybí limit hloubky rekurze v `dalsiHrac` |
| `server/src/main/java/.../postavy/Postava.java` | 33 | `VULTURE_SAM` – přenesení karet není implementováno |
| `klient_react/bang/src/modules/DialogContext.tsx` | 21 | Chybí předpřipravený typ pro hráče |
| `klient_react/bang/src/modules/DialogProvider.tsx` | 20 | Chybí kontrola provedení akce |

---

## ⚪ ARCHITEKTURA / OSTATNÍ NÁVRHY

### A1 – Žádné testy
Projekt neobsahuje žádné unit testy ani integrační testy. V `pom.xml` chybí testovací závislosti (JUnit). Frontend nemá žádné testy (Vitest/Jest).

### A2 – Žádná konfigurace
Port serveru (22207), timeout her (300 000 ms) a cesta ke složce pluginů jsou natvrdo v kódu. Chybí konfigurační soubor (`.properties`, `.env`, `application.yml`).

