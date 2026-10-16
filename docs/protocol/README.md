# Dokumentace protokolu síťové komunikace

Tato složka obsahuje kompletní dokumentaci komunikačního protokolu mezi klientem a serverem pro hru Bang.

> [!WARNING]  
> Předem je třeba varovat, že byla z velké části vytvořena Copilotem. Byla celá zkontrolována, ale mohou se v ní nacházet chyby

## Přehled souborů

### [index.md](index.md)
Hlavní dokumentace protokolu obsahující:
- Základní fungování a formát zpráv
- Kompletní přehled všech zpráv (klient→server a server→klient)
- Typické scénáře použití
- Poznámky k implementaci a bezpečnosti

**Kdy použít:** Pro kompletní referenci všech zpráv a jejich struktury.

### [predHrou.md](predHrou.md)
Detailní dokumentace fáze před hrou:
- Pořadí akcí při vytvoření/připojení k hře
- Podrobný popis každé zprávy v této fázi
- Scénáře: vytvoření hry, připojení, znovupřipojení
- Stavový diagram

**Kdy použít:** Při implementaci lobby systému a připojování hráčů.

### [behem-hry.md](behem-hry.md)
Detailní dokumentace herní fáze:
- Struktura tahu hráče
- Podrobný popis všech herních akcí
- Typické herní scénáře
- Poznámky k synchronizaci stavu a validaci

**Kdy použít:** Při implementaci samotné hrací logiky.

### [rychla-reference.md](rychla-reference.md)
Stručný přehled pro rychlou orientaci:
- Tabulky všech zpráv
- JSON struktury
- Přehled běžných chyb
- Základní scénáře v krátkosti

**Kdy použít:** Pro rychlé vyhledání konkrétní zprávy nebo struktury.

### [chyby.md](chyby.md)
Dokumentace chybových zpráv:
- Formát chybových zpráv
- Skupiny a kódy chyb
- Přehled všech běžných chyb s popisy
- Doporučené postupy pro zpracování chyb

**Kdy použít:** Při implementaci error handlingu a zpracování chybových stavů.

## Rychlý start

### Pro vývojáře klienta

1. Začněte s [rychla-reference.md](rychla-reference.md) pro základní přehled
2. Prostudujte [predHrou.md](predHrou.md) pro implementaci lobby
3. Prostudujte [behem-hry.md](behem-hry.md) pro implementaci hry
4. Používejte [index.md](index.md) jako kompletní referenci

### Pro vývojáře serveru

1. Prostudujte [index.md](index.md) pro celkový přehled protokolu
2. Používejte jednotlivé soubory pro detailní specifikace každé fáze
3. Dbejte na poznámky k bezpečnosti a validaci

## Struktura komunikace

```
┌─────────────┐                                    ┌─────────────┐
│   Klient    │                                    │   Server    │
└──────┬──────┘                                    └──────┬──────┘
       │                                                  │
       │ 1. WebSocket Connection                         │
       │─────────────────────────────────────────────────>│
       │                                                  │
       │ 2. posthogId:<id>                                │
       │─────────────────────────────────────────────────>│
       │                                                  │
       │ 3. welcome                                       │
       │<─────────────────────────────────────────────────│
       │                                                  │
       │ 4. FÁZE PŘED HROU (Lobby)                        │
       │    - infoHer                                     │
       │    - novaHra / pripojeniKeHre                    │
       │    - overeniTokenu, vraceniSe                    │
       │    - noveJmeno, setPostava                       │
       │    - nactiPreklady, preklady                     │
       │    - pridejBota, vyhodHrace, nahradHru           │
       │    - zahajeniHry                                 │
       │<────────────────────────────────────────────────>│
       │                                                  │
       │ 5. ZAHÁJENÍ HRY                                  │
       │    - hraZacala                                   │
       │    - role, noveIdHrace                           │
       │    - počáteční karty a životy                    │
       │<─────────────────────────────────────────────────│
       │                                                  │
       │ 6. FÁZE HRA                                      │
       │    - tahy hráčů (tvujTahZacal, tahZacal)         │
       │    - líznutí (linuti), hraní (odehrani, vylozeni)│
       │    - dialogy (vyberAkci, vyberHrace, vyberText)  │
       │    - vlastní UI (noveUI, odebratUI, uiClick)     │
       │    - animace a stav (koloStesti, stavHry)        │
       │<────────────────────────────────────────────────>│
       │                                                  │
       │ 7. KONEC HRY                                     │
       │    - vysledkyHry, konecHry, vyhral               │
       │<─────────────────────────────────────────────────│
       │                                                  │
```

## Základní koncepty

### Formát zpráv

Všechny zprávy jsou textové (String) ve dvou formátech:
- **S payload:** `typ:payload`
- **Bez payload:** `typ`

### JSON formát

Všechny JSON objekty v payload jsou obvykle:
- V jednom řádku (doporučeno pro server, ale není zaručeno)
- Bez zbytečných mezer
- V UTF-8 kódování

### Broadcast zprávy

