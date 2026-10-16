# Fáze před hrou

Tato fáze začíná po úspěšném připojení k serveru a trvá až do zahájení hry. V této fázi se hráči připojují, nastavují si jména, vybírají postavy a připravují se na start hry.

## Pořadí akcí

### Scénář 1: Vytvoření nové hry

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Klient** pošle `posthogId:<id>` (nebo `posthogId:ignore`)
3. **Server** pošle `welcome`, čímž potvrdí spojení
4. **Klient** pošle `infoHer`, tím si vyžádá informace o dostupných hrách
5. **Server** odpoví `infoHer:<json>` se seznamem dostupných her
6. **Klient** si vybere hru a pošle `novaHra:<typHry>`
7. **Server** vytvoří hru a odpoví:
   - `novaHra:<kodHry>` - kód nově vytvořené hry
   - `pripojenKeHre` - potvrzení připojení
   - `token:<token>` - token pro možnost znovupřipojení
   - `novyHrac:<json>` - informace o novém hráči (s `"isAdmin": true`)
8. **Klient** může nastavit jméno pomocí `noveJmeno:<jmeno>`
9. **Server** rozešle všem `noveJmeno:<idHrace>,<jmeno>`
10. **Klient** pošle `nactiPreklady:<jazyk>` a server odpoví `preklady:<json>`
11. **Server** pošle `vyberPostavu:<json>` s nabídkou postav
12. **Klient** vybere postavu pomocí `setPostava:<postava>`
13. **Server** rozešle všem `setPostava:<idHrace>,<postava>`
14. Další hráči se mohou připojit (viz Scénář 2)
15. **Klient** (hostitel / administrátor) může zahájit hru pomocí `zahajeniHry`
16. **Server** přejde do fáze hry a rozešle všem `hraZacala`

### Scénář 2: Připojení k existující hře

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Klient** pošle `posthogId:<id>`
3. **Server** pošle `welcome`
4. **Klient** pošle `infoHer` (volitelné, pokud chce informace o serveru)
5. **Klient** pošle `pripojeniKeHre:<kodHry>` s kódem existující hry
6. **Server** ověří, že hra existuje a ještě nezačala, pak odpoví:
   - `pripojenKeHre` - potvrzení připojení
   - `token:<token>` - token pro znovupřipojení
   - `novyHrac:<json>` - informace o novém hráči
7. **Server** rozešle **všem ostatním** hráčům `novyHrac:<json>` s informací o nově připojeném hráči
8. **Klient** může nastavit jméno pomocí `noveJmeno:<jmeno>`
9. **Server** rozešle všem `noveJmeno:<idHrace>,<jmeno>`
10. **Klient** pošle `nactiPreklady:<jazyk>` a server vrátí `preklady:<json>`
11. **Klient** může požádat o aktuální stav pomocí `nactiHru`
12. **Server** pošle aktuální stav hry (seznam hráčů `hraci:<json>`, apod.)
13. **Server** pošle `vyberPostavu:<json>` s nabídkou postav
14. **Klient** vybere postavu pomocí `setPostava:<postava>`
15. **Server** rozešle všem `setPostava:<idHrace>,<postava>`

### Scénář 3: Znovupřipojení k hře a správa více záložek

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Klient** pošle `posthogId:<id>`
3. **Server** pošle `welcome`
4. **Klient** může ověřit platnost tokenu pomocí `overeniTokenu:<token>`
5. **Server** odpoví `overeniTokenu:true` nebo `overeniTokenu:false`
6. **Klient** pošle `vraceniSe:<token>`
7. Pokud byl hráč již připojen v jiné záložce/okně:
   - Server pošle původnímu spojení chybu `error:{"error":"$error.pripojen_v_jine_zalozce","kod":22,"skupina":2}` a uzavře jeho socket.
   - Původní klient zablokuje automatický reconnect a zobrazí dialog "Přepnout hru sem".
8. **Server** odpoví novému spojení:
   - `pripojenKeHre` - potvrzení připojení
   - `hraZacala` - pokud již hra běží
   - Aktuální stav hry (automaticky zavolá načtení stavu včetně hráčů, karet a vlastních tlačítek)
