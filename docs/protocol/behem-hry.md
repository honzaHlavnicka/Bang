# Fáze během hry

Tato fáze začíná po zahájení hry (zpráva `hraZacala`) a trvá až do konce hry (zpráva `vyhral`). V této fázi probíhá samotná hra s tahy hráčů, hraním karet, líznutím karet a dalšími herními akcemi.

## Zahájení fáze

Po odeslání zprávy `zahajeniHry` serverem a následné odpovědi `hraZacala` server automaticky:

1. Přidělí všem hráčům role (šerif, bandita, zrádce)
2. Pošle každému hráči jeho roli: `role:<role>`
3. Pošle každému hráči jeho ID: `noveIdHrace:<id>`
4. Rozdá počáteční karty: `novaKarta:<json>` (opakovaně pro každou kartu)
5. Aktualizuje počty životů: `pocetZivotu:<idHrace>,<pocet>`
6. Začne první tah: `tahZacal:<idHrace>` nebo `tvujTahZacal`

## Struktura tahu

Typický tah hráče probíhá takto:

1. **Začátek tahu** - server oznámí začátek tahu
2. **Líznutí karet** - hráč líže karty (obvykle 2)
3. **Hraní karet** - hráč může hrát karty z ruky
4. **Konec tahu** - hráč ukončí svůj tah
5. **Příští hráč** - server oznámí začátek dalšího tahu

## Přehled zpráv

### Zprávy od klienta k serveru

#### `linuti`
**Účel:** Líznutí karty z balíčku

**Payload:** žádný

**Kdy poslat:** Během svého tahu, když chcete líznout kartu

**Omezení:** 
- Musíte být na tahu
- Herní pravidla musí dovolovat líznutí (obvykle max 2x na začátku tahu)

**Příklad:**
```
linuti
```

**Odpověď serveru:** 
- `novaKarta:<json>` - nová karta přidána do ruky
- `error:<json>` - pokud nemůžete líznout (např. už jste lízli 2x)

---

#### `odehrani:<idKarty>`
**Účel:** Odehrání karty z ruky

**Payload:** ID karty (číslo)

**Kdy poslat:** Během svého tahu, když chcete odehrát kartu

**Omezení:**
- Musíte mít kartu v ruce
- Musíte být na tahu (nebo karta musí být reakcí)
- Herní pravidla musí dovolovat odehrání karty

**Příklad:**
```
odehrani:42
```

**Odpověď serveru:**
- `odehrat:<idHrace>|<json>` - rozesláno všem, informace o odehrané kartě
- `vyberAkci:<json>` nebo `vyberHrace:<json>` - pokud karta vyžaduje výběr
- `error:<json>` - pokud kartu nelze odehrát

---

#### `vylozeni:<idKarty>[,<idHrace>]`
**Účel:** Vyložení karty před sebe (nebo před jiného hráče)

**Payload:** 
- ID karty (číslo)
- Volitelně: ID hráče před kterého se karta vyloží (odděleno čárkou)

**Kdy poslat:** Během svého tahu, když chcete vyložit trvalou kartu (např. zbraň, barel)

**Omezení:**
- Musíte mít kartu v ruce
- Musíte být na tahu
- Karta musí být vyložitelná

**Příklad:**
```
vylozeni:15
vylozeni:15,2
```

**Odpověď serveru:**
- `vylozit:<idHracePredKoho>,<idHraceKym>,<json>` - rozesláno všem
- `error:<json>` - pokud kartu nelze vyložit

---

#### `konecTahu`
**Účel:** Ukončení svého tahu

**Payload:** žádný

**Kdy poslat:** Na konci svého tahu, když už nechcete provádět další akce

**Omezení:**
- Musíte být na tahu
- Herní pravidla musí dovolovat ukončení (např. nemáte moc karet v ruce)

**Příklad:**
```
konecTahu
```

**Odpověď serveru:**
- `tahZacal:<idHrace>` - rozesláno všem, začíná tah dalšího hráče
- `error:<json>` - pokud nemůžete ukončit tah (např. máte moc karet)

---

#### `dialog:<id>,<data>`
**Účel:** Odpověď na dialog (výběr akce, hráče, karty, apod.)

**Payload:** ID dialogu a data oddělené čárkou

**Kdy poslat:** Jako odpověď na `vyberAkci` nebo `vyberHrace`

**Formát dat:**
- Pro `vyberAkci`: ID vybrané akce (číslo)
- Pro `vyberHrace`: ID vybraného hráče (číslo)