Některé zprávy jsou automaticky rozesílány všem hráčům ve hře:
- `novyHrac` - nový hráč (nebo bot) se připojil
- `pripojeniHrace` - odpojený hráč se znovu připojil
- `odpojeniHrace` - hráč ztratil spojení
- `noveJmeno` - změna jména
- `setPostava` - změna postavy
- `odehrat` - odehrání karty (-1 pro otočení karty z balíčku)
- `vylozeni` - vyložení karty na stůl
- `spalit` - spálení karty z ruky/stolu
- `spalenaVylozena` - spálení vyložené karty
- `hracSkoncil` - hráč byl vyřazen
- `tahZacal` - začátek tahu jiného hráče
- `pocetZivotu` - změna životů
- `chat` - chat zpráva
- `rychleOznameni` - rychlé oznámení (toast)
- `stavHry` - stavová hláška v centru obrazovky
- `obrazekDobiracihoBalicku` - grafika balíčku
- `koloStesti` - animace kola štěstí
- `vysledkyHry` - výsledková listina
- `konecHry` - ukončení partie

### Token systém

Po připojení k hře server pošle token:
- Formát: `<kodHry><identifikator>` (6 číslic + 16 znaků)
- Použití: pro znovupřipojení po výpadku i po obnovení stránky
- Uložení: localStorage / sessionStorage
- Ověření: zpráva `overeniTokenu:<token>` umožňuje ověřit platnost ještě před pokusem o připojení

### Chybové zprávy

Všechny chyby mají formát:
```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```
Kódy chyb jsou v rozsahu 1–22. Text v poli `error` typicky obsahuje lokalizační klíč `$error.<nazev>`, který klient překládá do jazyka uživatele.

## Implementační poznámky

### Bezpečnost a více záložek

- Token je hlavní autentizační mechanismus hráče.
- Administrátorské zprávy (`serverInfo`, `gameInfo`, `restartovatPluginy`) jsou chráněny heslem.
- **Ochrana proti více záložkám:** Pokud se hráč připojí z jiné záložky či zařízení se stejným tokenem, server předchozí relaci ukončí s chybou `PRIPOJEN_V_JINE_ZALOZCE` (kód 22). Původní klient zablokuje automatický reconnect a nabídne tlačítko pro přepnutí hry sem.

### Synchronizace a administrace

- Server je autoritativní zdroj pravdy.
- Klient udržuje lokální kopii stavu.
- **Migrace administrátora:** Při odpojení zakladatele hry přechází administrátorská práva na dalšího připojeného hráče.
- **Dynamická lokalizace:** Klient stahuje překlady přímo z pluginu dané hry (`nactiPreklady:<jazyk>`).

### Výkon a limity

- WebSocket pro obousměrnou komunikaci v reálném čase.
- Maximální kapacita jedné místnosti: 30 hráčů.
- **Timeout neaktivní hry:** 1 hodina (3 600 000 ms) od odpojení všech hráčů. Při návratu jakéhokoliv hráče se časovač ruší.

## Časté problémy a řešení

### Ztráta spojení

**Problém:** WebSocket spojení se přerušilo

**Řešení:**
1. Ověřit platnost uloženého tokenu (`overeniTokenu:<token>`)
2. Obnovit WebSocket spojení
3. Poslat `vraceniSe:<token>`
4. Server pošle aktuální stav (`nactiHru`) a hra plynule pokračuje

### Připojení ve více záložkách

**Problém:** Hra se otevře ve dvou záložkách téhož prohlížeče

**Řešení:**
1. Server staré záložce pošle chybu kód 22 (`PRIPOJEN_V_JINE_ZALOZCE`) a odpojí ji
2. Stará záložka zastaví auto-reconnect a zobrazí dialog "Přepnout hru sem"
3. Nová záložka převezme hru bez reconnect smyčky

### Desynchronizace stavu

**Problém:** Klient má jiný stav než server

**Řešení:**
1. Poslat `nactiHru`
2. Server pošle aktuální stav (hráči, karty, UI prvky)
3. Přepsat lokální stav

### Neočekávaná chyba

**Problém:** Server vrátil `error:<json>`

**Řešení:**
1. Parsovat JSON a přeložit text pomocí `translateServerText`
2. Pokud je chyba kritická (např. kód 1 `NEPRIPOJEN_KE_HRE` nebo 19 `VYHOZEN_ZE_HRY`), promazat token a vrátit se do lobby
3. Pokud jde o kód 22 (`PRIPOJEN_V_JINE_ZALOZCE`), zastavit auto-reconnect a nabídnout dialog

## Verze protokolu

**Aktuální verze:** 0.0.7

Protokol je stále ve vývoji. Možné změny v budoucích verzích (nejsou zaručené):
- Heartbeat mechanismus
- Lepší error handling s kódy
- Autentizace a autorizace
- Rate limiting
- Komprese zpráv

## Kontakt

Pro otázky nebo návrhy ohledně protokolu:
- Vytvořte issue v GitHub repozitáři

## Licence

Tato dokumentace je součástí projektu Bang a podléhá stejné licenci jako hlavní projekt.