9. Server rozešle ostatním `pripojeniHrace:<idHrace>`

### Scénář 4: Správa lobby administrátorem

Administrátor hry (hostitel, který hru založil, nebo na kterého práva přešla po odpojení původního hostitele) má v lobby dodatečné pravomoci:
- **Přidání bota:** Administrátor odešle `pridejBota` (nebo `pridejBota:<jmeno>`). Pokud hra boty podporuje, server vytvoří virtuálního hráče s `"isBot": true` a rozešle `novyHrac:<json>` a aktualizovaný seznam `hraci:<json>`. Pokud hra boty nepodporuje, vrátí chybu `HRA_NEPODPORUJE` (kód 21).
- **Vyhození hráče:** Administrátor odešle `vyhodHrace:<idHrace>`. Cílový hráč obdrží chybu `VYHOZEN_ZE_HRY` (kód 19) a jeho spojení je ukončeno. Ostatním se rozešle aktualizovaný seznam hráčů.
- **Změna / nahrazení hry:** Administrátor může poslat `nahradHru:<typHry>`. Server vytvoří novou místnost a rozešle `nahradHruBezPtani:<noveId>,<jmeno>` administrátorovi a `nahradHru:<noveId>,<jmeno>` ostatním hráčům s dotazem na přechod do nové hry.

## Přehled zpráv

### Zprávy od klienta k serveru

#### `posthogId:<id>`
**Účel:** Asociace telemetrie a analytického ID klienta (nebo `posthogId:ignore`) se spojením na serveru.

**Payload:** ID klienta nebo `ignore`

**Kdy poslat:** Ihned po navázání WebSocket spojení

**Příklad:**
```
posthogId:user_abc123
posthogId:ignore
```

---

#### `infoHer`
**Účel:** Získání informací o serveru a dostupných hrách

**Payload:** žádný

**Kdy poslat:** Hned po přijetí `welcome` nebo kdykoliv před připojením k hře

**Příklad:**
```
infoHer
```

---

#### `novaHra:<typHry>`
**Účel:** Vytvoření nové hry

**Payload:** číslo - ID typu hry (získané z odpovědi na `infoHer`)

**Kdy poslat:** Po obdržení seznamu her z `infoHer`

**Omezení:** Hráč nemůže vytvořit novou hru, pokud je již připojen k jiné hře

**Příklad:**
```
novaHra:0
```

**Možné chyby:**
- `{"error":"Už jsi připojen ke hře.","kod":15,"skupina":2}` - pokud už jste ve hře

---

#### `pripojeniKeHre:<kodHry>`
**Účel:** Připojení k existující hře

**Payload:** 6-místný kód hry

**Kdy poslat:** Kdykoliv před připojením k jiné hře

**Omezení:** 
- Hráč nemůže být připojen k více hrám současně
- Hra nesmí být již zahájena

**Příklad:**
```
pripojeniKeHre:123456
```

**Možné chyby:**
- `{"error":"Už jsi připojen ke hře.","kod":15,"skupina":2}` - už jste ve hře
- `{"error":"Hra, ke které se snažíš připojit neexistuje.","kod":5,"skupina":1}` - neplatný kód
- `{"error":"tato hra už byla zahájena. Bohužel se už nejde připojit."}` - hra již začala

---

#### `overeniTokenu:<token>`
**Účel:** Ověření, zda je token a příslušná herní místnost stále platná, ještě před pokusem o znovupřipojení.

**Payload:** token ve formátu `<kodHry(6)><identifikator>`

**Kdy poslat:** Po obnovení spojení nebo načtení stránky s uloženým tokenem

**Příklad:**
```
overeniTokenu:123456abcdef1234567890
```

**Odpověď serveru:** `overeniTokenu:true` nebo `overeniTokenu:false`

---

#### `vraceniSe:<token>`
**Účel:** Znovupřipojení k hře po výpadku spojení nebo otevření záložky