**Příklad:**
```
dialog:1,0
dialog:2,3
```

**Odpověď serveru:** Závisí na kontextu dialogu, obvykle aktualizace stavu hry

---

#### `chat:<zprava>`
**Účel:** Odeslání chat zprávy

**Payload:** textová zpráva

**Kdy poslat:** Kdykoliv během hry

**Příklad:**
```
chat:Ahoj všichni!
chat:Dobře zahráno!
```

**Odpověď serveru:**
- `chat:<zprava> [od: <jmenoHrace>]` - rozesláno všem hráčům

---

### Zprávy od serveru ke klientovi

#### `hraZacala` / `hraSpustena`
**Účel:** Oznámení, že hra byla zahájena

**Payload:** žádný

**Kdy čekat:** Po odeslání `zahajeniHry` nebo po znovupřipojení k již běžící hře

**Co dělat:** Přejít z lobby do herní obrazovky

**Příklad:**
```
hraZacala
```

---

#### `role:<role>`
**Účel:** Přidělení role hráči

**Payload:** identifikátor role (String)

**Možné role:**
- `šerif` - šerif
- `bandita` - bandita
- `zrádce` - zrádce
- `?` - neznámá role (pro ostatní hráče)

**Kdy čekat:** Na začátku hry, po `hraZacala`

**Co dělat:** Uložit a zobrazit roli hráče

**Příklad:**
```
role:šerif
role:bandita
```

---

#### `noveIdHrace:<id>`
**Účel:** Přidělení ID hráči

**Payload:** číslo

**Kdy čekat:** Na začátku hry, po `hraZacala`

**Co dělat:** Uložit ID hráče pro pozdější identifikaci v herních zprávách

**Příklad:**
```
noveIdHrace:0
```

---

#### `novaKarta:<json>`
**Účel:** Přidání nové karty do ruky hráče

**Payload:** JSON objekt s informacemi o kartě

**Struktura:**
```json
{
  "obrazek": "cesta/k/obrazku.png",
  "id": 42
}
```

**Kdy čekat:**
- Na začátku hry (počáteční karty)
- Po odeslání `linuti`
- Po speciálních herních akcích (např. efekt karty)

**Co dělat:** Přidat kartu do ruky hráče a zobrazit ji

**Příklad:**
```
novaKarta:{"obrazek":"bang.png","id":42}
novaKarta:{"obrazek":"pivo.png","id":15}
```

---

#### `zmenaPoctuKaret:<idHrace>,<pocet>` / `novyPocetKaret:<idHrace>,<pocet>`
**Účel:** Aktualizace počtu karet v ruce jiného hráče

**Payload:** ID hráče a nový počet karet oddělené čárkou

**Kdy čekat:** Kdykoliv během hry, když jiný hráč získá nebo ztratí karty

**Co dělat:** Aktualizovat zobrazený počet karet u daného hráče

**Příklad:**
```
zmenaPoctuKaret:1,6
novyPocetKaret:2,3
```

---

#### `pocetZivotu:<idHrace>,<pocet>`
**Účel:** Aktualizace počtu životů hráče

**Payload:** ID hráče a nový počet životů oddělené čárkou

**Kdy čekat:**
- Na začátku hry (počáteční životy)
- Po zranění nebo léčení hráče

**Co dělat:** 
- Aktualizovat zobrazený počet životů
- Zobrazit notifikaci, pokud se jedná o aktuálního hráče

**Příklad:**
```
pocetZivotu:0,3
pocetZivotu:1,0
```

**Poznámka:** Pokud počet životů klesne na 0, hráč je vyřazen ze hry.

---

#### `tvujTahZacal`
**Účel:** Oznámení, že začal tah aktuálního hráče

**Payload:** žádný

**Kdy čekat:** Když přijde na řadu aktuální hráč

**Co dělat:** 
- Aktivovat herní prvky (tlačítka, karty)
- Zobrazit notifikaci "Tvůj tah!"
- Nastavit turnPlayerId na ID aktuálního hráče

**Příklad:**
```
tvujTahZacal
```

---

#### `tahZacal:<idHrace>`
**Účel:** Oznámení, že začal tah jiného hráče

**Payload:** ID hráče, který je na tahu

**Kdy čekat:** Když přijde na řadu jiný hráč

**Co dělat:**
- Deaktivovat herní prvky (pokud byly aktivní)
- Zvýraznit hráče, který je na tahu
- Nastavit turnPlayerId

