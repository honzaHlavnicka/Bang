# Dokumentace protokolu bangu

## 0. Základní fungování protokolu

Komunikace mezi klientem a serverem probíhá přes WebSocket jako textové zprávy (String). Zprávy mají dva formáty:

1. **Zpráva s payload:** `typ:payload`
2. **Zpráva bez payload:** `typ`

Kde:
- `typ` je název typu zprávy (např. `welcome`, `novaHra`, `error`)
- `payload` je data zprávy, která mohou být prostý text, číslo nebo JSON objekt

## 1. Průběh komunikace a fáze

Komunikace se dělí na následující fáze:

1. **Připojení** - Navázání WebSocket spojení
2. **Uvítání** - Server potvrdí připojení zprávou `welcome`
3. **Fáze před hrou** - Výběr hry, připojení k místnosti, nastavení hráče
4. **Zahájení hry** - Start hry, rozdání rolí a karet
5. **Fáze hra** - Samotná hra s tahy hráčů
6. **Konec hry** - Vyhlášení výherce

## 2. Podrobný rozpis komunikace

### 2.1 Fáze připojení

#### Server → Klient

##### `welcome`
První zpráva poslaná serverem po úspěšném navázání WebSocket spojení.

**Payload:** žádný

**Příklad:**
```
welcome
```

**Reakce klienta:** Klient by měl odpovědět zprávou `infoHer` pro získání informací o serveru.

---

### 2.2 Fáze před hrou

Podrobná dokumentace této fáze je v souboru [predHrou.md](predHrou.md).

#### Klient → Server

##### `infoHer`
Požadavek na informace o serveru a dostupných hrách.

**Payload:** žádný

**Příklad:**
```
infoHer
```

**Odpověď serveru:** `infoHer:<json>`

---

##### `novaHra:<typHry>`
Vytvoření nové hry.

**Payload:** číslo reprezentující typ hry (ID typu hry získané z `infoHer`)

**Příklad:**
```
novaHra:0
```

**Odpověď serveru:** `novaHra:<kodHry>` s 6-místným kódem hry

---

##### `pripojeniKeHre:<kodHry>`
Připojení k existující hře pomocí kódu.

**Payload:** 6-místný kód hry

**Příklad:**
```
pripojeniKeHre:123456
```

**Odpověď serveru:** `pripojenKeHre` nebo `error:<json>`

---

##### `vraceniSe:<token>`
Opětovné připojení hráče k hře pomocí tokenu.

**Payload:** token ve formátu `<kodHry><identifikator>` (6 číslic + token)

**Příklad:**
```
vraceniSe:123456abcdef123456
```

**Odpověď serveru:** `pripojenKeHre` a následně stav hry

---

##### `noveJmeno:<jmeno>`
Nastavení nebo změna jména hráče.

**Payload:** nové jméno hráče (String)

**Příklad:**
```
noveJmeno:Honza
```

**Odpověď serveru:** `noveJmeno:<idHrace>,<jmeno>` rozeslaný všem hráčům

---

##### `setPostava:<postava>`
Výběr postavy hráčem.

**Payload:** identifikátor postavy (String)

**Příklad:**
```
setPostava:BART_CASSIDY
```

**Odpověď serveru:** `setPostava:<idHrace>,<postava>` rozeslaný všem hráčům

---

##### `zahajeniHry`
Požadavek na zahájení hry (může poslat pouze host/první hráč).

**Payload:** žádný

**Příklad:**
```
zahajeniHry
```

**Odpověď serveru:** Hra se zahájí a všem se pošle `hraZacala`

---

##### `nactiHru`
Požadavek na načtení aktuálního stavu hry.

**Payload:** žádný

**Příklad:**
```
nactiHru
```

**Odpověď serveru:** Série zpráv s aktuálním stavem hry

---

##### `serverInfo:<heslo>`
Požadavek na detailní informace o serveru (pouze pro administrátory).

**Payload:** heslo (String)

**Příklad:**
```
serverInfo:heslo123
```

**Odpověď serveru:** `serverDataHTML:<html>` nebo `error:<json>`

---

#### Server → Klient

##### `infoHer:<json>`
Informace o serveru a dostupných hrách.

**Payload:** JSON objekt s verzí serveru a seznamem her