**Payload:** token ve formátu `<kodHry><identifikator>` (celkem 22 znaků: 6 číslic + 16 znaků)

**Kdy poslat:** Po ověření tokenu nebo obnovení spojení

**Poznámka:** Pokud byl hráč již připojen v jiné záložce či okně, server pošle starému spojení chybu `PRIPOJEN_V_JINE_ZALOZCE` (kód 22) a odpojí jej, čímž zabrání cyklení reconnectu.

**Příklad:**
```
vraceniSe:123456abcdef1234567890
```

**Možné chyby:**
- `{"error":"hráč v této hře nenalezen"}` - neplatný token nebo hra již neexistuje
- `{"error":"Hra do které se snažíš připojit neexistuje"}` - neplatný kód hry

---

#### `noveJmeno:<jmeno>`
**Účel:** Nastavení nebo změna jména hráče

**Payload:** nové jméno (String)

**Kdy poslat:** Kdykoliv po připojení k hře

**Omezení:** Musíte být připojeni k hře

**Příklad:**
```
noveJmeno:Honza
```

**Možné chyby:**
- `{"error":"Nejsi připojen ke hře"}` - pokud nejste ve hře

---

#### `setPostava:<postava>`
**Účel:** Výběr postavy

**Payload:** identifikátor postavy (String)

**Kdy poslat:** Po obdržení `vyberPostavu` se seznamem dostupných postav

**Omezení:** 
- Musíte být připojeni k hře
- Hra nesmí být zahájena
- Postava musí být v nabídce

**Příklad:**
```
setPostava:BART_CASSIDY
```

---

#### `pridejBota` / `pridejBota:<jmeno>`
**Účel:** Přidání počítačem řízeného hráče (bota) do lobby

**Payload:** volitelné jméno bota

**Kdy poslat:** V lobby před zahájením hry (pouze administrátor hry)

**Omezení:**
- Pouze administrátor hry
- Hra nesmí být zahájena
- Herní plugin musí podporovat boty (`podporujeBoty()`)
- Maximálně 30 hráčů v místnosti

**Příklad:**
```
pridejBota
pridejBota:Bot Pepa
```

**Možné chyby:**
- `{"error":"$error.nejsi_admin_hry","kod":17,"skupina":1}` - pokud nejste admin
- `{"error":"$error.hra_uz_zahajena","kod":20,"skupina":1}` - hra už běží
- `{"error":"$error.hra_nepodporuje_boty","kod":21,"skupina":1}` - plugin nepodporuje boty

---

#### `vyhodHrace:<idHrace>`
**Účel:** Vyhození hráče z lobby před zahájením hry

**Payload:** ID vyhazovaného hráče

**Kdy poslat:** V lobby před zahájením hry (pouze administrátor hry)

**Omezení:**
- Pouze administrátor hry
- Hra nesmí být zahájena

**Příklad:**
```
vyhodHrace:2
```

**Reakce serveru:** Vyhozenému hráči je zaslána chyba `VYHOZEN_ZE_HRY` (kód 19) a spojení je ukončeno. Ostatním hráčům je rozeslán aktualizovaný seznam hráčů.

---

#### `nahradHru:<typHry>`
**Účel:** Změna typu hry v lobby a přesun hráčů do nové místnosti

**Payload:** ID nového typu hry

**Kdy poslat:** V lobby (pouze administrátor hry)

**Příklad:**
```
nahradHru:1
```

**Reakce serveru:** Administrátor obdrží `nahradHruBezPtani:<noveId>,<jmeno>` a ostatní hráči `nahradHru:<noveId>,<jmeno>` s potvrzovacím dialogem.

---

#### `nactiPreklady` / `nactiPreklady:<jazyk>`
**Účel:** Stažení lokalizačních řetězců herního pluginu pro požadovaný jazyk

**Payload:** volitelný kód jazyka (např. `cs`, `en`)

**Kdy poslat:** Po připojení ke hře nebo při změně jazyka

**Příklad:**
```
nactiPreklady:cs
```

