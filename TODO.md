# TODO – Přehled chyb, problémů a porušení best-practices

Tento soubor obsahuje přehled nalezených problémů v projektu, seřazených podle závažnosti.
Soubory jsou uváděny relativně ke kořeni projektu.

---

## 🔴 BEZPEČNOST (kritické)

### B1 – Hardcoded heslo pro admin příkazy
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 67 a 74

Heslo `"heslo123"` je natvrdo zapsáno v kódu. Útočník, který získá přístup ke zdrojovému kódu (nebo jen k JAR souboru a `javap`), může příkazy `serverInfo` a `restartovatPluginy` volat neomezeně. Heslo by mělo být načítáno z proměnné prostředí (např. `System.getenv("ADMIN_PASSWORD")`). Na problém upozorňují i komentáře `//TODO`.

---

### B2 – Logická chyba: chybná `return` po úspěšném admin příkazu `serverInfo`
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 66–71

```java
if(message.startsWith("serverInfo:")){
    if(message.replace("serverInfo:", "").equals("heslo123")){
        conn.send("serverDataHTML:"+serverDataHTML()); // odeslána data...
    }
    posliChybu(conn, Chyba.SPATNE_HESLO); // ...ale pak se VŽDY odešle i chyba!
    return;
}
```

Po odeslání HTML dat chybí `return`, takže se klientovi za každou okolnost odešle i chybová zpráva `SPATNE_HESLO`, i když bylo heslo správné.

---

### B3 – `substring()` bez kontroly délky – riziko `StringIndexOutOfBoundsException`
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 137–138

```java
KomunikatorHryImp komunikator = hryPodleId.get(message.substring(10, 16));
if(komunikator.vraciSeHrac(conn, message.substring(16))){
```

Zpráva ve formátu `vraceniSe:<token>` musí mít délku alespoň 17 znaků. Pokud klient pošle kratší zprávu, vyhodí JVM výjimku `StringIndexOutOfBoundsException`, která server neshodí (je zachycena WebSocket knihovnou), ale způsobí neočekávané chování. Chybí validace délky vstupní zprávy.

---

### B4 – Expozice WebSocket objektu do globálního scope prohlížeče
**Soubor:**
- `klient_react/bang/src/modules/GameProvider.tsx` – řádek 55

```typescript
(window as unknown as { ws: WebSocket }).ws = socket; //TODO: odstranit testovací přiřazení
```

WebSocket spojení je přístupné přes `window.ws`, takže jakýkoli JavaScript na stránce (včetně třetí strany při XSS útoku) může posílat zprávy serveru bez vědomí aplikace. Pouze testovací kód, ale nesmí být v produkci.

---

### B5 – Žádná autentizace ani autorizace hráčů
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java`
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java`

Kdokoli se může připojit k libovolné hře, pokud zná 6místný kód. Tokenový systém chrání pouze opětovné připojení po odpojení, ale ne prvotní připojení. Neexistuje žádný mechanismus ověření identity hráče (login, heslo ke hře, …).

---

### B6 – Dynamické načítání pluginů bez ověření podpisu
**Soubor:**
- `server/src/main/java/cz/honza/bang/pravidla/NacitacPluginu.java`

Systém načítá libovolné JAR soubory ze složky `pluginy/` pomocí `URLClassLoader` a spouští jejich kód bez jakékoliv verifikace (checksum, kryptografický podpis). Nahrazení pluginu škodlivým JARem by vedlo ke spuštění libovolného kódu na serveru.

---

### B7 – Žádné omezení počtu zpráv (rate limiting)
**Soubory:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java`
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java`

Server nezaznamenává, ani neomezuje, kolik zpráv za sekundu může jeden klient posílat. Útočník může zaplavit server zprávami a způsobit DoS.

---

## 🟠 CHYBY V LOGICE (bugs)

### C1 – `pouziteKody` (seznam použitých kódů her) se nikdy nenaplní
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 31, 172–178

