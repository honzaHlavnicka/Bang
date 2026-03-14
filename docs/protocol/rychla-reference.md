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
| `infoHer` | - | Získání informací o serveru a hrách |
| `novaHra:<typHry>` | číslo | Vytvoření nové hry |
| `pripojeniKeHre:<kodHry>` | 6-místný kód | Připojení k existující hře |
| `vraceniSe:<token>` | token | Znovupřipojení k hře |
| `noveJmeno:<jmeno>` | text | Nastavení/změna jména |
| `setPostava:<postava>` | text | Výběr postavy |
| `zahajeniHry` | - | Zahájení hry |
| `nactiHru` | - | Načtení aktuálního stavu |
| `serverInfo:<heslo>` | text | Informace o serveru (admin) |
| `getIdHry` | - | Dotaz na ID hry |
| `novaHraSHracema:<typHry>` | číslo | Restart hry se stejnými hráči (admin hry) |
| `restartovatPluginy:<heslo>` | text | Znovunačtení pluginů (admin serveru) |

### Během hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `linuti` | - | Líznutí karty |
| `odehrani:<idKarty>` | číslo | Odehrání karty |
| `vylozeni:<idKarty>[,<idHrace>]` | číslo[,číslo] | Vyložení karty |
| `spaleni:<idKarty>` | číslo | Spálení karty |
| `konecTahu` | - | Ukončení tahu |
| `dialog:<id>,<data>` | číslo,číslo | Odpověď na dialog |
| `chat:<zprava>` | text | Chat zpráva |

## Zprávy Server → Klient

### Připojení

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `welcome` | - | Potvrzení připojení |

### Před hrou

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `infoHer:<json>` | JSON | Seznam dostupných her |
| `novaHra:<kodHry>` | 6-místný kód | Kód nové hry |
| `pripojenKeHre` | - | Potvrzení připojení k hře |
| `token:<token>` | token | Token pro znovupřipojení |
| `noveIdHrace:<id>` | číslo | Přidělené ID hráče |
| `novyHrac:<json>` | JSON | Nový hráč se připojil |
| `noveJmeno:<id>,<jmeno>` | číslo,text | Změna jména hráče |
| `setPostava:<id>,<postava>` | číslo,text | Změna postavy hráče |
| `vyberPostavu:<json>` | JSON pole | Nabídka postav |
| `hraci:<json>` | JSON pole | Seznam všech hráčů |
| `povoleneUI:<json>` | JSON pole | Povolené UI prvky |
| `setIdHry:<id>` | číslo | ID aktuální hry |
| `serverDataHTML:<html>` | HTML | Data o serveru (admin) |

### Zahájení hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `hraZacala` | - | Hra byla zahájena |
| `role:<role>` | text | Přidělená role |
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
| `odehrat:<id>\|<json>` | číslo\|JSON | Karta byla odehrána |
| `vylozeni:<id1>,<id2>,<json>` | číslo,číslo,JSON | Karta byla vyložena (kym, predKoho, karta) |
| `spalit:<id>\|<json>` | číslo\|JSON | Karta byla spálena |
| `spalenaVylozena:<idKarty>,<idHrace>` | číslo,číslo | Vyložená karta byla spálena |
| `hracSkoncil:<id>` | číslo | Hráč byl vyřazen |
| `povoleneUI:<json>` | JSON pole | Povolené UI prvky |
| `rychleOznameni:<text>` | text | Rychlé oznámení |
| `vyberAkci:<json>` | JSON | Dialog pro výběr akce |
| `vyberHrace:<json>` | JSON | Dialog pro výběr hráče |
| `vyberKartu:<json>` | JSON | Dialog pro výběr karty |
| `vyhral:<id>` | číslo | Hráč vyhrál |
| `chat:<zprava> [od: <jmeno>]` | text | Chat zpráva |

### Chyby

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `error:<json>` | JSON | Chybová zpráva |

### Ostatní

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `Echo: <zprava>` | text | Echo (debug) |
| `popoup:<text>` | text | Vyskakovací okno |

## JSON struktury

### infoHer (odpověď)
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

### novyHrac / hraci (položka)
```json
{
  "jmeno": "Hráč 1",
  "zivoty": 4,
  "maximumZivotu": 4,
  "role": "šerif",
  "id": 0,
  "pocetKaret": 5,
  "postava": "BART_CASSIDY"
}
```

### novaKarta / odehrat / vylozeni / spalit (karta)
```json
{
  "jmeno": "bang",
  "obrazek": "cesta/k/obrazku.png",
  "id": 42
}
```

### vyberPostavu (položka postavy)
```json
{
  "jmeno": "BART_CASSIDY",
  "obrazek": "BART_CASSIDY",
  "popis": "Popis postavy",
  "zivoty": "4"
}
```

### vyberAkci
```json
{
  "id": 1,
  "akce": [
    {"id": 0, "nazev": "Ano"},
    {"id": 1, "nazev": "Ne"}
  ]
}
```

### vyberHrace
```json
{
  "id": 2,
  "hraci": [0, 1, 2],
  "nadpis": "Vyber hráče"
}
```

### vyberKartu
```json
{
  "id": 3,
  "karty": [
    {"jmeno": "bang", "obrazek": "bang.png", "id": 42}
  ],
  "nadpis": "Vyber kartu"
}
```

### error
```json
{
  "error": "popis chyby",
  "kod": 1,
  "skupina": 2
}
```

## Typické chyby

| Kód | Chyba | Příčina |
|-----|-------|---------|
| 1 | `Nejsi připojen ke hře.` | Herní akce bez připojení |
| 5 | `Hra, ke které se snažíš připojit neexistuje.` | Neplatný kód hry |
| 7 | `Nejsi na tahu.` | Akce mimo tah |
| 8 | `Takhle tah ukončit nejde.` | Pokus o ukončení tahu s mnoha kartami |
| 13 | `Teď si nemůžeš líznout.` | Nelze líznout kartu |
| 14 | `Špatné heslo.` | Špatné heslo pro serverInfo/restartovatPluginy |
| 15 | `Už jsi připojen ke hře.` | Pokus o připojení k další hře |
| 17 | `Nejsi administrátor hry.` | Akce bez práv administrátora hry |
| - | `tato hra už byla zahájena...` | Hra již běží |
| - | `hráč v této hře nenalezen` | Neplatný token |

## Základní scénáře

### Vytvoření hry
1. `→ infoHer`
2. `← infoHer:<json>`
3. `→ novaHra:0`
4. `← novaHra:123456, pripojenKeHre, token:..., novyHrac:...`
5. `→ noveJmeno:Honza`
6. `← noveJmeno:0,Honza`

### Připojení k hře
1. `→ pripojeniKeHre:123456`
2. `← pripojenKeHre, token:..., novyHrac:...`
3. `→ noveJmeno:Petr`
4. `← noveJmeno:1,Petr`

### Znovupřipojení
1. `→ vraceniSe:123456abc...`
2. `← pripojenKeHre, hraZacala, <aktuální stav>`

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

- Všechny JSON objekty jsou v jednom řádku bez mezer
- Token obsahuje kód hry (6 číslic) + identifikátor
- Kód hry je 6-místné číslo (100000-999999)
- Neaktivní hra se smaže po 5 minutách
- Některé zprávy jsou rozesílány všem hráčům (broadcast)