**Odpověď serveru:** `preklady:<json>`

---

#### `zahajeniHry`
**Účel:** Zahájení hry

**Payload:** žádný

**Kdy poslat:** Když jsou všichni hráči připraveni (obvykle může poslat pouze hostitel)

**Omezení:**
- Musíte být připojeni k hře
- Hra nesmí být již zahájena
- Musí být dostatečný počet hráčů

**Příklad:**
```
zahajeniHry
```

---

#### `nactiHru`
**Účel:** Načtení aktuálního stavu hry

**Payload:** žádný

**Kdy poslat:** Kdykoliv po připojení k hře pro získání aktuálních informací

**Příklad:**
```
nactiHru
```

---

#### `serverInfo:<heslo>`
**Účel:** Získání detailních informací o serveru (pouze pro administrátory)

**Payload:** heslo (String)

**Kdy poslat:** Kdykoliv, ale určeno pouze pro správu serveru

**Příklad:**
```
serverInfo:heslo123
```

**Možné chyby:**
- `{"error":"Špatné heslo.","kod":14,"skupina":1}` - nesprávné heslo

---

#### `gameInfo:<kod>:<heslo>`
**Účel:** Získání stavu konkrétní hry v JSON formátu (pouze pro administrátory)

**Payload:** kód hry a heslo oddělené dvojtečkou

**Kdy poslat:** Kdykoliv pro kontrolu herního stavu administrátorem serveru

**Příklad:**
```
gameInfo:123456:heslo123
```

**Odpověď serveru:** `gameInfoJSON:<json>` nebo `error:<json>`

---

#### `getIdHry`
**Účel:** Dotaz na ID aktuální hry

**Payload:** žádný

**Kdy poslat:** Kdykoliv po připojení ke hře

**Příklad:**
```
getIdHry
```

---

#### `novaHraSHracema:<typHry>`
**Účel:** Restartování hry se stejnými hráči (pouze pro administrátora hry)

**Payload:** číslo - ID typu hry

**Kdy poslat:** Po skončení hry, pokud chcete hrát znovu se stejnými hráči

**Omezení:** Pouze administrátor hry

**Příklad:**
```
novaHraSHracema:0
```

**Možné chyby:**
- `{"error":"Nejsi administrátor hry.","kod":17,"skupina":1}` - nemáte práva administrátora

---

#### `restartovatPluginy:<heslo>`
**Účel:** Znovunačtení herních pluginů (pouze pro administrátory serveru)

**Payload:** heslo serveru (String)

**Kdy poslat:** Pouze pro správu serveru

**Příklad:**
```
restartovatPluginy:heslo123
```

**Možné chyby:**
- `{"error":"Špatné heslo.","kod":14,"skupina":1}` - nesprávné heslo

---

### Zprávy od serveru ke klientovi

#### `welcome`
**Účel:** Potvrzení úspěšného připojení

**Payload:** žádný

**Kdy čekat:** Ihned po navázání WebSocket spojení

**Co dělat:** Odpovědět zprávou `infoHer` pro získání informací o serveru

**Příklad:**
```
welcome
```

---

#### `infoHer:<json>`
**Účel:** Informace o serveru a dostupných hrách

**Payload:** JSON s verzí serveru a seznamem her

**Struktura:**
```json
{
  "verze": "0.0.7",
  "hry": [
    {
      "id": 0,
      "jmeno": "Bang!",
      "popis": "Populární hra s cílem zabít ostatní hráče.",
      "url": "https://albi.cz/bang-pravidla.pdf"
    },
    {
      "id": 1,
      "jmeno": "UNO",
      "popis": "Slavná základní karetní hra.",
      "url": ""
    }
  ]
}
```

**Kdy čekat:** Po odeslání `infoHer`

**Co dělat:** Zobrazit seznam her a umožnit hráči vytvořit novou hru nebo zadat kód existující

**Příklad:**
```
infoHer:{"verze":"0.0.7","hry":[{"id":0,"jmeno":"Bang!","popis":"Populární hra s cílem zabít ostatní hráče.","url":"https://albi.cz/bang-pravidla.pdf"}]}
```

