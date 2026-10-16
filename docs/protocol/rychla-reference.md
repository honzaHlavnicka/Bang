# Rychlá reference protokolu

Stručný přehled všech zpráv v protokolu pro rychlou orientaci.

## Formát zpráv

Zprávy mají dva formáty:
- `typ:payload` - zpráva s daty
- `typ` - zpráva bez dat

## Zprávy Klient → Server

### Před hrou

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `posthogId:<id>` | text / `ignore` | Analytické ID klienta |
| `infoHer` | - | Získání informací o serveru a hrách |
| `novaHra:<typHry>` | číslo | Vytvoření nové hry |
| `pripojeniKeHre:<kodHry>` | 6-místný kód | Připojení k existující hře |
| `overeniTokenu:<token>` | token | Ověření platnosti tokenu před znovupřipojením |
| `vraceniSe:<token>` | token | Znovupřipojení k hře |
| `noveJmeno:<jmeno>` | text | Nastavení/změna jména |
| `setPostava:<postava>` | text | Výběr postavy |
| `pridejBota` / `pridejBota:<jmeno>` | volitelný text | Přidání bota do lobby (admin hry) |
| `vyhodHrace:<idHrace>` | číslo | Vyhození hráče z lobby (admin hry) |
| `nahradHru:<typHry>` | číslo | Změna typu hry v lobby (admin hry) |
| `nactiPreklady:<jazyk>` | text | Načtení lokalizace herního pluginu |
| `zahajeniHry` | - | Zahájení hry (admin hry) |
| `nactiHru` | - | Načtení aktuálního stavu hry |
| `serverInfo:<heslo>` | text | Informace o serveru (admin serveru) |
| `gameInfo:<kod>:<heslo>` | kód:heslo | JSON stav hry (admin serveru) |
| `getIdHry` | - | Dotaz na ID hry |
| `novaHraSHracema:<typHry>` | číslo | Restart hry se stejnými hráči (admin hry) |
| `restartovatPluginy:<heslo>` | text | Znovunačtení pluginů (admin serveru) |

### Během hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `linuti` | - | Líznutí karty |
| `odehrani:<idKarty>` | číslo | Odehrání karty z ruky |
| `vylozeni:<idKarty>[,<idHrace>]` | číslo[,číslo] | Vyložení karty na stůl |
| `spaleni:<idKarty>` | číslo | Spálení karty |
| `konecTahu` | - | Ukončení tahu |
| `dialog:<id>,<data>` | číslo,data | Odpověď na dialog (akce/hráč/karta/text) |
| `uiClick:<id>` | číslo | Kliknutí na vlastní UI tlačítko pluginu |
| `chat:<zprava>` | text | Chat zpráva |

## Zprávy Server → Klient

### Připojení

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `welcome` | - | Potvrzení připojení k serveru |

### Před hrou

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `infoHer:<json>` | JSON | Seznam dostupných her a verze |
| `novaHra:<kodHry>` | 6-místný kód | Kód vytvořené hry |
| `pripojenKeHre` | - | Potvrzení připojení k hře |
| `overeniTokenu:<boolean>` | `true`/`false` | Výsledek ověření tokenu |
| `token:<token>` | token | Token pro znovupřipojení |
| `noveIdHrace:<id>` | číslo | Přidělené ID hráče |
| `novyHrac:<json>` | JSON | Nový hráč se připojil |
| `pripojeniHrace:<id>` | číslo | Odpojený hráč se znovu připojil |
| `odpojeniHrace:<id>` | číslo | Hráč se odpojil |
| `noveJmeno:<id>,<jmeno>` | číslo,text | Změna jména hráče |
| `setPostava:<id>,<postava>` | číslo,text | Změna postavy hráče |
| `vyberPostavu:<json>` | JSON pole | Nabídka postav |
| `hraci:<json>` | JSON pole | Seznam všech hráčů v místnosti |
| `nahradHru:<id>,<jmeno>` | číslo,text | Pozvánka do nové hry (admin změnil typ hry) |
| `nahradHruBezPtani:<id>,<jmeno>` | číslo,text | Přesměrování admina do nové hry |
| `preklady:<json>` | JSON | Lokalizační slovník herního pluginu |
| `povoleneUI:<json>` | JSON pole | Povolené UI prvky |
| `setIdHry:<id>` | číslo | ID aktuální hry |
| `serverDataHTML:<html>` | HTML | Data o serveru (admin) |
| `gameInfoJSON:<json>` | JSON | Stav hry pro admina |

### Zahájení hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `hraZacala` | - | Hra byla zahájena |
| `role:<role>` | text | Přidělená role hráče |
| `noveIdHrace:<id>` | číslo | Přidělené ID hráče |