**Příklad:**
```
tahZacal:1
tahZacal:2
```

---

#### `odehrat:<idHrace>|<json>`
**Účel:** Informace o odehrané kartě

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`

**Struktura:**
```
<idHrace>|{"obrazek":"bang.png","id":42}
```

**Kdy čekat:** Po odeslání `odehrani:<idKarty>` nebo když jiný hráč odehraje kartu

**Co dělat:**
- Přesunout kartu z ruky na odkladový balíček
- Přidat kartu do viditelného odkladového balíčku
- Zobrazit animaci odehrání karty

**Příklad:**
```
odehrat:0|{"obrazek":"bang.png","id":42}
odehrat:1|{"obrazek":"pivo.png","id":15}
```

---

#### `vylozit:<idHracePredKoho>,<idHraceKym>,<json>`
**Účel:** Informace o vyložené kartě

**Payload:** 
- ID hráče před koho byla karta vyložena
- ID hráče kým byla karta vyložena
- JSON s informacemi o kartě
(vše odděleno čárkami)

**Struktura:**
```
<idHracePredKoho>,<idHraceKym>,{"obrazek":"barel.png","id":15}
```

**Kdy čekat:** Po odeslání `vylozeni:<idKarty>` nebo když jiný hráč vyloží kartu

**Co dělat:**
- Přidat kartu do vyložených karet hráče
- Zobrazit kartu před hráčem

**Příklad:**
```
vylozit:0,0,{"obrazek":"barel.png","id":15}
vylozit:1,0,{"obrazek":"remington.png","id":20}
```

**Poznámka:** První dva parametry jsou obvykle stejné (hráč vyloží kartu před sebe), ale mohou být různé u speciálních karet.

---

#### `vyberAkci:<json>`
**Účel:** Dialog pro výběr akce

**Payload:** JSON objekt s ID dialogu a seznamem akcí

**Struktura:**
```json
{
  "id": 1,
  "akce": [
    {"id": 0, "nazev": "Ano"},
    {"id": 1, "nazev": "Ne"}
  ]
}
```

**Kdy čekat:** Když je potřeba, aby hráč vybral z více možností (např. použít barel, reagovat na útok)

**Co dělat:** 
- Zobrazit dialog s možnostmi
- Po výběru poslat `dialog:<id>,<idAkce>`

**Příklad:**
```
vyberAkci:{"id":1,"akce":[{"id":0,"nazev":"Použít barel"},{"id":1,"nazev":"Nepoužít"}]}
vyberAkci:{"id":2,"akce":[{"id":0,"nazev":"Ano"},{"id":1,"nazev":"Ne"}]}
```

---

#### `vyberHrace:<json>`
**Účel:** Dialog pro výběr hráče

**Payload:** JSON objekt s ID dialogu, seznamem ID hráčů a volitelným nadpisem

**Struktura:**
```json
{
  "id": 2,
  "hraci": [0, 1, 2],
  "nadpis": "Vyber hráče na kterého chceš střílet"
}
```

**Kdy čekat:** Když je potřeba, aby hráč vybral cílového hráče (např. pro útok, krádež karty)

**Co dělat:**
- Zobrazit dialog se seznamem hráčů
- Zvýraznit vybratelné hráče
- Po výběru poslat `dialog:<id>,<idHrace>`

**Příklad:**
```
vyberHrace:{"id":2,"hraci":[1,2,3],"nadpis":"Vyber hráče na kterého chceš střílet"}
vyberHrace:{"id":3,"hraci":[0,2],"nadpis":"Vyber hráče"}
```

---

#### `vyhral:<idHrace>`
**Účel:** Konec hry - oznámení výherce

**Payload:** ID hráče, který vyhrál

**Kdy čekat:** Když jsou splněny výherní podmínky (např. všichni bandité jsou mrtví)

**Co dělat:**
- Zobrazit obrazovku s gratulací výherci
- Ukázat finální stav hry
- Nabídnout možnost hrát znovu nebo odejít

**Příklad:**
```
vyhral:0
vyhral:2
```

---

#### `chat:<zprava> [od: <jmeno>]`
**Účel:** Chat zpráva od hráče

**Payload:** zpráva a jméno odesílatele

**Kdy čekat:** Kdykoliv během hry, když někdo pošle chat zprávu

**Co dělat:** Zobrazit zprávu v chat okně

**Příklad:**
```
chat:Ahoj všichni! [od: Honza]
chat:Dobře zahráno! [od: Petr]
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