---

#### `novaHra:<kodHry>`
**Účel:** Potvrzení vytvoření nové hry a její kód

**Payload:** 6-místný kód hry (číslo)

**Kdy čekat:** Po odeslání `novaHra:<typHry>`

**Co dělat:** Uložit a zobrazit kód hry, aby se k ní mohli připojit další hráči

**Příklad:**
```
novaHra:123456
```

---

#### `pripojenKeHre`
**Účel:** Potvrzení úspěšného připojení k hře

**Payload:** žádný

**Kdy čekat:** 
- Po odeslání `novaHra:<typHry>`
- Po odeslání `pripojeniKeHre:<kodHry>`
- Po odeslání `vraceniSe:<token>`

**Co dělat:** Přejít do lobby obrazovky

**Příklad:**
```
pripojenKeHre
```

---

#### `token:<token>`
**Účel:** Token pro možnost znovupřipojení

**Payload:** token ve formátu `<kodHry><identifikator>`

**Kdy čekat:** Po úspěšném připojení k hře

**Co dělat:** Uložit token do localStorage nebo jiného trvalého úložiště

**Příklad:**
```
token:123456abcdef1234567890
```

---

#### `overeniTokenu:<boolean>`
**Účel:** Ověření, zda token je ještě platný.

**Payload:** `true` nebo `false`

**Kdy čekat:** Po odeslání `overeniTokenu:<token>`

**Příklad:**
```
overeniTokenu:true
overeniTokenu:false
```

---

#### `novyHrac:<json>`
**Účel:** Informace o novém hráči ve hře

**Payload:** JSON objekt s informacemi o hráči

**Struktura:**
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

**Kdy čekat:** 
- Po vlastním připojení k hře
- Když se připojí nový lidský hráč nebo bot (rozesláno všem)

**Co dělat:** Přidat hráče do seznamu hráčů v lobby

**Příklad:**
```
novyHrac:{"id":0,"jmeno":"Hráč 1","zivoty":4,"pocetKaret":0,"maximumZivotu":4,"isAdmin":true,"isOnline":true,"isBot":false,"vylozeneKarty":[]}
```

---

#### `pripojeniHrace:<idHrace>`
**Účel:** Oznámení o znovupřipojení dříve odpojeného hráče

**Payload:** ID hráče

**Kdy čekat:** Když se odpojený hráč znovu připojí pomocí `vraceniSe`

**Co dělat:** Nastavit stav hráče na online (`isOnline: true`)

**Příklad:**
```
pripojeniHrace:1
```

---

#### `odpojeniHrace:<idHrace>`
**Účel:** Oznámení o odpojení hráče (ztráta WebSocket spojení)

**Payload:** ID hráče

**Kdy čekat:** Při pádu nebo uzavření spojení hráče

**Co dělat:** Nastavit stav hráče na offline (`isOnline: false`)

**Příklad:**
```
odpojeniHrace:1
```

---

#### `noveJmeno:<idHrace>,<jmeno>`
**Účel:** Změna jména hráče

**Payload:** ID hráče a nové jméno oddělené čárkou

**Kdy čekat:** Po odeslání `noveJmeno:<jmeno>` nebo když jiný hráč změní jméno

**Co dělat:** Aktualizovat jméno hráče v seznamu hráčů

**Příklad:**
```
noveJmeno:0,Honza
```

---

#### `setPostava:<idHrace>,<postava>`
**Účel:** Změna postavy hráče

**Payload:** ID hráče a identifikátor postavy oddělené čárkou

**Kdy čekat:** Po odeslání `setPostava:<postava>` nebo když jiný hráč vybere postavu

**Co dělat:** Aktualizovat postavu hráče v seznamu hráčů

**Příklad:**
```
setPostava:0,BART_CASSIDY
```

---

#### `vyberPostavu:<json>`
**Účel:** Nabídka postav pro výběr

**Payload:** JSON pole objektů s informacemi o postavách

**Struktura:**
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

