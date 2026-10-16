# Fáze během hry

Tato fáze začíná po zahájení hry (zpráva `hraZacala`). I po zprávě `vyhral` může fáze pokračovat, protože hráči mohou pozorovat dohrávanou hru. V této fázi probíhá samotná hra s tahy hráčů, hraním karet, líznutím karet a dalšími herními akcemi.

## Zahájení fáze

Po odeslání zprávy `zahajeniHry` serverem a následné odpovědi `hraZacala` server automaticky:

1. Přidělí všem hráčům role (šerif, bandita, zrádce)
2. Pošle každému hráči jeho roli: `role:<role>`
3. Pošle každému hráči jeho ID: `noveIdHrace:<id>`
4. Rozdá počáteční karty: `novaKarta:<json>` (opakovaně pro každou kartu)
5. Aktualizuje počty životů: `pocetZivotu:<idHrace>,<pocet>`
6. Začne první tah: `tahZacal:<idHrace>` nebo `tvujTahZacal`

## Struktura tahu

Typický tah hráče ve hře Bang probíhá takto (struktura se může lišit hra od hry):

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
- `vylozeni:<idHraceKym>,<idHracePredKoho>,<json>` - rozesláno všem
- `error:<json>` - pokud kartu nelze vyložit

---

#### `spaleni:<idKarty>`
**Účel:** Spálení karty z ruky

**Payload:** ID karty (číslo)

**Kdy poslat:** Během svého tahu, když chcete spálit kartu (vyhodit ji na odkladací balíček)

**Omezení:**
- Musíte mít kartu v ruce
- Musíte být na tahu
- Karta musí být spalitelná

**Příklad:**
```
spaleni:42
```

**Odpověď serveru:**
- `spalit:<idHrace>|<json>` - rozesláno všem, informace o spálené kartě
- `error:<json>` - pokud kartu nelze spálit

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
**Účel:** Odpověď na dialog (výběr akce, hráče, karty nebo zadání textu)

**Payload:** ID dialogu a data oddělené čárkou

**Kdy poslat:** Jako odpověď na `vyberAkci`, `vyberHrace`, `vyberKartu` nebo `vyberText`

**Formát dat:**
- Pro `vyberAkci`: ID vybrané akce (číslo)
- Pro `vyberHrace`: ID vybraného hráče nebo čárkami oddělená ID vybraných hráčů (např. `2` nebo `1,2`)
- Pro `vyberKartu`: ID vybrané karty nebo čárkami oddělená ID karet (např. `42` nebo `42,15`)
- Pro `vyberText`: zadaný textový řetězec

**Příklad:**
```
dialog:1,0
dialog:2,3
dialog:3,42,15
dialog:4,Nějaký text
```

**Odpověď serveru:** Závisí na kontextu dialogu, obvykle aktualizace stavu hry

---

#### `uiClick:<id>`
**Účel:** Kliknutí na vlastní UI tlačítko přidané pluginem hry (zaslané dříve přes `noveUI`)

**Payload:** číselné ID tlačítka

**Kdy poslat:** Když hráč klikne na vlastní akční tlačítko v rozhraní

**Příklad:**
```
uiClick:1
```

**Odpověď serveru:** Vyvolání obslužné akce pluginu a případná aktualizace stavu nebo dialog.

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

#### `hraZacala`
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

