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

#### Klient → Server

##### `posthogId:<id>`
Zaslání analytického ID klienta (nebo `posthogId:ignore`) pro asociaci telemetrie na serveru. Klient posílá tuto zprávu okamžitě po navázání spojení.

**Payload:** ID klienta nebo `ignore`

**Příklad:**
```
posthogId:user_abc123
posthogId:ignore
```

---

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

##### `overeniTokenu:<token>`
Ověření platnosti herního tokenu před pokusem o znovupřipojení.

**Payload:** token ve formátu `<kodHry(6)><identifikator>`

**Příklad:**
```
overeniTokenu:123456abcdef123456
```

**Odpověď serveru:** `overeniTokenu:true` nebo `overeniTokenu:false`

---

##### `vraceniSe:<token>`
Opětovné připojení hráče k hře pomocí tokenu. Pokud byl hráč již připojen v jiné záložce/okně, staré spojení obdrží chybu `PRIPOJEN_V_JINE_ZALOZCE` (kód 22) a bude uzavřeno.

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

##### `pridejBota` / `pridejBota:<jmeno>`
Přidání počítačem řízeného hráče (bota) do lobby (pouze pro administrátora hry). Hra musí boty podporovat (`podporujeBoty()`).

**Payload:** volitelné jméno bota

**Příklad:**
```
pridejBota
pridejBota:Bot Pepa
```

**Odpověď serveru:** rozeslání `novyHrac:<json>` s `"isBot": true`, případně chyba `HRA_NEPODPORUJE` (kód 21).

---

##### `vyhodHrace:<idHrace>`
Vyhození hráče z lobby před zahájením hry (pouze pro administrátora hry).

**Payload:** ID vyhazovaného hráče

**Příklad:**
```
vyhodHrace:2
```

**Odpověď serveru:** Vyhozenému hráči je poslána chyba `VYHOZEN_ZE_HRY` (kód 19) a jeho spojení je ukončeno. Všem ostatním se odešle aktualizovaný seznam hráčů.

---

##### `nahradHru:<typHry>`
Změna typu hry v lobby (pouze pro administrátora hry).

**Payload:** ID typu hry

**Příklad:**
```
nahradHru:1
```

**Odpověď serveru:** `nahradHru:<typHry>` nebo `nahradHruBezPtani:<typHry>`

---

##### `nactiPreklady` / `nactiPreklady:<jazyk>`
Požadavek na stažení lokalizačních řetězců daného herního pluginu pro zvolený jazyk.

**Payload:** volitelný kód jazyka (např. `cs`, `en`)

**Příklad:**
```
nactiPreklady:cs
```

**Odpověď serveru:** `preklady:<json>`

---

##### `zahajeniHry`
Požadavek na zahájení hry (může poslat pouze administrátor hry).

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

**Odpověď serveru:** `serverDataHTML:<html>` nebo `error:<json>` (kod: 14)

---

##### `gameInfo:<kod>:<heslo>`
Požadavek na herní stav dané hry v JSON formátu (pouze pro administrátory).

**Payload:** kód hry a heslo serveru oddělené dvojtečkou

**Příklad:**
```
gameInfo:123456:heslo123
```

**Odpověď serveru:** `gameInfoJSON:<json>` nebo `error:<json>` (kod: 14 nebo 5 nebo 12)

---

##### `getIdHry`
Dotaz na ID aktuální hry.

**Payload:** žádný

**Příklad:**
```
getIdHry
```

**Odpověď serveru:** `setIdHry:<id>`

---

##### `novaHraSHracema:<typHry>`
Restart hry se stejnými hráči (pouze pro administrátora hry).

**Payload:** číslo - ID typu hry

**Příklad:**
```
novaHraSHracema:0
```

**Odpověď serveru:** `error:<json>` (kod: 17) pokud nemáte práva administrátora

---

##### `restartovatPluginy:<heslo>`
Znovunačtení herních pluginů (pouze pro administrátory serveru).

**Payload:** heslo serveru (String)

**Příklad:**
```
restartovatPluginy:heslo123
```