**Kdy čekat:** Po připojení k hře nebo po vytvoření nové hry

**Co dělat:** Zobrazit dialog s výběrem postav

**Příklad:**
```
vyberPostavu:[{"jmeno":"BART_CASSIDY","obrazek":"BART_CASSIDY","popis":"Kdykoliv je zasažen, lízne si kartu.","zivoty":"4"}]
```

---

#### `hraci:<json>`
**Účel:** Kompletní seznam všech hráčů v herní místnosti

**Payload:** JSON pole objektů hráčů

**Struktura:**
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

**Kdy čekat:** Po odeslání `nactiHru`, po znovupřipojení, nebo po změně v lobby (např. přidání bota či vyhození hráče)

**Co dělat:** Nahradit lokální seznam hráčů tímto seznamem

**Příklad:**
```
hraci:[{"id":0,"jmeno":"Hráč 1","zivoty":4,"pocetKaret":5,"role":"šerif","postava":"BART_CASSIDY","maximumZivotu":4,"isAdmin":true,"isOnline":true,"isBot":false,"vylozeneKarty":[]}]
```

---

#### `nahradHru:<noveId>,<jmeno>` / `nahradHruBezPtani:<noveId>,<jmeno>`
**Účel:** Pozvánka nebo přesměrování do nové hry po změně typu hry administrátorem

**Payload:** kód nové hry a jméno hráče, který změnu vyvolal

**Kdy čekat:** Po odeslání `nahradHru` administrátorem

**Co dělat:** Zobrazit potvrzovací dialog (pro ostatní hráče) nebo okamžitě přejít do nové místnosti (pro administrátora)

**Příklad:**
```
nahradHru:654321,Honza
nahradHruBezPtani:654321,Honza
```

---

#### `preklady:<json>`
**Účel:** Lokalizační balíček herního pluginu pro aktuální jazyk klienta

**Payload:** JSON mapa klíčů a přeložených textů

**Kdy čekat:** V reakci na zprávu `nactiPreklady:<jazyk>`

**Co dělat:** Zaregistrovat překlady do lokalizačního subsystému klienta (např. i18next do namespace `plugin`)

**Příklad:**
```
preklady:{"karta.bang":"Bang!","popis.bang":"Vystřelí na jiného hráče."}
```

---

#### `serverDataHTML:<html>`
**Účel:** HTML stránka s informacemi o serveru (pouze pro administrátory)

**Payload:** HTML kód

**Kdy čekat:** Po odeslání `serverInfo:<heslo>` se správným heslem

**Co dělat:** Zobrazit HTML v iframe nebo novém okně

**Příklad:**
```
serverDataHTML:<div><h1>Server data</h1><p>Počet her: 5</p></div>
```

---

#### `gameInfoJSON:<json>`
**Účel:** JSON stav konkrétní hry (pouze pro administrátory)

**Payload:** JSON objekt se stavem hry

**Kdy čekat:** Po odeslání `gameInfo:<kod>:<heslo>` se správným heslem

**Příklad:**
```
gameInfoJSON:{"id":123456,"pocetHracu":4,"zahajena":true}
```

---

#### `noveIdHrace:<id>`
**Účel:** Přidělení ID hráče po připojení ke hře

**Payload:** číslo

**Kdy čekat:** Po úspěšném připojení ke hře (po `pripojenKeHre`)

**Co dělat:** Uložit ID hráče pro pozdější identifikaci

**Příklad:**
```
noveIdHrace:4
```

---

#### `povoleneUI:<json>`
**Účel:** Informace o povolených prvcích uživatelského rozhraní

**Payload:** JSON pole názvů povolených UI prvků

**Kdy čekat:** Po připojení ke hře nebo po změně stavu hry

**Co dělat:** Zobrazit/skrýt příslušné herní prvky

**Příklad:**
```
povoleneUI:["ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"]
```

---

#### `setIdHry:<id>`
**Účel:** Odpověď na dotaz `getIdHry` - ID aktuální hry

**Payload:** číslo - ID hry