**Možné role (pro hru Bang, mohou se měnit v budoucích verzích):**
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
  "id": 42,
  "jmeno": "bang",
  "obrazek": "bang.png",
  "vylozitelna": false,
  "hratelna": true
}
```

**Kdy čekat:**
- Na začátku hry (počáteční karty)
- Po odeslání `linuti`
- Po speciálních herních akcích (např. efekt karty)

**Co dělat:** Přidat kartu do ruky hráče a zobrazit ji

**Příklad:**
```
novaKarta:{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
novaKarta:{"id":15,"jmeno":"pivo","obrazek":"pivo.png","vylozitelna":false,"hratelna":true}
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
**Účel:** Informace o odehrané kartě z ruky na odhazovací balíček

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`. Speciální případ: `idHrace === "-1"` indikuje otočení vrchní karty balíčku bez vazby na konkrétního hráče.

**Struktura:**
```
<idHrace>|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

**Kdy čekat:** Po odeslání `odehrani:<idKarty>` nebo když jiný hráč odehraje kartu

**Co dělat:**
- Přesunout kartu z ruky na odkladový balíček
- Přidat kartu do viditelného odkladového balíčku
- Zobrazit animaci odehrání karty

**Příklad:**
```
odehrat:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
odehrat:-1|{"id":15,"jmeno":"srdcove_eso","obrazek":"eso.png","vylozitelna":false,"hratelna":true}
```

---

#### `vylozeni:<idHraceKym>,<idHracePredKoho>,<json>`
**Účel:** Informace o vyložené kartě

**Payload:** 
- ID hráče který kartu vyložil (kym)
- ID hráče před koho byla karta vyložena (predKoho)
- JSON s informacemi o kartě
(vše odděleno čárkami)

**Struktura:**
```
<idHraceKym>,<idHracePredKoho>,{"id":15,"jmeno":"barel","obrazek":"barel.png","vylozitelna":true,"hratelna":false}
```

**Kdy čekat:** Po odeslání `vylozeni:<idKarty>` nebo když jiný hráč vyloží kartu

**Co dělat:**
- Přidat kartu do vyložených karet hráče (`idHracePredKoho`)
- Zobrazit kartu před hráčem na stole

**Příklad:**
```
vylozeni:0,0,{"id":15,"jmeno":"barel","obrazek":"barel.png","vylozitelna":true,"hratelna":false}
vylozeni:0,1,{"id":20,"jmeno":"vezeni","obrazek":"vezeni.png","vylozitelna":true,"hratelna":false}
```

**Poznámka:** První dva parametry jsou obvykle stejné (hráč vyloží kartu před sebe), ale mohou být různé u speciálních karet (např. Vězení, Dynamit).

---

#### `vyberAkci:<json>`
**Účel:** Dialog pro výběr akce

**Payload:** JSON objekt s ID dialogu, seznamem akcí, nadpisem a příznakem zavíratelnosti

**Struktura:**
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

**Kdy čekat:** Když je potřeba, aby hráč vybral z více možností (např. použít barel, reagovat na útok)

**Co dělat:** 
- Zobrazit dialog s možnostmi (podporuje překlad `$key`)
- Po výběru poslat `dialog:<id>,<idAkce>`

**Příklad:**
```
vyberAkci:{"id":1,"akce":[{"id":0,"nazev":"Použít barel"},{"id":1,"nazev":"Nepoužít"}],"nadpis":"Použít barel?","notClosable":true}
```

---

#### `vyberHrace:<json>`
**Účel:** Dialog pro výběr jednoho nebo více hráčů

**Payload:** JSON objekt s ID dialogu, seznamem ID hráčů, min/max limity a volitelným nadpisem

**Struktura:**
```json
{
  "id": 2,
  "hraci": [1, 2, 3],
  "nadpis": "Vyber hráče na kterého chceš střílet",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

**Kdy čekat:** Když je potřeba, aby hráč vybral cílového hráče (např. pro útok, krádež karty)

**Co dělat:**
- Zobrazit dialog se seznamem hráčů
- Zvýraznit vybratelné hráče
- Po výběru poslat `dialog:<id>,<idHrace>` (nebo čárkami oddělená ID hráčů)

**Příklad:**
```
vyberHrace:{"id":2,"hraci":[1,2,3],"nadpis":"Vyber hráče na kterého chceš střílet","min":1,"max":1,"notClosable":true}
```

---

#### `spalit:<idHrace>|<json>`
**Účel:** Informace o spálené kartě (zahozené na odkladací balíček)

**Payload:** ID hráče a JSON s informacemi o kartě oddělené znakem `|`

**Struktura:**
```
<idHrace>|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

**Kdy čekat:** Po odeslání `spaleni:<idKarty>` nebo když jiný hráč spálí kartu

**Co dělat:**
- Přesunout kartu z ruky hráče na odkladací balíček
- Zobrazit animaci spálení karty

**Příklad:**
```
spalit:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
```

---

#### `spalenaVylozena:<idKarty>,<idHrace>`
**Účel:** Informace o spálení vyložené (trvalé) karty z pole hráče

**Payload:** ID karty a ID hráče oddělené čárkou

**Kdy čekat:** Když je spálena trvalá karta z pole před hráčem (např. Cat Balou)

**Co dělat:**
- Odebrat kartu z vyložených karet daného hráče
- Přidat ji na odkladací balíček

**Příklad:**
```
spalenaVylozena:15,0
```

---

#### `hracSkoncil:<idHrace>`
**Účel:** Oznámení, že hráč byl vyřazen ze hry

**Payload:** ID vyřazeného hráče

**Kdy čekat:** Když počet životů hráče klesne na 0

**Co dělat:**
- Označit hráče jako vyřazeného
- Zobrazit notifikaci o vyřazení

**Příklad:**
```
hracSkoncil:2
```

---

#### `povoleneUI:<json>`
**Účel:** Informace o povolených prvcích uživatelského rozhraní

**Payload:** JSON pole názvů povolených UI prvků

**Kdy čekat:** Na začátku hry a po každé změně stavu hry, která ovlivňuje dostupné akce

**Co dělat:** Zobrazit/skrýt příslušné herní prvky (dobírací balíček, odhazovací balíček atd.)

**Příklad:**
```
povoleneUI:["ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"]
```

---

#### `noveUI:<json>`
**Účel:** Přidání nebo aktualizace vlastního akčního tlačítka definovaného herním pluginem

**Payload:** JSON objekt s ID tlačítka, textem a informací o stavu disabled

**Struktura JSON:**
```json
{
  "id": 1,
  "text": "Vzít dynamit",
  "disabled": false
}
```

**Kdy čekat:** Kdykoliv plugin hráči zpřístupní speciální akci

**Co dělat:** Vykreslit tlačítko v sekci vlastního UI; při kliknutí odeslat `uiClick:<id>`

**Příklad:**
```
noveUI:{"id":1,"text":"Vzít dynamit","disabled":false}
```

---

#### `odebratUI:<id>`
**Účel:** Odstranění vlastního akčního tlačítka

**Payload:** číselné ID tlačítka

**Kdy čekat:** Když plugin zruší dostupnost speciální akce

**Co dělat:** Odstranit tlačítko z rozhraní

**Příklad:**
```
odebratUI:1
```

---

#### `rychleOznameni:<text>`
**Účel:** Rychlé textové oznámení hráči (toast)

**Payload:** text oznámení (nebo lokalizační klíč `$key`)

**Kdy čekat:** Kdykoliv během hry pro informativní zprávy

**Co dělat:** Zobrazit krátké oznámení (toast nebo notifikaci)

**Příklad:**
```
rychleOznameni:Barva změněna na červenou
```

---

#### `stavHry:<text>`
**Účel:** Stavové oznámení zobrazené v centru herní obrazovky

**Payload:** text zprávy nebo lokalizační formát `$key:{"param":"hodnota"}`

**Kdy čekat:** Při významných událostech (začátek duelu, indická invaze apod.)

**Co dělat:** Zobrazit oznámení uprostřed obrazovky

**Příklad:**
```
stavHry:Probíhá duel!
stavHry:$stav.duel:{"hrac1":"Honza","hrac2":"Petr"}
```

---

#### `obrazekDobiracihoBalicku:<nazev>`
**Účel:** Nastavení grafiky vrchní karty nebo rubu balíčku

**Payload:** název nebo cesta obrázku

**Kdy čekat:** Při změně grafiky balíčku pluginem

**Příklad:**
```
obrazekDobiracihoBalicku:karta_zadni_modra
```

---

#### `koloStesti:<json>`
**Účel:** Zobrazení animovaného kola štěstí řízeného serverem

**Payload:** JSON se seznamem výsečí, vybraným indexem a nadpisem

**Struktura JSON:**
```json
{
  "moznosti": [
    {"id": 0, "name": "Zásah", "barva": "#ff0000", "velikost": 1},
    {"id": 1, "name": "Minul", "barva": "#00ff00", "velikost": 2}
  ],
  "vybranaMoznost": 1,
  "nadpis": "Barel"
}
```

**Kdy čekat:** Při vyhodnocení náhodných efektů (server plně řídí výsledek, klient animuje)

**Co dělat:** Zobrazit dialog s kolem štěstí

**Příklad:**
```
koloStesti:{"moznosti":[{"id":0,"name":"Zásah","barva":"#ff0000","velikost":1},{"id":1,"name":"Minul","barva":"#00ff00","velikost":2}],"vybranaMoznost":1,"nadpis":"Barel"}
```

---

#### `vyberKartu:<json>`
**Účel:** Dialog pro výběr jedné nebo více karet

**Payload:** JSON objekt s ID dialogu, seznamem karet, limity a nadpisem

**Struktura:**
```json
{
  "id": 3,
  "karty": [
    {"id": 42, "jmeno": "bang", "obrazek": "bang.png"},
    {"id": 43, "jmeno": "pivo", "obrazek": "pivo.png"}
  ],
  "nadpis": "Vyber kartu k odebrání",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

**Kdy čekat:** Když pravidla vyžadují výběr karty (např. Cat Balou, Panika)

**Co dělat:**
- Zobrazit dialog s kartami
- Po výběru poslat `dialog:<id>,<idKarty>` (nebo čárkami oddělená ID)

**Příklad:**
```
vyberKartu:{"id":3,"karty":[{"id":42,"jmeno":"bang","obrazek":"bang.png"}],"nadpis":"Vyber kartu k odebrání","min":1,"max":1,"notClosable":true}
```

---

#### `vyberText:<json>`
**Účel:** Dialog pro zadání volného textu hráčem

**Payload:** JSON objekt s ID dialogu, titulkem, placeholderem a textem tlačítka

**Struktura:**
```json
{
  "id": 4,
  "title": "Hádej slovo",
  "placeholder": "Zadej svůj tip...",
  "buttonText": "Odeslat",
  "notClosable": false
}
```

**Kdy čekat:** Když herní plugin vyžaduje textový vstup hráče

**Co dělat:**
- Zobrazit dialog s textovým polem
- Po odeslání poslat `dialog:<id>,<text>`

**Příklad:**
```
vyberText:{"id":4,"title":"Zadej slovo","placeholder":"Tip","buttonText":"Potvrdit","notClosable":false}
```

---

#### `vysledkyHry:<json>`
**Účel:** Oznámení konečného pořadí hráčů

**Payload:** JSON 2D pole čísel (skupiny ID hráčů podle umístění)

**Kdy čekat:** Při vyhodnocení konce hry před `konecHry`

**Příklad:**
```
vysledkyHry:[[0],[1,2],[3]]
```

---

#### `konecHry`
**Účel:** Oznámení o ukončení herní partie

**Payload:** žádný

**Kdy čekat:** Po dohrání hry

**Co dělat:** Zobrazit konečné skóre a uvolnit překlady pluginu

**Příklad:**
```
konecHry
```

---

#### `vyhral:<idHrace>`
**Účel:** Oznámení výherce hry

**Payload:** ID hráče, který vyhrál

**Kdy čekat:** Když jsou splněny výherní podmínky

**Co dělat:** Zobrazit dialog s oznámením vítěze

**Příklad:**
```
vyhral:0
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

**Payload:** JSON objekt s informací o chybě, kódem a skupinou

**Struktura:**
```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```

**Kdy čekat:** Kdykoliv, když dojde k chybě (např. neplatný tah, nelze líznout kartu)

**Co dělat:** Zobrazit chybovou zprávu uživateli (s přeložením `$error` klíče)

**Příklady chyb během hry:**
```
error:{"error":"$error.nejsi_na_tahu","kod":7,"skupina":3}
error:{"error":"$error.nejde_si_liznout","kod":13,"skupina":3}
error:{"error":"$error.karta_neni_hratelna","kod":3,"skupina":1}
error:{"error":"$error.nemuzes_ukoncit_tah","kod":8,"skupina":3}
error:{"error":"$error.pripojen_v_jine_zalozce","kod":22,"skupina":2}
```

---

## Typické herní scénáře

### Scénář 1: Základní tah hráče

```
1. Server → Všem: tahZacal:0 (nebo tvujTahZacal pro hráče 0)
2. Klient → Server: linuti
3. Server → Klient: novaKarta:{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
4. Klient → Server: linuti
5. Server → Klient: novaKarta:{"id":43,"jmeno":"pivo","obrazek":"pivo.png","vylozitelna":false,"hratelna":true}
6. Klient → Server: odehrani:42
7. Server → Všem: odehrat:0|{"id":42,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
8. Server → Cílový hráč: vyberHrace:{"id":1,"hraci":[1,2],"nadpis":"Vyber cíl","min":1,"max":1,"notClosable":true}
9. Klient → Server: dialog:1,1
10. Server → Hráč 1: vyberAkci:{"id":2,"akce":[{"id":0,"nazev":"Minul"},{"id":1,"nazev":"Použít barel"}],"nadpis":"Obrana","notClosable":true}
11. Klient (hráč 1) → Server: dialog:2,1
12. Server → Všem: aktualizace stavu (např. žádná změna, pokud barel fungoval)
13. Klient → Server: konecTahu
14. Server → Všem: tahZacal:1
```

### Scénář 2: Vyložení karty

```
1. Server → Klient: tvujTahZacal
2. Klient → Server: vylozeni:15
3. Server → Všem: vylozeni:0,0,{"id":15,"jmeno":"barel","obrazek":"barel.png","vylozitelna":true,"hratelna":false}
4. Server → Všem: zmenaPoctuKaret:0,4
5. Klient → Server: konecTahu
6. Server → Všem: tahZacal:1
```

### Scénář 3: Konec hry

```
1. Klient → Server: odehrani:50 (Bang! na posledního banditu)
2. Server → Všem: odehrat:0|{"id":50,"jmeno":"bang","obrazek":"bang.png","vylozitelna":false,"hratelna":true}
3. Server → Cílový hráč: vyberAkci:...
4. Klient → Server: dialog:...
5. Server → Všem: pocetZivotu:2,0 (bandita zemřel)
6. Server → Všem: vysledkyHry:[[0],[1],[2]]
7. Server → Všem: vyhral:0 (šerif vyhrál)
8. Server → Všem: konecHry
```

### Scénář 4: Použití piva

```
1. Server → Klient: tvujTahZacal
2. Klient → Server: odehrani:43 (pivo)
3. Server → Všem: odehrat:0|{"id":43,"jmeno":"pivo","obrazek":"pivo.png","vylozitelna":false,"hratelna":true}
4. Server → Všem: pocetZivotu:0,4 (hráč 0 si vyléčil život)
5. Klient → Server: konecTahu
```

## Poznámky k implementaci

### Synchronizace stavu

Klient by měl udržovat kompletní stav hry:
- Seznam všech hráčů a jejich vlastnosti (životy, karty v ruce, vyložené karty, online stav, admin příznak)
- Vlastní karty v ruce (s příznaky `hratelna` a `vylozitelna`)
- Vlastní vyložené karty na stole
- Vlastní akční tlačítka ze zpráv `noveUI`
- Aktuální hráč na tahu (`tvujTahZacal` / `tahZacal`)
- Odkládací balíček (poslední odehraná nebo spálená karta)
- Dobírací balíček (`obrazekDobiracihoBalicku`)

### Dynamická lokalizace a řetězce $key

Server odesílá lokalizovatelné texty ve dvou formátech:
1. `$klic` – jednoduchý překladový klíč (např. `$error.karta_nejde_zahrat`)
2. `$klic:{"parametr":"hodnota", ...}` – překladový klíč s parametry pro interpolaci (např. `$stav.duel:{"hrac1":"Honza"}`)

Klient před zobrazením textu uživateli (v chybách, dialogových nadpisech, oznámeních i vlastních tlačítkách) tyto řetězce rekurzivně vyhledává v lokalizačním balíčku pluginu načteném přes `nactiPreklady:<jazyk>`.

### Vlastní UI prvky pluginu

Herní pluginy mohou dynamicky vytvářet vlastní tlačítka v klientském rozhraní:
- Pomocí `noveUI:<json>` server pošle tlačítko s jeho ID, textem a stavem `disabled`.
- Při kliknutí na tlačítko klient odešle `uiClick:<id>`.
- Pomocí `odebratUI:<id>` server tlačítko odstraní.
- Při reconnectu hráče server automaticky pošle všechna aktivní tlačítka znovu.

### Validace na straně klienta

I když server vždy validuje všechny akce, klient by měl provádět základní validaci pro lepší UX:
- Zablokovat tlačítka a interakce, když hráč není na tahu (pokud nejde o reakci na výzvu/dialog)
- Zvýraznit pouze hratelné karty (`hratelna: true`) a vyložitelné karty (`vylozitelna: true`)
- Zablokovat konec tahu, pokud pravidla nepovolují ukončení (např. nadbytek karet v ruce)

### Časování zpráv a dialogy

- Dialogy mají unikátní ID přidělené serverem. Klient musí poslat odpověď ve formátu `dialog:<id>,<odpoved>`.
- Pokud je u dialogu uvedeno `"notClosable": true`, dialog nelze zavřít bez výběru možnosti.
- Při znovupřipojení se stav hry znovu načte a čekající dialogy či tlačítka se znovu aktivují.

### Odpojení během hry a více záložek

Pokud se hráč odpojí během hry:
1. Uložený token v localStorage/sessionStorage zůstává platný po dobu 1 hodiny.
2. Po obnovení spojení pošle klient `vraceniSe:<token>`.
3. Pokud byla hra otevřena v jiné záložce, server původní spojení uzavře s chybou `PRIPOJEN_V_JINE_ZALOZCE` (kód 22), čímž zamezí nekonečné reconnect smyčce.
4. Server pošle nový stav hry a hráč plynule pokračuje v partii.