**Odpověď serveru:** `ok` nebo `error:<json>` (kod: 14)

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
      "id": 0,
      "jmeno": "Bang!",
      "popis": "Populární hra s cílem zabít ostatní hráče.",
      "url": "https://albi.cz/bang-pravidla.pdf"
    }
  ]
}
```

**Příklad:**
```
infoHer:{"verze":"0.0.7","hry":[{"id":0,"jmeno":"Bang!","popis":"Populární hra s cílem zabít ostatní hráče.","url":"https://albi.cz/bang-pravidla.pdf"}]}
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

##### `overeniTokenu:<boolean>`
Odpověď na dotaz `overeniTokenu:<token>` s informací o platnosti tokenu hráče.

**Payload:** `true` nebo `false`

**Příklad:**
```
overeniTokenu:true
overeniTokenu:false
```

---

##### `novyHrac:<json>`
Informace o novém hráči, který se připojil ke hře (rozesláno všem).

**Payload:** JSON objekt s informacemi o hráči

**Struktura JSON:**
```json
{
  "id": 0,
  "jmeno": "Hráč 1",
  "zivoty": 4,
  "pocetKaret": 0,
  "postava": "BART_CASSIDY",
  "maximumZivotu": 4,
  "isAdmin": true,
  "isOnline": true,
  "isBot": false,
  "vylozeneKarty": []
}
```

**Příklad:**
```
novyHrac:{"id":0,"jmeno":"Hráč 1","zivoty":4,"pocetKaret":0,"maximumZivotu":4,"isAdmin":true,"isOnline":true,"isBot":false,"vylozeneKarty":[]}
```

---

##### `pripojeniHrace:<idHrace>`
Oznámení, že se dříve odpojený hráč znovu připojil (rozesláno všem).

**Payload:** ID hráče

**Příklad:**
```
pripojeniHrace:1
```

---

##### `odpojeniHrace:<idHrace>`
Oznámení, že hráč ztratil spojení se serverem (rozesláno všem).

**Payload:** ID hráče

**Příklad:**
```
odpojeniHrace:1
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

**Payload:** JSON pole objektů s informacemi o postavách

**Struktura JSON:**
```json
[
  {
    "jmeno": "BART_CASSIDY",
    "obrazek": "BART_CASSIDY",
    "popis": "Popis postavy",
    "zivoty": "4"
  }
]
```

**Příklad:**
```
vyberPostavu:[{"jmeno":"BART_CASSIDY","obrazek":"BART_CASSIDY","popis":"Kdykoliv je zasažen, lízne si kartu.","zivoty":"4"}]
```

---

##### `hraci:<json>`
Seznam všech hráčů ve hře.

**Payload:** JSON pole objektů hráčů

**Struktura JSON:**
```json
[
  {
    "id": 0,
    "jmeno": "Hráč 1",
    "zivoty": 4,
    "pocetKaret": 5,
    "role": "šerif",
    "postava": "BART_CASSIDY",
    "maximumZivotu": 4,
    "isAdmin": true,
    "isOnline": true,
    "isBot": false,
    "vylozeneKarty": []
  }
]
```

**Příklad:**
```
hraci:[{"id":0,"jmeno":"Hráč 1","zivoty":4,"pocetKaret":5,"role":"šerif","postava":"BART_CASSIDY","maximumZivotu":4,"isAdmin":true,"isOnline":true,"isBot":false,"vylozeneKarty":[]}]
```

---

##### `nahradHru:<noveId>,<jmeno>` / `nahradHruBezPtani:<noveId>,<jmeno>`
Pozvánka do nově vytvořené hry (odesíláno při nahrazení hry administrátorem). `nahradHruBezPtani` je poslána administrátorovi, který akci vyvolal; ostatním hráčům je zaslána `nahradHru` s dialogem pro potvrzení přechodu.

**Payload:** nové ID hry a jméno hráče

**Příklad:**
```
nahradHru:654321,Honza
nahradHruBezPtani:654321,Honza
```

---

##### `preklady:<json>`
Sada lokalizačních řetězců daného herního pluginu pro aktuální jazyk klienta.

**Payload:** JSON mapa klíčů a přeložených textů

**Příklad:**
```
preklady:{"karta.bang":"Bang!","popis.bang":"Vystřelí na jiného hráče."}
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

##### `setIdHry:<id>`
Odpověď na dotaz `getIdHry` - ID aktuální hry.

**Payload:** číslo - ID hry