```java
private List<Integer> pouziteKody = new ArrayList<>();

private int nahodneIdHry(){
    int kod = random.nextInt(999999 - 100000 + 1) + 100000;
    if(pouziteKody.indexOf(Integer.valueOf(kod)) != -1){ // tato podmínka NIKDY není true
        return nahodneIdHry();
    }
    return kod;
}
```

Kódy se do `pouziteKody` nikde nepřidávají, takže `indexOf()` vždy vrátí -1. Kontrola duplicitních kódů her je zcela nefunkční. Dvě hry tak mohou dostat stejný kód, přičemž druhá hra přepíše první v mapě `hryPodleId`.

---

### C2 – `message.startsWith("novaHra")` zachytí i zprávu `novaHraSHracema:`
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádek 92

```java
if (message.startsWith("novaHra") || message.startsWith("pripojeniKeHre:111")) {
```

Zpráva `novaHraSHracema:<id>` (určená pro reset hry) začíná `"novaHra"`, a proto se zachytí v této větvi místo v `prislaZprava()`. Výsledkem je, že pokus o reset hry způsobí chybu `UZ_PRIPOJEN` (pokud je hráč ve hře) nebo vytvoří novou hru (pokud není).

---

### C3 – `BarelEfekt.poZtrateZivota` – nesprávná herní logika a testovací zpráva v produkci
**Soubor:**
- `pluginy/bang/src/main/java/cz/honza/bang/pluginy/bang/BarelEfekt.java` – řádky 29–40

```java
//TODO: naprogramovat tento efekt :)
public void poZtrateZivota(Hra hra, Hrac hrac) {
    Random r = new Random();
    if(r.nextInt(3) == 0){
        hrac.pridejZivot();
        hra.getKomunikator().posli(hrac,"TODO: byl jsi zachráněn barelem"); // doslova "TODO:..."
    }
}
```

1. Efekt barelu je implementován jako náhodná 1/3 šance, ne jako otočení karty (pravidla hry Bang!).
2. Klientovi se posílá zpráva `"TODO: byl jsi zachráněn barelem"` – doslova tento text jako protokolová zpráva.
3. Komentář `//TODO: naprogramovat tento efekt :)` přímo říká, že efekt není hotový.

---

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

### C5 – `PravidlaBangu.muzeVylozit` porovnává objekt místo jména karty
**Soubor:**
- `pluginy/bang/src/main/java/cz/honza/bang/pluginy/bang/PravidlaBangu.java` – řádek 138

```java
return !kdo.getVylozeneKarty().contains(co); //špatně. musíš podle názvu
```

Komentář v kódu přímo říká, že implementace je špatná – porovnává instanci karty místo jejího jména/typu.

---

### C6 – `Bang.odehrat` a `CatBalou.odehrat` volají `Integer.parseInt()` bez ošetření výjimky
**Soubory:**
- `pluginy/bang/src/main/java/cz/honza/bang/pluginy/bang/Bang.java` – řádek 34
- `pluginy/bang/src/main/java/cz/honza/bang/pluginy/bang/CatBalou.java` – řádek 49

```java
Hrac naKoho = hra.getHrac(Integer.parseInt(odpoved)); //TODO: možná nějaká exception kontrola
```

Pokud klient odpoví textem, který není číslo, vyhodí se `NumberFormatException`, která není zachycena. Komentář v CatBalou na problém upozorňuje.

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

### C9 – `nactiHru` posílá klientovi doslova ladící větu
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` – řádek 167

```java
conn.send("načítání hry. tohle bude nejakej json.");
```

Klientovi se posílá neparsovatelný ladící text namísto skutečné strukturované zprávy.

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

### K4 – `Timer` v `hracOdpojen` bez uloženého odkazu – nelze zrušit
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/KomunikatorHryImp.java` – řádky 177–185

```java
new Timer().schedule(new TimerTask() {
    @Override
    public void run() {
        socket.ukoncitHru(idHry);
    }
}, SMAZAT_NEAKTIVNI_HRU_MS);
```