### Během hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `novaKarta:<json>` | JSON | Nová karta v ruce |
| `zmenaPoctuKaret:<id>,<pocet>` | číslo,číslo | Změna počtu karet hráče |
| `novyPocetKaret:<id>,<pocet>` | číslo,číslo | Nový počet karet hráče |
| `pocetZivotu:<id>,<pocet>` | číslo,číslo | Změna životů hráče |
| `tvujTahZacal` | - | Začal váš tah |
| `tahZacal:<id>` | číslo | Začal tah hráče |
| `odehrat:<id>\|<json>` | číslo\|JSON | Karta byla odehrána (-1 = otočení vrchu balíčku) |
| `vylozeni:<id1>,<id2>,<json>` | číslo,číslo,JSON | Karta byla vyložena (kym, predKoho, karta) |
| `spalit:<id>\|<json>` | číslo\|JSON | Karta byla spálena |
| `spalenaVylozena:<idKarty>,<idHrace>` | číslo,číslo | Vyložená karta byla spálena ze stolu |
| `hracSkoncil:<id>` | číslo | Hráč byl vyřazen |
| `povoleneUI:<json>` | JSON pole | Povolené základní UI prvky |
| `noveUI:<json>` | JSON | Přidání/změna vlastního akčního tlačítka pluginu |
| `odebratUI:<id>` | číslo | Odstranění vlastního akčního tlačítka |
| `rychleOznameni:<text>` | text | Rychlé textové oznámení (toast, podporuje `$key`) |
| `stavHry:<text>` | text | Stavová hláška v centru obrazovky (podporuje `$key`) |
| `obrazekDobiracihoBalicku:<nazev>` | text | Nastavení grafiky balíčku karet |
| `koloStesti:<json>` | JSON | Serverem řízená animace kola štěstí |
| `vyberAkci:<json>` | JSON | Dialog pro výběr z možností |
| `vyberHrace:<json>` | JSON | Dialog pro výběr hráče/hráčů |
| `vyberKartu:<json>` | JSON | Dialog pro výběr karet |
| `vyberText:<json>` | JSON | Dialog pro zadání volného textu |
| `vysledkyHry:<json>` | JSON 2D pole | Konečné umístění hráčů |
| `konecHry` | - | Konec herní partie |
| `vyhral:<id>` | číslo | Hráč vyhrál |
| `chat:<zprava> [od: <jmeno>]` | text | Chat zpráva |

### Chyby

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `error:<json>` | JSON | Chybová zpráva (kód 1–22, skupina 0–3, klíč `$error`) |

### Ostatní

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `Echo: <zprava>` | text | Echo (debug) |
| `popoup:<text>` | text | Vyskakovací okno |

## JSON struktury

### novyHrac / hraci (položka)
```json
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
```

### novaKarta / odehrat / vylozeni / spalit (karta)
```json
{
  "id": 42,
  "jmeno": "bang",
  "obrazek": "bang.png",
  "vylozitelna": false,
  "hratelna": true
}
```

### noveUI (vlastní tlačítko)
```json
{
  "id": 1,
  "text": "Použít schopnost",
  "disabled": false
}
```

### koloStesti
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

### vyberAkci
```json
{
  "id": 1,
  "akce": [
    {"id": 0, "nazev": "Ano"},
    {"id": 1, "nazev": "Ne"}
  ],
  "nadpis": "Akce",
  "notClosable": true
}
```

