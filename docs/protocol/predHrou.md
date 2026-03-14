# Fáze před hrou

Tato fáze začíná po úspěšném připojení k serveru a trvá až do zahájení hry. V této fázi se hráči připojují, nastavují si jména, vybírají postavy a připravují se na start hry.

## Pořadí akcí

### Scénář 1: Vytvoření nové hry

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Server** pošle `welcome`, čímž potvrdí spojení
3. **Klient** pošle `infoHer`, tím si vyžádá informace o dostupných hrách
4. **Server** odpoví `infoHer:<json>` se seznamem dostupných her
5. **Klient** si vybere hru a pošle `novaHra:<typHry>`
6. **Server** vytvoří hru a odpoví:
   - `novaHra:<kodHry>` - kód nově vytvořené hry
   - `pripojenKeHre` - potvrzení připojení
   - `token:<token>` - token pro možnost znovupřipojení
   - `novyHrac:<json>` - informace o novém hráči
7. **Klient** může nastavit jméno pomocí `noveJmeno:<jmeno>`
8. **Server** rozešle všem `noveJmeno:<idHrace>,<jmeno>`
9. **Server** pošle `vyberPostavu:<json>` s nabídkou postav
10. **Klient** vybere postavu pomocí `setPostava:<postava>`
11. **Server** rozešle všem `setPostava:<idHrace>,<postava>`
12. Další hráči se mohou připojit (viz Scénář 2)
13. **Klient** (hostitel) může zahájit hru pomocí `zahajeniHry`
14. **Server** přejde do fáze hry a rozešle všem `hraZacala`

### Scénář 2: Připojení k existující hře

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Server** pošle `welcome`
3. **Klient** pošle `infoHer` (volitelné, pokud chce informace o serveru)
4. **Klient** pošle `pripojeniKeHre:<kodHry>` s kódem existující hry
5. **Server** ověří, že hra existuje a ještě nezačala, pak odpoví:
   - `pripojenKeHre` - potvrzení připojení
   - `token:<token>` - token pro znovupřipojení
   - `novyHrac:<json>` - informace o novém hráči (pouze tomuto klientovi)
6. **Server** rozešle **všem ostatním** hráčům `novyHrac:<json>` s informací o nově připojeném hráči
7. **Klient** může nastavit jméno pomocí `noveJmeno:<jmeno>`
8. **Server** rozešle všem `noveJmeno:<idHrace>,<jmeno>`
9. **Klient** může požádat o aktuální stav pomocí `nactiHru`
10. **Server** pošle aktuální stav hry (seznam hráčů, apod.)
11. **Server** pošle `vyberPostavu:<json>` s nabídkou postav
12. **Klient** vybere postavu pomocí `setPostava:<postava>`
13. **Server** rozešle všem `setPostava:<idHrace>,<postava>`

### Scénář 3: Znovupřipojení k hře

1. **Klient** se připojí k serveru pomocí WebSocket
2. **Server** pošle `welcome`
3. **Klient** načte uložený token z localStorage a pošle `vraceniSe:<token>`
4. **Server** ověří token a odpoví:
   - `pripojenKeHre` - potvrzení připojení
   - `hraZacala` - pokud již hra běží
   - Aktuální stav hry (automaticky zavolá nactiHru)

## Přehled zpráv

### Zprávy od klienta k serveru

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
- `{"error":"už jsi připojen ke hře"}` - pokud už jste ve hře

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
- `{"error":"už jsi připojen ke hře"}` - už jste ve hře
- `{"error":"Hra neexistuje"}` - neplatný kód
- `{"error":"tato hra už byla zahájena. Bohužel se už nejde připojit."}` - hra již začala

---

#### `vraceniSe:<token>`
**Účel:** Znovupřipojení k hře po výpadku spojení

**Payload:** token ve formátu `<kodHry><identifikator>` (celkem 22 znaků: 6 číslic + 16 znaků)

**Kdy poslat:** Po obnovení spojení, pokud máte uložený token

**Příklad:**
```
vraceniSe:123456abcdef1234567890
```

**Možné chyby:**
- `{"error":"hráč v této hře nenalezen"}` - neplatný token nebo hra již neexistuje

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

#### `novyHrac:<json>`
**Účel:** Informace o novém hráči ve hře

**Payload:** JSON objekt s informacemi o hráči

**Struktura:**
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

**Kdy čekat:** 
- Po vlastním připojení k hře
- Když se připojí nový hráč (rozesláno všem)

**Co dělat:** Přidat hráče do seznamu hráčů v lobby

**Příklad:**
```
novyHrac:{"jmeno":"Hráč 1","zivoty":4,"maximumZivotu":4,"role":"?","id":0}
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
**Účel:** Seznam všech hráčů ve hře

**Payload:** JSON pole objektů hráčů

**Struktura:**
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

**Kdy čekat:** Po odeslání `nactiHru` nebo po znovupřipojení

**Co dělat:** Nahradit aktuální seznam hráčů tímto seznamem

**Příklad:**
```
hraci:[{"jmeno":"Hráč 1","zivoty":4,"maximumZivotu":4,"role":"šerif","id":0,"pocetKaret":5,"postava":"BART_CASSIDY"}]
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

**Payload:** JSON objekt s informací o chybě

**Struktura:**
```json
{
  "error": "popis chyby",
  "kod": 100,
  "skupina": "PROTOKOL"
}
```

**Kdy čekat:** Kdykoliv, když dojde k chybě

**Co dělat:** Zobrazit chybovou zprávu uživateli

**Příklady:**
```
error:{"error":"Už jsi připojen ke hře.","kod":15,"skupina":2}
error:{"error":"Hra, ke které se snažíš připojit neexistuje.","kod":5,"skupina":1}
error:{"error":"Nejsi připojen ke hře.","kod":1,"skupina":2}
```

---

## Stavový diagram

```
[Připojení] 
    ↓ (WebSocket připojeno)
[Uvítání] - welcome
    ↓ (infoHer)
[Výběr hry] - infoHer:<json>
    ↓ 
    ├─→ [Vytvoření hry] - novaHra:<typHry>
    │       ↓
    │   [Nová hra vytvořena] - novaHra:<kodHry>, pripojenKeHre, token:<token>
    │       ↓
    │   [Lobby]
    │
    └─→ [Připojení k hře] - pripojeniKeHre:<kodHry>
            ↓
        [Připojen] - pripojenKeHre, token:<token>
            ↓
        [Lobby]
            ↓ (vyberPostavu)
        [Výběr postavy] - setPostava:<postava>
            ↓
        [Čekání na ostatní]
            ↓ (zahajeniHry)
        [Zahájení hry] - hraZacala
            ↓
        [Fáze hra]
```

## Poznámky

1. **Token:** Vždy si uložte token poskytnutý serverem. Umožňuje znovupřipojení po výpadku.

2. **Pořadí zpráv:** Některé zprávy jsou závislé na předchozích. Například musíte být připojeni k hře (`pripojenKeHre`) před tím, než můžete změnit jméno.

3. **Broadcast zprávy:** Zprávy jako `novyHrac`, `noveJmeno`, `setPostava` jsou rozeslány všem hráčům ve hře, včetně odesílatele.

4. **Kód hry:** Je 6-místné číslo generované serverem v rozsahu 100000-999999.

5. **Automatické mazání:** Pokud se všichni hráči odpojí, hra se automaticky smaže po 5 minutách (300 000 ms).

6. **Limit hráčů:** Maximální počet hráčů závisí na pravidlech konkrétní hry.