**Struktura JSON:**
```json
{
  "verze": "0.0.7",
  "hry": [
    {
      "nazev": "název hry",
      "popis": "popis hry",
      "id": 0
    }
  ]
}
```

**Příklad:**
```
infoHer:{"verze":"0.0.7","hry":[{"nazev":"Bang!","popis":"Hlavní hra Bang","id":0},{"nazev":"UNO","popis":"Zjednodušené UNO","id":1}]}
```

---

##### `novaHra:<kodHry>`
Potvrzení vytvoření nové hry s jejím kódem.

**Payload:** 6-místný kód hry (číslo)

**Příklad:**
```
novaHra:123456
```

---

##### `pripojenKeHre`
Potvrzení úspěšného připojení k hře.

**Payload:** žádný

**Příklad:**
```
pripojenKeHre
```

---

##### `token:<token>`
Token pro pozdější opětovné připojení k hře.

**Payload:** token ve formátu `<kodHry><identifikator>` (6 číslic + unikátní identifikátor)

**Příklad:**
```
token:123456abcdef123456
```

**Poznámka:** Klient by měl token uložit (např. do localStorage) pro možnost znovupřipojení.

---

##### `novyHrac:<json>`
Informace o novém hráči, který se připojil ke hře (rozesláno všem).

**Payload:** JSON objekt s informacemi o hráči

**Struktura JSON:**
```json
{
  "jmeno": "Hráč 1",
  "zivoty": 4,
  "maximumZivotu": 4,
  "role": "?",
  "id": 0,
  "pocetKaret": 0,
  "postava": ""
}
```

**Příklad:**
```
novyHrac:{"jmeno":"Hráč 1","zivoty":4,"maximumZivotu":4,"role":"?","id":0,"pocetKaret":0}
```

---

##### `noveJmeno:<idHrace>,<jmeno>`
Změna jména hráče (rozesláno všem).

**Payload:** ID hráče a nové jméno oddělené čárkou

**Příklad:**
```
noveJmeno:0,Honza
```

---

##### `setPostava:<idHrace>,<postava>`
Změna postavy hráče (rozesláno všem).

**Payload:** ID hráče a identifikátor postavy oddělené čárkou

**Příklad:**
```
setPostava:0,BART_CASSIDY
```

---

##### `vyberPostavu:<json>`
Nabídka postav pro výběr.

**Payload:** JSON objekt s dostupnými postavami

**Příklad:**
```
vyberPostavu:{"postavy":["BART_CASSIDY","PAUL_REGRET"]}
```

---

##### `hraci:<json>`
Seznam všech hráčů ve hře.

**Payload:** JSON pole objektů hráčů

**Struktura JSON:**
```json
[
  {
    "jmeno": "Hráč 1",
    "zivoty": 4,
    "maximumZivotu": 4,
    "role": "šerif",
    "id": 0,
    "pocetKaret": 5,
    "postava": "BART_CASSIDY"
  }
]
```

**Příklad:**
```
hraci:[{"jmeno":"Hráč 1","zivoty":4,"maximumZivotu":4,"role":"šerif","id":0,"pocetKaret":5,"postava":"BART_CASSIDY"}]
```

---

##### `serverDataHTML:<html>`
HTML stránka s detailními informacemi o serveru (pouze pro administrátory).

**Payload:** HTML kód (String)

**Příklad:**
```
serverDataHTML:<div><h1>Server data</h1>...</div>
```

---

### 2.3 Fáze hra

Podrobná dokumentace této fáze je v souboru [behem-hry.md](behem-hry.md).

#### Klient → Server

##### `linuti`
Požadavek na líznutí karty.

**Payload:** žádný

**Příklad:**
```
linuti
```

---

##### `odehrani:<idKarty>`
Odehrání karty.

**Payload:** ID karty (číslo)

**Příklad:**
```
odehrani:42
```

---

##### `vylozeni:<idKarty>[,<idHrace>]`
Vyložení karty před sebe (nebo před jiného hráče).

**Payload:** ID karty, volitelně ID hráče před kterého se karta vyloží (odděleno čárkou)

**Příklad:**
```
vylozeni:15
vylozeni:15,2
```

---

##### `konecTahu`
Ukončení tahu hráče.

**Payload:** žádný

**Příklad:**
```
konecTahu
```

---

##### `dialog:<id>,<data>`
Odpověď na dialog (výběr akce, hráče, apod.).