### vyberHrace
```json
{
  "id": 2,
  "hraci": [0, 1, 2],
  "nadpis": "Vyber hráče",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

### vyberKartu
```json
{
  "id": 3,
  "karty": [
    {"id": 42, "jmeno": "bang", "obrazek": "bang.png"}
  ],
  "nadpis": "Vyber kartu",
  "min": 1,
  "max": 1,
  "notClosable": true
}
```

### vyberText
```json
{
  "id": 4,
  "title": "Hádej slovo",
  "placeholder": "Tip...",
  "buttonText": "Odeslat",
  "notClosable": false
}
```

### vysledkyHry
```json
[[0], [1, 2], [3]]
```

### error
```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```

## Přehled kódů chyb (1–22)

| Kód | Konstanta | Lokalizační klíč | Příčina / Význam |
|-----|-----------|------------------|------------------|
| 1 | `NEPRIPOJEN_KE_HRE` | `$error.nepripojen_ke_hre` | Herní akce bez připojení k místnosti |
| 2 | `KARTA_NEEXISTUJE` | `$error.karta_neexistuje` | Karta není v ruce hráče |
| 3 | `KARTA_NENI_HRATELNA` | `$error.karta_neni_hratelna` | Karta nemá hratelný efekt |
| 4 | `KARTA_NEJDE_ZAHRAT` | `$error.karta_nejde_zahrat` | Pravidla nedovolují kartu zahrát |
| 5 | `HRA_NEEXISTUJE` | `$error.hra_neexistuje` | Neplatný kód hry |
| 6 | `POSTAVA_NENI_NA_VYBER` | `$error.postava_neni_na_vyber` | Postava není v nabídce |
| 7 | `NEJSI_NA_TAHU` | `$error.nejsi_na_tahu` | Akce vyžaduje být na tahu |
| 8 | `NEMUZES_UKONCIT_TAH` | `$error.nemuzes_ukoncit_tah` | Přebytek karet v ruce apod. |
| 9 | `KARTA_NEJDE_SPALIT` | `$error.karta_nejde_spalit` | Kartu nelze zahodit/spálit |
| 10 | `NENI_VYLOZITELNA` | `$error.neni_vylozitelna` | Karta není trvalá/vyložitelná |
| 11 | `KARTU_NEJDE_VYLOZIT` | `$error.kartu_nejde_vylozit` | Kartu nelze teď vyložit |
| 12 | `CHYBA_PROTOKOLU` | `$error.chyba_protokolu` | Neplatný formát dat zprávy |
| 13 | `NEJDE_SI_LIZNOUT` | `$error.nejde_si_liznout` | Nelze líznout další kartu |
| 14 | `SPATNE_HESLO` | `$error.spatne_heslo` | Špatné heslo pro admin příkazy |
| 15 | `UZ_PRIPOJEN` | `$error.uz_pripojen` | Pokus o připojení k další hře současně |
| 16 | `DOSLI_KARTY_V_BALICKU` | `$error.dosli_karty_v_balicku` | Prázdný dobírací i odhazovací balíček |
| 17 | `NEJSI_ADMIN_HRY` | `$error.nejsi_admin_hry` | Akce vyžaduje administrátora hry |
| 18 | `PLNY_SERVER` | `$error.plny_server` | Dosažen limit 30 hráčů |
| 19 | `VYHOZEN_ZE_HRY` | `$error.vyhozen_ze_hry` | Hráč byl vyhozen z lobby adminem |
| 20 | `HRA_UZ_ZAHAJENA` | `$error.hra_uz_zahajena` | Pokus o změnu lobby v běžící hře |
| 21 | `HRA_NEPODPORUJE` | `$error.hra_nepodporuje` | Plugin nepodporuje funkci (např. boty) |
| 22 | `PRIPOJEN_V_JINE_ZALOZCE` | `$error.pripojen_v_jine_zalozce` | Odpojení starého spojení při reconnectu z jiné záložky |

## Základní scénáře

### Vytvoření hry
1. `→ posthogId:<id>`
2. `← welcome`
3. `→ infoHer`
4. `← infoHer:<json>`
5. `→ novaHra:0`
6. `← novaHra:123456, pripojenKeHre, token:..., novyHrac:...`
7. `→ noveJmeno:Honza`
8. `← noveJmeno:0,Honza`
9. `→ nactiPreklady:cs`
10. `← preklady:<json>`

### Připojení k hře
1. `→ posthogId:<id>`
2. `← welcome`
3. `→ pripojeniKeHre:123456`
4. `← pripojenKeHre, token:..., novyHrac:...`
5. `→ noveJmeno:Petr`
6. `← noveJmeno:1,Petr`
7. `→ nactiPreklady:cs`
8. `← preklady:<json>`

### Znovupřipojení a řešení více záložek
1. `→ overeniTokenu:123456abc...`
2. `← overeniTokenu:true`
3. `→ vraceniSe:123456abc...`
   - *Pokud byl hráč v jiné záložce:* staré spojení dostane chybu 22 a je odpojeno
4. `← pripojenKeHre, hraZacala, <aktuální stav>`

### Zahájení hry
1. `→ zahajeniHry`
2. `← hraZacala, role:šerif, noveIdHrace:0, novaKarta:..., ...`

### Tah hráče
1. `← tvujTahZacal`
2. `→ linuti`
3. `← novaKarta:...`
4. `→ odehrani:42`
5. `← odehrat:0|..., vyberHrace:...`
6. `→ dialog:1,2`
7. `← <aktualizace stavu>`
8. `→ konecTahu`
9. `← tahZacal:1`

## Poznámky

- Všechny JSON objekty jsou v jednom řádku bez mezer v kódování UTF-8.
- Token obsahuje kód hry (6 číslic) + identifikátor.
- Kód hry je 6-místné číslo (100000–999999).
- **Timeout neaktivní hry:** 1 hodina (3 600 000 ms) od odpojení všech hráčů. Při znovupřipojení se timeout ruší.
- Zprávy začínající `$key` jsou lokalizační klíče, které klient překládá z balíčku načteného přes `nactiPreklady`.
- Většina aktualizací stavu se rozesílá všem hráčům (broadcast).