Reference na `Timer` se nikam neukládá. Pokud se hráč znovu připojí před vypršením časovače, nelze ho zrušit a hra se po 5 minutách smaže, i když se aktivně hraje. Při opakovaném odpojování téhož hráče vznikají neomezené `Timer` objekty.

---

### K5 – `BalicekImp` používá raw typ `@Deprecated` metodu
**Soubor:**
- `server/src/main/java/cz/honza/bang/BalicekImp.java` – metoda `toDeque()` (řádek 136)

Metoda je označena `@Deprecated` a vrací přímý odkaz na vnitřní kolekci (porušuje encapsulation). Pokud tato metoda existuje pouze pro testovací účely, měla by být odstraněna.

---

### K6 – `HraImp.prohodBalicky()` používá raw typ bez generik
**Soubor:**
- `server/src/main/java/cz/honza/bang/HraImp.java` – řádky 198–203

```java
BalicekImp novyOdhazovaciBalicek = balicek; // raw type, varování kompilátoru
```

Použití raw typu způsobuje unchecked warning a obchází typovou bezpečnost generik.

---

### K7 – `zmenaSmeru()` používá zbytečné if-else místo negace
**Soubor:**
- `server/src/main/java/cz/honza/bang/SpravceTahuImp.java` – řádky 208–213

```java
if(zmenenSmer){
    zmenenSmer = false;
}else{
    zmenenSmer = true;
}
// Správně: zmenenSmer = !zmenenSmer;
```

---

### K8 – Zakomentovaný kód (celé třídy) v repozitáři
**Soubory:**
- `server/src/main/java/cz/honza/bang/postavy/Postava.java` – celá třída je zakomentovaná
- `server/src/main/java/cz/honza/bang/pravidla/SpravceHernichPravidel.java` – původní implementace je zakomentovaná

Zakomentovaný kód by měl být odstraněn (správa verzí zajišťuje historii).

---

### K9 – Nepopsané pluginy – `getPopis()` vrací literal `"TODO: popis"`
**Soubory:**
- `pluginy/bang/src/main/java/cz/honza/bang/pluginy/bang/BanggPlugin.java` – řádek 26
- `pluginy/Uno/src/main/java/cz/honza/bang/pluginy/uno/UNOPlugin.java` – řádek 26

```java
return "TODO: popis"; // toto se posílá klientovi jako popis hry!
```

Tato hodnota se posílá klientovi v JSON listu dostupných her.

---

### K10 – Starý soubor `App-old.tsx` v repozitáři
**Soubor:**
- `klient_react/bang/src/App-old.tsx`

Soubor obsahuje starou verzi komponenty `App` z doby, kdy aplikace jen počítala kliky. Měl by být odstraněn nebo přesunut do git historie.

---

### K11 – Pole `HraImp.obrazekZadniStrany` je deklarováno, ale nikde nepoužíváno
**Soubor:**
- `server/src/main/java/cz/honza/bang/HraImp.java` – řádek 43

```java
private String obrazekZadniStrany;
```

Pole je deklarováno, ale nikdy nenastaveno ani nečteno.

---

### K12 – `pouziteKody` je `ArrayList` s použitím `indexOf()` – O(n) složitost
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádky 31, 175

Kontrola existence kódu přes `indexOf()` na `ArrayList` je O(n). Správně by měl být použit `HashSet<Integer>` pro O(1) vyhledávání. (Viz také bod C1 – seznam se navíc nikdy nenaplní.)

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

### K14 – `setGameValue` v `GameContext` přijímá parametr typu `any`
**Soubor:**
- `klient_react/bang/src/modules/GameContext.ts` – řádek 54

```typescript
setGameValue: (data: any, type: GameValueType) => void;
```

Použití `any` obchází TypeScript typový systém. Měl by být použit konkrétní typ nebo `unknown`.

---

### K15 – Zakomentované testovací dialogy v produkčním kódu
**Soubor:**
- `klient_react/bang/src/modules/GameProvider.tsx` – řádky 24–28