**Payload:** ID dialogu a data oddělené čárkou

**Příklad:**
```
dialog:1,2
```

---

##### `chat:<zprava>`
Chat zpráva.

**Payload:** textová zpráva

**Příklad:**
```
chat:Ahoj všichni!
```

**Odpověď serveru:** `chat:<zprava> [od: <jmenoHrace>]` rozeslaná všem

---

#### Server → Klient

##### `hraZacala` / `hraSpustena`
Hra byla zahájena.

**Payload:** žádný

**Příklad:**
```
hraZacala
```

---

##### `role:<role>`
Role přidělená hráči.

**Payload:** identifikátor role (String, např. "šerif", "bandita", "zrádce", "?" pro neznámou)

**Příklad:**
```
role:šerif
```

---

##### `noveIdHrace:<id>`
ID přidělené hráči.

**Payload:** číslo

**Příklad:**
```
noveIdHrace:0
```

---

##### `novaKarta:<json>`
Nová karta přidána hráči do ruky.

**Payload:** JSON objekt s informacemi o kartě

**Struktura JSON:**
```json
{
  "obrazek": "cesta/k/obrazku.png",
  "id": 42
}
```

**Příklad:**
```
novaKarta:{"obrazek":"bang.png","id":42}
```

---

##### `zmenaPoctuKaret:<idHrace>,<pocet>` / `novyPocetKaret:<idHrace>,<pocet>`
Změna počtu karet v ruce hráče.

**Payload:** ID hráče a nový počet karet oddělené čárkou

**Příklad:**
```
zmenaPoctuKaret:1,6
```

---

##### `pocetZivotu:<idHrace>,<pocet>`
Změna počtu životů hráče.

**Payload:** ID hráče a nový počet životů oddělené čárkou

**Příklad:**
```
pocetZivotu:0,3
```

---

##### `tvujTahZacal`
Začal tah aktuálního hráče.

**Payload:** žádný

**Příklad:**
```
tvujTahZacal
```

---

##### `tahZacal:<idHrace>`
Začal tah jiného hráče.

**Payload:** ID hráče na tahu

**Příklad:**
```
tahZacal:1
```

---