**Příklad:**
```
setIdHry:123456
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

##### `spaleni:<idKarty>`
Spálení karty z ruky (zahození na odkladací balíček).

**Payload:** ID karty

**Příklad:**
```
spaleni:42
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
Odpověď na dialog (výběr akce, hráče, karty nebo zadání textu).

**Payload:** ID dialogu a data oddělené čárkou. Formát dat závisí na typu dialogu:
- Výběr akce: ID vybrané akce (číslo)
- Výběr hráče: ID vybraného hráče nebo čárkou oddělená ID (např. `1` nebo `1,2`)
- Výběr karty: ID vybrané karty nebo čárkou oddělená ID karet
- Zadání textu: zadaný textový řetězec

**Příklad:**
```
dialog:1,2
dialog:2,1,3
dialog:3,Ahoj světe
```

---

##### `uiClick:<id>`
Kliknutí na vlastní UI tlačítko přidané pluginem hry (přes `noveUI`).

**Payload:** číselné ID UI tlačítka

**Příklad:**
```
uiClick:1
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

##### `hraZacala`
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
  "id": 42,
  "jmeno": "bang",
  "obrazek": "bang.png",
  "vylozitelna": false,
  "hratelna": true
}
```

**Příklad:**
```
novaKarta:{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
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
Karta byla odehrána z ruky na odhazovací balíček. Pokud je `idHrace` rovno `-1`, jde o otočení vrchní karty balíčku bez vazby na konkrétního hráče.

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`

**Struktura:**
```
<idHrace>|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

**Příklad:**
```
odehrat:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

---

##### `vylozeni:<idHraceKym>,<idHracePredKoho>,<json>`
Karta byla vyložena na stůl (před sebe nebo jiného hráče).

**Payload:** ID hráče který kartu vyložil, ID hráče před koho, JSON s kartou (odděleno čárkami)

**Příklad:**
```
vylozeni:0,0,{"id":15,"jmeno":"barel","obrazek":"barel.png","vylozitelna":true,"hratelna":false}
```

---

##### `spalit:<idHrace>|<json>`
Karta byla spálena (zahozena z ruky nebo stolu na odkládací balíček).

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`

**Příklad:**
```
spalit:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

---

##### `spalenaVylozena:<idKarty>,<idHrace>`
Vyložená (trvalá) karta byla spálena ze stolu daného hráče.

**Payload:** ID karty a ID hráče oddělené čárkou

**Příklad:**
```
spalenaVylozena:15,0
```

---

##### `hracSkoncil:<idHrace>`
Hráč byl vyřazen ze hry (životy klesly na 0).

**Payload:** ID hráče

**Příklad:**
```
hracSkoncil:2
```

---

##### `povoleneUI:<json>`
Informace o povolených prvcích uživatelského rozhraní.

**Payload:** JSON pole názvů povolených UI prvků

**Příklad:**
```
povoleneUI:["ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"]
```

---

##### `noveUI:<json>`
Přidání nebo aktualizace vlastního akčního tlačítka vytvořeného pluginem hry.

**Payload:** JSON s ID, textem a příznakem neaktivity

**Struktura JSON:**
```json
{
  "id": 1,
  "text": "Speciální akce",
  "disabled": false
}
```

**Příklad:**
```
noveUI:{"id":1,"text":"Vzít dynamit","disabled":false}
```

---

##### `odebratUI:<id>`
Odebrání vlastního akčního tlačítka.

**Payload:** číselné ID tlačítka

**Příklad:**
```
odebratUI:1
```

---

##### `rychleOznameni:<text>`
Krátké textové oznámení (toast/notifikace). Podporuje lokalizační klíče `$key`.

**Payload:** text oznámení

**Příklad:**
```
rychleOznameni:Barva změněna na červenou
```

---

##### `stavHry:<text>`
Stavové oznámení zobrazené v centru herní obrazovky (např. probíhající fáze hry, duely apod.). Podporuje lokalizační syntaxi `$key:{"param":"hodnota"}`.

**Payload:** text zprávy nebo lokalizační řetězec

**Příklad:**
```
stavHry:Probíhá duel mezi hráčem Petr a Honza
```

---

##### `obrazekDobiracihoBalicku:<nazev>`
Nastavení obrázku vrchní karty nebo rubu dobíracího balíčku.