**Kdy čekat:** Kdykoliv, když dojde k chybě (např. neplatný tah, nelze líznout kartu)

**Co dělat:** Zobrazit chybovou zprávu uživateli

**Příklady chyb během hry:**
```
error:{"error":"Nejsi na tahu"}
error:{"error":"Už jsi líznul maximální počet karet"}
error:{"error":"Tuto kartu nemůžeš odehrát"}
error:{"error":"Musíš zahodit karty"}
```

---

## Typické herní scénáře

### Scénář 1: Základní tah hráče

```
1. Server → Všem: tahZacal:0 (nebo tvujTahZacal pro hráče 0)
2. Klient → Server: linuti
3. Server → Klient: novaKarta:{"obrazek":"bang.png","id":42}
4. Klient → Server: linuti
5. Server → Klient: novaKarta:{"obrazek":"pivo.png","id":43}
6. Klient → Server: odehrani:42
7. Server → Všem: odehrat:0|{"obrazek":"bang.png","id":42}
8. Server → Cílový hráč: vyberHrace:{"id":1,"hraci":[1,2],"nadpis":"Vyber cíl"}
9. Klient → Server: dialog:1,1
10. Server → Hráč 1: vyberAkci:{"id":2,"akce":[{"id":0,"nazev":"Minul"},{"id":1,"nazev":"Použít barel"}]}
11. Klient (hráč 1) → Server: dialog:2,1
12. Server → Všem: aktualizace stavu (např. žádná změna, pokud barel fungoval)
13. Klient → Server: konecTahu
14. Server → Všem: tahZacal:1
```

### Scénář 2: Vyložení karty

```
1. Server → Klient: tvujTahZacal
2. Klient → Server: vylozeni:15
3. Server → Všem: vylozit:0,0,{"obrazek":"barel.png","id":15}
4. Server → Klient: zmenaPoctuKaret:0,4
5. Klient → Server: konecTahu
6. Server → Všem: tahZacal:1
```

### Scénář 3: Konec hry

```
1. Klient → Server: odehrani:50 (Bang! na posledního banditu)
2. Server → Všem: odehrat:0|{"obrazek":"bang.png","id":50}
3. Server → Cílový hráč: vyberAkci:...
4. Klient → Server: dialog:...
5. Server → Všem: pocetZivotu:2,0 (bandita zemřel)
6. Server → Všem: vyhral:0 (šerif vyhrál)
```

### Scénář 4: Použití piva

```
1. Server → Klient: tvujTahZacal
2. Klient → Server: odehrani:43 (pivo)
3. Server → Všem: odehrat:0|{"obrazek":"pivo.png","id":43}
4. Server → Všem: pocetZivotu:0,4 (hráč 0 si vyléčil život)
5. Klient → Server: konecTahu
```

## Poznámky k implementaci

### Synchronizace stavu

Klient by měl udržovat kompletní stav hry:
- Seznam všech hráčů a jejich vlastnosti (životy, karty v ruce, vyložené karty)
- Vlastní karty v ruce
- Aktuální hráč na tahu
- Odkladový balíček (viditelné odehrané karty)

### Validace na straně klienta

I když server vždy validuje všechny akce, klient by měl provádět základní validaci pro lepší UX:
- Zablokovat tlačítka, když nejste na tahu
- Zobrazit pouze odehratelné karty
- Zablokovat konec tahu, pokud nemůžete ukončit (např. máte moc karet)

### Časování zpráv

Některé zprávy přicházejí v rychlém sledu (např. při začátku hry). Klient by měl:
- Zpracovávat zprávy v pořadí, v jakém přicházejí
- Používat animace, aby bylo jasné, co se děje
- Nedělat uživatelské rozhraní neresponsivní

### Odpojení během hry

Pokud se hráč odpojí během hry:
1. Uložte token pro znovupřipojení
2. Po obnovení spojení pošlete `vraceniSe:<token>`
3. Server pošle aktuální stav hry
4. Aktualizujte UI na základě stavu

### Chybové stavy

Vždy zpracovávejte chybové zprávy:
- Zobrazujte chyby uživateli srozumitelně
- Při kritických chybách (např. "Nejsi připojen ke hře") vraťte se na hlavní obrazovku
- Při drobných chybách (např. "Nejsi na tahu") pouze informujte uživatele
