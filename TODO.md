# TODO – Přehled chyb, problémů a porušení best-practices

Tento soubor obsahuje přehled nalezených problémů v projektu, seřazených podle závažnosti.
Soubory jsou uváděny relativně ke kořeni projektu.

---

## 🔴 BEZPEČNOST (kritické)

### S1 – Výchozí admin heslo `heslo123` při chybějící konfiguraci
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 48–51
- `.env.example` – řádek 3
- `build.sh` – řádek 108

```java
this.adminPassword = System.getenv("ADMIN_PASSWORD");
if (this.adminPassword == null || this.adminPassword.isEmpty()) {
    this.adminPassword = "heslo123"; // Výchozí heslo
}
```

Pokud není `ADMIN_PASSWORD` nastavené, server automaticky přepne na známé hardcoded heslo. To výrazně zvyšuje riziko neautorizovaného přístupu.

---


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

### C11 – `PravidlaKvarteta.vyberKartu` parsuje vstup špatným oddělovačem
**Soubor:**
- `pluginy/kvarteto/src/main/java/cz/honza/bang/pluginy/kvarteto/PravidlaKvarteta.java` – řádek 96

```java
// chybně (aktuálně v kódu)
String[] data = textCoZadal.trim().toLowerCase().split(textCoZadal, 2);
// správně
String[] data = textCoZadal.trim().toLowerCase().split(":", 2);
```

Kvůli chybnému splitu pak pro běžný vstup jako `5:1` často vyjde `data.length != 2`, logika validace selže a metoda se opakovaně volá znovu.

---

## 🟡 BEST PRACTICES / KVALITA KÓDU


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



### K13 – Překlepy v názvech TypeScript typů/proměnných
**Soubor:**
- `klient_react/bang/src/modules/GameContext.ts`

```typescript
startedConection: boolean;       // správně: startedConnection
gameStateMessege?: string;       // správně: gameStateMessage
gameStateMessegeFull?: string;   // správně: gameStateMessageFull
```

---

### K14 – Frontend dependency konflikt blokuje standardní `npm install`
**Soubor:**
- `klient_react/bang/package.json` – řádky 19, 21

Projekt používá `react@^19.1.1`, ale zároveň `react-custom-roulette@^1.4.1`, která vyžaduje peer dependency `react@^18.2.0`. Bez `--force`/`--legacy-peer-deps` tak instalace padá na `ERESOLVE`, což komplikuje setup i CI.



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
| `pluginy/vybusnaKotatka/src/main/java/.../PravidlaKotatek.java` | 22, 36, 41, 46, 51, 56 | Klíčové metody hází `UnsupportedOperationException`, plugin není hratelný |
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
