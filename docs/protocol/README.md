# Dokumentace protokolu síťové komunikace

Tato složka obsahuje kompletní dokumentaci komunikačního protokolu mezi klientem a serverem pro hru Bang.

**Předem je třeba varovat, že byla z velké části vytvořena Copilotem. Byla celá zkontrolována, ale mohou se v ní nacházet chyby**

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
       │ 2. welcome                                       │
       │<─────────────────────────────────────────────────│
       │                                                  │
       │ 3. FÁZE PŘED HROU                                │
       │    - infoHer                                     │
       │    - novaHra / pripojeniKeHre                    │
       │    - noveJmeno, setPostava                       │
       │    - zahajeniHry                                 │
       │<────────────────────────────────────────────────>│
       │                                                  │
       │ 4. ZAHÁJENÍ HRY                                  │
       │    - hraZacala                                   │
       │    - role, noveIdHrace                           │
       │    - počáteční karty a životy                    │
       │<─────────────────────────────────────────────────│
       │                                                  │
       │ 5. FÁZE HRA                                      │
       │    - tahy hráčů                                  │
       │    - líznutí karet, hraní karet                  │
       │    - dialogy a interakce                         │
       │<────────────────────────────────────────────────>│
       │                                                  │
       │ 6. KONEC HRY                                     │
       │    - vyhral                                      │
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
- `novyHrac` - nový hráč se připojil
- `noveJmeno` - změna jména
- `setPostava` - změna postavy
- `odehrat` - odehrání karty
- `vylozit` - vyložení karty
- `tahZacal` - začátek tahu
- `pocetZivotu` - změna životů
- `chat` - chat zprávy

### Token systém

Po připojení k hře server pošle token:
- Formát: `<kodHry><identifikator>` (6 číslic + 16 znaků)
- Použití: pro znovupřipojení po výpadku
- Uložení: localStorage nebo podobné trvalé úložiště

### Chybové zprávy

Všechny chyby mají formát:
```json
{
  "error": "popis chyby",
  "kod": 100,
  "skupina": "PROTOKOL"
}
```

## Implementační poznámky

### Bezpečnost

- Token je jediný autentizační mechanismus
- ServerInfo je chráněn heslem
- Validace na serveru je povinná
- Klient by měl provádět základní validaci pro UX

### Synchronizace

- Server je autoritativní zdroj pravdy
- Klient udržuje lokální kopii stavu
- Všechny změny jsou potvrzeny serverem
- Zprávy jsou zpracovávány v pořadí příchodu

### Výkon

- WebSocket pro obousměrnou komunikaci
- Minimální latence díky persistentnímu spojení
- JSON pro jednoduchost a čitelnost
- Neaktivní hry se automaticky mažou (5 min timeout)

## Časté problémy a řešení

### Ztráta spojení

**Problém:** WebSocket spojení se přerušilo

**Řešení:**
1. Načíst uložený token
2. Obnovit WebSocket spojení
3. Poslat `vraceniSe:<token>`
4. Server pošle aktuální stav

### Desynchronizace stavu

**Problém:** Klient má jiný stav než server

**Řešení:**
1. Poslat `nactiHru`
2. Server pošle aktuální stav
3. Přepsat lokální stav

### Neočekávaná chyba

**Problém:** Server vrátil `error:<json>`

**Řešení:**
1. Parsovat JSON a zobrazit chybu
2. Pokud je kritická, vrátit se na hlavní obrazovku
3. Logovat pro debugging

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