**Payload:** název/cesta obrázku

**Příklad:**
```
obrazekDobiracihoBalicku:karta_zadni_zelena
```

---

##### `koloStesti:<json>`
Zobrazení serverem řízené animace kola štěstí. Výsledek je plně určen serverem.

**Payload:** JSON se seznamem možností, vybraným indexem a nadpisem

**Struktura JSON:**
```json
{
  "moznosti": [
    {"id": 0, "name": "Zásah", "barva": "#ff0000", "velikost": 1},
    {"id": 1, "name": "Minul", "barva": "#00ff00", "velikost": 2}
  ],
  "vybranaMoznost": 1,
  "nadpis": "Kolo štěstí"
}
```

**Příklad:**
```
koloStesti:{"moznosti":[{"id":0,"name":"Zásah","barva":"#ff0000","velikost":1},{"id":1,"name":"Minul","barva":"#00ff00","velikost":2}],"vybranaMoznost":1,"nadpis":"Barel"}
```

---

##### `vyberKartu:<json>`
Dialog pro výběr jedné nebo více karet.

**Payload:** JSON objekt s ID dialogu, seznamem karet, min/max počtem, zda je dialog zavíratelný a volitelným nadpisem

**Struktura JSON:**
```json
{
  "id": 3,
  "karty": [
    {"id": 42, "jmeno": "bang", "obrazek": "bang.png"}
  ],
  "nadpis": "Vyber kartu k odhození",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

**Příklad:**
```
vyberKartu:{"id":3,"karty":[{"id":42,"jmeno":"bang","obrazek":"bang.png"}],"nadpis":"Vyber kartu k odhození","min":1,"max":1,"notClosable":true}
```

**Očekávaná odpověď:** `dialog:<id>,<idKarty>` (nebo čárkou oddělená ID karet)

---

##### `vyberAkci:<json>`
Dialog pro výběr z možností.

**Payload:** JSON objekt s ID dialogu, seznamem akcí, nadpisem a příznakem zavíratelnosti

**Struktura JSON:**
```json
{
  "id": 1,
  "akce": [
    {"id": 0, "nazev": "Ano"},
    {"id": 1, "nazev": "Ne"}
  ],
  "nadpis": "Chceš použít schopnost?",
  "notClosable": true
}
```

**Příklad:**
```
vyberAkci:{"id":1,"akce":[{"id":0,"nazev":"Ano"},{"id":1,"nazev":"Ne"}],"nadpis":"Použít barel?","notClosable":true}
```

**Očekávaná odpověď:** `dialog:<id>,<idAkce>`

---

##### `vyberHrace:<json>`
Dialog pro výběr jednoho nebo více hráčů.

**Payload:** JSON objekt s ID dialogu, seznamem ID hráčů, min/max limity a volitelným nadpisem

**Struktura JSON:**
```json
{
  "id": 2,
  "hraci": [0, 1, 2],
  "nadpis": "Vyber hráče na kterého chceš střílet",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

**Příklad:**
```
vyberHrace:{"id":2,"hraci":[0,1,2],"nadpis":"Vyber hráče","min":1,"max":1,"notClosable":true}
```

**Očekávaná odpověď:** `dialog:<id>,<idHrace>` (nebo čárkou oddělená ID hráčů)

---

##### `vyberText:<json>`
Dialog s textovým vstupem pro hráče.

**Payload:** JSON objekt s ID dialogu, titulkem, zástupným textem, textem tlačítka a příznakem zavíratelnosti

**Struktura JSON:**
```json
{
  "id": 4,
  "title": "Zadej heslo",
  "placeholder": "Zde napiš...",
  "buttonText": "Potvrdit",
  "notClosable": false
}
```

**Příklad:**
```
vyberText:{"id":4,"title":"Hádej slovo","placeholder":"Napiš tip","buttonText":"Odeslat","notClosable":false}
```

**Očekávaná odpověď:** `dialog:<id>,<text>`

---

##### `vysledkyHry:<json>`
Matice konečného pořadí hráčů. Jednotlivé řádky odpovídají umístěním (1. místo je na indexu 0).

**Payload:** JSON 2D pole čísel (ID hráčů)

**Příklad:**
```
vysledkyHry:[[0],[1,2],[3]]
```

---

##### `konecHry`
Oznámení o ukončení hry. Klient vyresetuje načtené překlady pluginu a zobrazí dialog konce hry.

**Payload:** žádný

**Příklad:**
```
konecHry
```

---

##### `vyhral:<idHrace>`
Konec hry - konkrétní hráč vyhrál.

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
Chybová zpráva. Všechny chyby definované v serverovém enumu `cz.honzaa.bang.sdk.Chyba` obsahují číselný kód (1–22), skupinu (0–3) a lokalizační klíč nebo zprávu v poli `error`.

**Struktura JSON:**
```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```

**Skupiny chyb:**
- `0` - Interní chyba serveru
- `1` - Špatný formát zprávy / neplatná operace
- `2` - Chybějící data / spojení
- `3` - Nedodržení herních pravidel

**Přehled kódů chyb (1–22):**
- `1`: `NEPRIPOJEN_KE_HRE` ("$error.nepripojen_ke_hre", skupina 2)
- `2`: `KARTA_NEEXISTUJE` ("$error.karta_neexistuje", skupina 2)
- `3`: `KARTA_NENI_HRATELNA` ("$error.karta_neni_hratelna", skupina 1)
- `4`: `KARTA_NEJDE_ZAHRAT` ("$error.karta_nejde_zahrat", skupina 3)
- `5`: `HRA_NEEXISTUJE` ("$error.hra_neexistuje", skupina 1)
- `6`: `POSTAVA_NENI_NA_VYBER` ("$error.postava_neni_na_vyber", skupina 1)
- `7`: `NEJSI_NA_TAHU` ("$error.nejsi_na_tahu", skupina 3)
- `8`: `NEMUZES_UKONCIT_TAH` ("$error.nemuzes_ukoncit_tah", skupina 3)
- `9`: `KARTA_NEJDE_SPALIT` ("$error.karta_nejde_spalit", skupina 1)
- `10`: `NENI_VYLOZITELNA` ("$error.neni_vylozitelna", skupina 1)
- `11`: `KARTU_NEJDE_VYLOZIT` ("$error.kartu_nejde_vylozit", skupina 3)
- `12`: `CHYBA_PROTOKOLU` ("$error.chyba_protokolu", skupina 1)
- `13`: `NEJDE_SI_LIZNOUT` ("$error.nejde_si_liznout", skupina 3)
- `14`: `SPATNE_HESLO` ("$error.spatne_heslo", skupina 1)
- `15`: `UZ_PRIPOJEN` ("$error.uz_pripojen", skupina 2)
- `16`: `DOSLI_KARTY_V_BALICKU` ("$error.dosli_karty_v_balicku", skupina 3)
- `17`: `NEJSI_ADMIN_HRY` ("$error.nejsi_admin_hry", skupina 1)
- `18`: `PLNY_SERVER` ("$error.plny_server", skupina 1)
- `19`: `VYHOZEN_ZE_HRY` ("$error.vyhozen_ze_hry", skupina 1)
- `20`: `HRA_UZ_ZAHAJENA` ("$error.hra_uz_zahajena", skupina 1)
- `21`: `HRA_NEPODPORUJE` ("$error.hra_nepodporuje", skupina 1)
- `22`: `PRIPOJEN_V_JINE_ZALOZCE` ("$error.pripojen_v_jine_zalozce", skupina 2)

**Poznámka:** Podrobné informace o všech chybách naleznete v [chyby.md](chyby.md).

---

### 2.5 Ostatní zprávy

##### `Echo: <zprava>`
Echo zpráva (pro debugování spojení).

**Payload:** původní zpráva

**Příklad:**
```
Echo: konecTahu
```

---

##### `popoup:<text>`
Vyskakovací okno s textem (používáno zřídka pro mimořádná oznámení).

**Payload:** text zprávy

**Příklad:**
```
popoup:Důležitá zpráva
```

---

## 3. Typické scénáře použití

### 3.1 Vytvoření nové hry

```
1. Klient → Server: připojení WebSocket
2. Klient → Server: posthogId:<id>
3. Server → Klient: welcome
4. Klient → Server: infoHer
5. Server → Klient: infoHer:<json se seznamem her>
6. Klient → Server: novaHra:0
7. Server → Klient: novaHra:123456
8. Server → Klient: pripojenKeHre
9. Server → Klient: token:123456abcdef...
10. Server → Klient: novyHrac:<json>
11. Klient → Server: noveJmeno:Honza
12. Server → Klient: noveJmeno:0,Honza
13. Klient → Server: nactiPreklady:cs
14. Server → Klient: preklady:<json>
```

### 3.2 Připojení k existující hře

```
1. Klient → Server: připojení WebSocket
2. Klient → Server: posthogId:<id>
3. Server → Klient: welcome
4. Klient → Server: pripojeniKeHre:123456
5. Server → Klient: pripojenKeHre
6. Server → Klient: token:123456xyz...
7. Server → Klient: novyHrac:<json>
8. Server → všem: novyHrac:<json nového hráče>
9. Klient → Server: noveJmeno:Petr
10. Server → všem: noveJmeno:1,Petr
11. Klient → Server: nactiPreklady:cs
12. Server → Klient: preklady:<json>
```

### 3.3 Znovupřipojení k hře a řešení více záložek

```
1. Klient → Server: připojení WebSocket
2. Klient → Server: posthogId:<id>
3. Server → Klient: welcome
4. Klient → Server: overeniTokenu:123456abcdef...
5. Server → Klient: overeniTokenu:true
6. Klient → Server: vraceniSe:123456abcdef...
   - Pokud byl hráč připojen v jiné záložce:
     Server → staré spojení: error:{"error":"$error.pripojen_v_jine_zalozce","kod":22,"skupina":2}
     Server uzavře staré WebSocket spojení
     Stará záložka zastaví auto-reconnect a zobrazí dialog "Přepnout hru sem"
7. Server → Klient: pripojenKeHre
8. Server → Klient: hraZacala (pokud již hra běží)
9. Server → Klient: nactiHru (kompletní stav: hraci, role, novaKarta, vylozene karty, UI tlačítka)
```

### 3.4 Odehrání karty v hře

```
1. Klient → Server: odehrani:42
2. Server → všem: odehrat:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
3. Server → cílový hráč: vyberAkci:<json s možnostmi reakce>
4. Klient → Server: dialog:1,0
5. Server → všem: aktualizace stavu podle výsledku akce
```

## 4. Poznámky k implementaci

### 4.1 Bezpečnost a autentizace
- Token pro znovupřipojení obsahuje kód hry (6 číslic) + unikátní kryptograficky náhodný identifikátor.
- Administrátorské zprávy (`serverInfo`, `gameInfo`, `restartovatPluginy`) jsou chráněny heslem (konfigurovatelné přes `ADMIN_PASSWORD`).
- Při reconnectu z jiné záložky dochází k bezpečnému odpojení předchozí relace s kódem chyby 22 (`PRIPOJEN_V_JINE_ZALOZCE`), aby nedocházelo k souběhu zpráv a cyklickému znovupřipojování.

### 4.2 Limity a časování
- Kód hry: 6-místné číslo (100000–999999).
- Maximální počet hráčů v jedné místnosti: 30 (limit serveru).
- **Timeout neaktivní hry:** 3 600 000 ms (1 hodina) od odpojení posledního hráče. Pokud se jakýkoliv hráč v průběhu této doby znovu připojí, timeout je okamžitě zrušen.
- **Správa administrátora hry:** Pokud místnost opustí nebo se odpojí její zakladatel (administrátor hry), server automaticky deleguje administrátorská práva na dalšího připojeného hráče.

### 4.3 Lokalizace
Protokol podporuje dynamickou lokalizaci:
- Klient může pomocí `nactiPreklady:<jazyk>` stáhnout lokalizační slovník konkrétního herního pluginu (`preklady:<json>`).
- Server v textech (chyby, `stavHry`, dialogy, názvy tlačítek) posílá buď klíč `$klic`, nebo klíč s parametry `$klic:{"parametr":"hodnota"}`. Klient rekurzivně překládá tyto hodnoty přes i18n bundle.

### 4.4 JSON formát
JSON objekty v payload jsou posílány bez mezer a na jednom řádku v kódování UTF-8. Klient by měl být odolný vůči novým polím v JSON objektech.