##### `odehrat:<idHrace>|<json>`
Karta byla odehrána.

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`

**Struktura:**
```
<idHrace>|{"obrazek":"bang.png","id":42}
```

**Příklad:**
```
odehrat:0|{"obrazek":"bang.png","id":42}
```

---

##### `vylozit:<idHracePredKoho>,<idHraceKym>,<json>`
Karta byla vyložena.

**Payload:** ID hráče před koho, ID hráče kým, JSON s kartou (odděleno čárkami)

**Příklad:**
```
vylozit:0,0,{"obrazek":"barel.png","id":15}
```

---

##### `vyberAkci:<json>`
Dialog pro výběr akce.

**Payload:** JSON objekt s ID dialogu a seznamem akcí

**Struktura JSON:**
```json
{
  "id": 1,
  "akce": [
    {"id": 0, "nazev": "Ano"},
    {"id": 1, "nazev": "Ne"}
  ]
}
```

**Příklad:**
```
vyberAkci:{"id":1,"akce":[{"id":0,"nazev":"Ano"},{"id":1,"nazev":"Ne"}]}
```

**Očekávaná odpověď:** `dialog:<id>,<idAkce>`

---

##### `vyberHrace:<json>`
Dialog pro výběr hráče.

**Payload:** JSON objekt s ID dialogu, seznamem ID hráčů a volitelným nadpisem

**Struktura JSON:**
```json
{
  "id": 2,
  "hraci": [0, 1, 2],
  "nadpis": "Vyber hráče na kterého chceš střílet"
}
```

**Příklad:**
```
vyberHrace:{"id":2,"hraci":[0,1,2],"nadpis":"Vyber hráče"}
```

**Očekávaná odpověď:** `dialog:<id>,<idHrace>`

---

##### `vyhral:<idHrace>`
Konec hry - hráč vyhrál.

**Payload:** ID výherce

**Příklad:**
```
vyhral:0
```

---

##### `chat:<zprava> [od: <jmeno>]`
Chat zpráva od hráče.

**Payload:** zpráva a jméno odesílatele

**Příklad:**
```
chat:Ahoj všichni! [od: Honza]
```

---

### 2.4 Chybové zprávy

##### `error:<json>`
Chybová zpráva.

**Payload:** JSON objekt s informacemi o chybě

**Struktura JSON:**
```json
{
  "error": "popis chyby",
  "kod": 100,
  "skupina": "PROTOKOL"
}
```

**Příklady chyb:**
- `{"error":"už jsi připojen ke hře"}` - hráč se pokusil připojit k další hře
- `{"error":"Hra neexistuje"}` - neplatný kód hry
- `{"error":"tato hra už byla zahájena. Bohužel se už nejde připojit."}` - hra již začala
- `{"error":"Nejsi připojen ke hře"}` - pokus o herní akci bez připojení ke hře
- `{"error":"hráč v této hře nenalezen"}` - neplatný token při vracení se do hry
- `{"error":"špatné heslo"}` - neplatné heslo pro serverInfo

**Příklad:**
```
error:{"error":"Hra neexistuje"}
```

**Poznámka:** Podrobné informace o všech chybách, jejich kódech a skupinách naleznete v [chyby.md](chyby.md).

---

### 2.5 Ostatní zprávy

##### `Echo: <zprava>`
Echo zpráva (pro debugování, bude pravděpodobně odstraněno).

**Payload:** původní zpráva

**Příklad:**
```
Echo: konecTahu
```

---

##### `popoup:<text>` (překlep v implementaci)
Vyskakovací okno s textem. Tato zpráva je určena pouze pro speciální účely a běžný uživatel by se s ní neměl setkat.

**Payload:** text zprávy

**Příklad:**
```
popoup:Nějaká důležitá zpráva
```

---

## 3. Typické scénáře použití

### 3.1 Vytvoření nové hry

```
1. Klient → Server: připojení WebSocket
2. Server → Klient: welcome
3. Klient → Server: infoHer
4. Server → Klient: infoHer:<json se seznamem her>
5. Klient → Server: novaHra:0
6. Server → Klient: novaHra:123456
7. Server → Klient: pripojenKeHre
8. Server → Klient: token:123456abcdef...
9. Server → Klient: novyHrac:<json>
10. Klient → Server: noveJmeno:Honza
11. Server → Klient: noveJmeno:0,Honza
```

### 3.2 Připojení k existující hře

```
1. Klient → Server: připojení WebSocket
2. Server → Klient: welcome
3. Klient → Server: infoHer
4. Server → Klient: infoHer:<json>
5. Klient → Server: pripojeniKeHre:123456
6. Server → Klient: pripojenKeHre
7. Server → Klient: token:123456xyz...
8. Server → Klient: novyHrac:<json>
9. Server → všem: novyHrac:<json nového hráče>
10. Klient → Server: noveJmeno:Petr
11. Server → všem: noveJmeno:1,Petr
```

### 3.3 Znovupřipojení k hře

```
1. Klient → Server: připojení WebSocket
2. Server → Klient: welcome
3. Klient → Server: vraceniSe:123456abcdef...
4. Server → Klient: pripojenKeHre
5. Server → Klient: hraZacala (pokud hra již běží)
6. Server → Klient: načtení aktuálního stavu hry
```

### 3.4 Odehrání karty v hře

```
1. Klient → Server: odehrani:42
2. Server → všem: odehrat:0|{"obrazek":"bang.png","id":42}
3. Server → cílový hráč: vyberAkci:<json s možnostmi reakce>
4. Klient → Server: dialog:1,0
5. Server → všem: aktualizace stavu podle výsledku akce
```

## 4. Poznámky k implementaci

### 4.1 Bezpečnost
- Token pro znovupřipojení obsahuje kód hry (6 číslic) + unikátní identifikátor
- ServerInfo je chráněn heslem (TODO: přesunout do env proměnných)
- Po odpojení všech hráčů se hra automaticky smaže po 5 minutách

### 4.2 Limity
- Kód hry: 6-místné číslo (100000-999999)
- Maximální počet hráčů: určen pravidly hry
- Timeout neaktivní hry: 300 000 ms (5 minut)

### 4.3 JSON formát
JSON objekty v payload jsou obvykle bez mezer a v jednom řádku (doporučeno, ale není zaručeno).

### 4.4 Budoucí vylepšení
- Odstranění Echo zpráv
- Přidání heartbeat mechanismu
- Lepší error handling s kódy chyb
- Autentizace a autorizace
- Rate limiting
