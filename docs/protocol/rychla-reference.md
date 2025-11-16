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

### Během hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `linuti` | - | Líznutí karty |
| `odehrani:<idKarty>` | číslo | Odehrání karty |
| `vylozeni:<idKarty>[,<idHrace>]` | číslo[,číslo] | Vyložení karty |
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
| `novyHrac:<json>` | JSON | Nový hráč se připojil |
| `noveJmeno:<id>,<jmeno>` | číslo,text | Změna jména hráče |
| `setPostava:<id>,<postava>` | číslo,text | Změna postavy hráče |
| `vyberPostavu:<json>` | JSON | Nabídka postav |
| `hraci:<json>` | JSON pole | Seznam všech hráčů |
| `serverDataHTML:<html>` | HTML | Data o serveru (admin) |

### Zahájení hry

| Zpráva | Payload | Popis |
|--------|---------|-------|
| `hraZacala` / `hraSpustena` | - | Hra byla zahájena |
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
| `vylozit:<id1>,<id2>,<json>` | číslo,číslo,JSON | Karta byla vyložena |
| `vyberAkci:<json>` | JSON | Dialog pro výběr akce |
| `vyberHrace:<json>` | JSON | Dialog pro výběr hráče |
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
      "nazev": "název hry",
      "popis": "popis hry",
      "id": 0
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

### novaKarta / odehrat / vylozit (karta)
```json
{
  "obrazek": "cesta/k/obrazku.png",
  "id": 42
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

### error
```json
{
  "error": "popis chyby",
  "kod": 100,
  "skupina": "PROTOKOL"
}
```

## Typické chyby

| Chyba | Příčina |
|-------|---------|
| `už jsi připojen ke hře` | Pokus o připojení k další hře |
| `Hra neexistuje` | Neplatný kód hry |
| `tato hra už byla zahájena...` | Hra již běží |
| `Nejsi připojen ke hře` | Herní akce bez připojení |
| `hráč v této hře nenalezen` | Neplatný token |
| `špatné heslo` | Špatné heslo pro serverInfo |
| `Nejsi na tahu` | Akce mimo tah |
| `Musíš zahodit karty` | Pokus o ukončení s mnoha kartami |

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