Rozsáhlý blok zakomentovaného kódu pro testování dialogů by měl být odstraněn (viz TODO komentář).

---

### K16 – `NacitacPluginu` neuzavírá `URLClassLoader` (resource leak)
**Soubor:**
- `server/src/main/java/cz/honza/bang/pravidla/NacitacPluginu.java` – metoda `nactiPluginyZJARu`

`URLClassLoader` je vytvořen, ale nikdy uzavřen. Použití `try-with-resources` by zajistilo správné uvolnění zdrojů.

---

### K17 – `SpravceHernichPravidel.getJSONVytvoritelneHry()` přiřazuje ID přes `AtomicInteger` v lambda
**Soubor:**
- `server/src/main/java/cz/honza/bang/pravidla/SpravceHernichPravidel.java`

```java
AtomicInteger id = new AtomicInteger(); //int nefunguje a chatGPT doporučil toto
```

Komentář přímo přiznává, že řešení bylo přijato na základě rady chatbotu bez hlubšího pochopení. Správné řešení je použít `IntStream.range()` nebo klasický cyklus `for`.

---

### K18 – Chybí `return` nebo `else` v `dalsiHracPodleRole` – potenciálně nekončící smyčka
**Soubor:**
- `server/src/main/java/cz/honza/bang/SpravceTahuImp.java` – metoda `dalsiHracPodleRole`

```java
public HracImp dalsiHracPodleRole(cz.honza.bang.sdk.Role role) {
    HracImp hrac;
    do {
        hrac = dalsiHrac();
    } while (hrac.getRole() != role);
    ...
}
```

Pokud žádný hráč nemá hledanou roli (nebo jsou všichni s touto rolí vyřazeni), metoda se zacyklí donekonečna.

---

### K19 – `vzdalenostPod()` v `HracImp` – překlep v Javadoc a logická chyba v podmínce
**Soubor:**
- `server/src/main/java/cz/honza/bang/HracImp.java` – metoda `vzdalenostPod`

1. Javadoc říká: *"Vrací List hráčů, jejichž vzdálenost je větší než `max`"*, ale podmínka je `>= max`. Nesoulad dokumentace a kódu.
2. Samotná podmínka `rozdilPodleMist >= max` vrací hráče ve vzdálenosti **rovné nebo větší** než `max` – název parametru `max` a text Javadoc je proto zavádějící.

---

### K20 – `pripojeniKeHre:111` jako hardcoded výjimka
**Soubor:**
- `server/src/main/java/cz/honza/bang/net/SocketServer.java` – řádek 92

```java
if (message.startsWith("novaHra") || message.startsWith("pripojeniKeHre:111")) {
```

Kód hry `111` je natvrdo zakódován jako výjimka, která vytvoří novou hru místo připojení k existující. Důvod není dokumentován ani jasný.

---

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

### A3 – Žádný ping/pong mechanismus
WebSocket spojení nemá heartbeat. Odpojení není detekováno okamžitě, hra čeká 5 minut, než se smaže – i když je hráč fyzicky odpojen.

### A4 – Žádná podpora WSS (TLS)
Komunikace probíhá přes nešifrovaný WebSocket (`ws://`). Pro produkci by mělo být použito `wss://` s TLS certifikátem.

### A5 – Veškerý stav hry je pouze v paměti
Při restartu serveru se všechny rozehrané hry ztratí. Chybí perzistence (databáze, soubory).

### A6 – Adresa serveru je v klientovi hardcoded na `localhost`
**Soubor:** `klient_react/bang/src/modules/GameProvider.tsx` – řádek 33

```typescript
const socketAdress = "ws://localhost:22207"; // produkce vyžaduje změnu
//         ^^^^^^^^ překlep: chybí 'd' (správně socketAddress)
```

Komentované alternativy v okolním kódu naznačují, že nasazení vyžaduje ruční editaci souboru.
Název proměnné `socketAdress` navíc obsahuje překlep (chybí `d`) – správně `socketAddress`.