**Kdy čekat:** Po odeslání `getIdHry`

**Příklad:**
```
setIdHry:123456
```

---

#### `error:<json>`
**Účel:** Chybová zpráva

**Payload:** JSON objekt s informací o chybě, kódem a skupinou

**Struktura:**
```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```

**Kdy čekat:** Kdykoliv, když dojde k chybě

**Co dělat:** Zobrazit chybovou zprávu uživateli (s překladem lokalizačního klíče)

**Příklady:**
```
error:{"error":"$error.uz_pripojen","kod":15,"skupina":2}
error:{"error":"$error.hra_neexistuje","kod":5,"skupina":1}
error:{"error":"$error.nepripojen_ke_hre","kod":1,"skupina":2}
error:{"error":"$error.pripojen_v_jine_zalozce","kod":22,"skupina":2}
```

---

## Stavový diagram

```
[Připojení] 
    ↓ (WebSocket připojeno)
[Analytika] - posthogId:<id>
    ↓
[Uvítání] - welcome
    ↓ (infoHer)
[Výběr hry] - infoHer:<json>
    ↓ 
    ├─→ [Vytvoření hry] - novaHra:<typHry>
    │       ↓
    │   [Nová hra vytvořena] - novaHra:<kodHry>, pripojenKeHre, token:<token>, novyHrac:<json>
    │       ↓
    │   [Lobby] ←─── (přidání botů: pridejBota, vyhození: vyhodHrace, změna: nahradHru)
    │
    ├─→ [Připojení k existující hře] - pripojeniKeHre:<kodHry>
    │       ↓
    │   [Připojen] - pripojenKeHre, token:<token>, novyHrac:<json>
    │       ↓
    │   [Lobby]
    │
    └─→ [Znovupřipojení] - overeniTokenu:<token> → vraceniSe:<token>
            ↓
        [Připojen zpět] - pripojenKeHre, nactiHru, (odpojení staré záložky error 22)
            ↓
        [Lobby nebo Hra podle stavu]

V Lobby:
    [Výběr postavy] - vyberPostavu:<json> → setPostava:<postava>
    [Lokalizace] - nactiPreklady:<jazyk> → preklady:<json>
    [Čekání na ostatní / Start]
        ↓ (zahajeniHry hostitelem)
    [Zahájení hry] - hraZacala
        ↓
    [Fáze hra]
```

## Poznámky

1. **Token:** Vždy si uložte token poskytnutý serverem (localStorage / sessionStorage). Umožňuje bezpečné znovupřipojení po výpadku i po obnovení stránky.

2. **Pořadí zpráv:** Některé zprávy jsou závislé na předchozích. Musíte být připojeni k hře (`pripojenKeHre`) před tím, než můžete změnit jméno nebo vybrat postavu.

3. **Broadcast zprávy:** Zprávy jako `novyHrac`, `noveJmeno`, `setPostava`, `pripojeniHrace`, `odpojeniHrace` jsou rozeslány všem hráčům ve hře.

4. **Kód hry:** Je 6-místné číslo generované serverem v rozsahu 100000–999999.

5. **Automatické mazání a timeout:** Pokud se všichni hráči odpojí, hra se automaticky smaže po 1 hodině (3 600 000 ms). Jakmile se jakýkoliv hráč znovu připojí, odpočet se okamžitě zruší.

6. **Více záložek (Multi-tab reconnect):** Pokud se hráč připojí z jiné záložky se stejným tokenem, server předchozí spojení odpojí chybou `PRIPOJEN_V_JINE_ZALOZCE` (kód 22). Klient v původní záložce vypne automatický reconnect a nabídne tlačítko pro přepnutí hry zpět.

7. **Migrace administrátora:** Pokud administrátor hry (hostitel) opustí lobby nebo se odpojí, server automaticky předá administrátorská práva jinému online hráči.

8. **Lokalizace pluginů:** Pomocí `nactiPreklady:<jazyk>` si klient stahuje překlady přímo z pluginu dané hry, což umožňuje lokalizovat i komunitní či externí pluginy.
