# Dokumentace chyb

Tento dokument obsahuje podrobné informace o chybových zprávách v protokolu, jejich kódech a skupinách.

## Formát chybové zprávy

Všechny chyby mají jednotný formát:

```
error:<json>
```

kde JSON má následující strukturu:

```json
{
  "error": "popis chyby",
  "kod": 100,
  "skupina": "PROTOKOL"
}
```

### Pole JSON objektu

- **error** (String, povinné): Lidsky čitelný popis chyby v češtině
- **kod** (Number, volitelné): Číselný kód chyby pro programové zpracování
- **skupina** (String, volitelné): Kategorie/skupina chyby

## Skupiny chyb

Chyby jsou rozděleny do několika skupin podle jejich původu a charakteru:

### PROTOKOL
Chyby související s porušením protokolu nebo neplatným použitím zpráv.

**Příklady:**
- Pokus o herní akci bez připojení ke hře
- Pokus o připojení k více hrám současně
- Neplatný formát zprávy

### HRA
Chyby související s herní logikou a pravidly.

**Příklady:**
- Pokus o akci, která není povolena pravidly
- Pokus o ukončení tahu s neplatným stavem
- Pokus o odehrání karty, kterou nemáte

### PRIPOJENI
Chyby související s připojením a autentizací.

**Příklady:**
- Neplatný kód hry
- Neplatný token pro znovupřipojení
- Hra již byla zahájena

### AUTORIZACE
Chyby související s oprávněními a přístupem.

**Příklady:**
- Neplatné heslo pro serverInfo
- Pokus o akci, kterou nemáte právo provést

## Přehled běžných chyb

### Chyby před hrou

#### `už jsi připojen ke hře`
**Skupina:** PROTOKOL  
**Kdy nastane:** Pokus o vytvoření nové hry nebo připojení k jiné hře, když jste již ve hře  
**Řešení:** Odpojte se od aktuální hry před připojením k jiné

```json
{"error":"už jsi připojen ke hře"}
```

---

#### `Hra neexistuje`
**Skupina:** PRIPOJENI  
**Kdy nastane:** Pokus o připojení k hře s neplatným kódem  
**Řešení:** Ověřte správnost 6-místného kódu hry

```json
{"error":"Hra neexistuje"}
```

---

#### `tato hra už byla zahájena. Bohužel se už nejde připojit.`
**Skupina:** PRIPOJENI  
**Kdy nastane:** Pokus o připojení k hře, která již běží  
**Řešení:** Můžete se pokusit o znovupřipojení pomocí tokenu, pokud jste byli dříve ve hře

```json
{"error":"tato hra už byla zahájena. Bohužel se už nejde připojit."}
```

---

#### `Nejsi připojen ke hře`
**Skupina:** PROTOKOL  
**Kdy nastane:** Pokus o herní akci bez připojení ke hře  
**Řešení:** Nejprve se připojte k hře pomocí `novaHra` nebo `pripojeniKeHre`

```json
{"error":"Nejsi připojen ke hře"}
```

---

#### `hráč v této hře nenalezen`
**Skupina:** PRIPOJENI  
**Kdy nastane:** Pokus o znovupřipojení s neplatným tokenem  
**Řešení:** Token může být neplatný nebo hra již neexistuje. Připojte se k nové hře

```json
{"error":"hráč v této hře nenalezen"}
```

---

#### `špatné heslo`
**Skupina:** AUTORIZACE  
**Kdy nastane:** Neplatné heslo pro přístup k serverInfo  
**Řešení:** Použijte správné administrátorské heslo

```json
{"error":"špatné heslo"}
```

---

### Chyby během hry

#### `Nejsi na tahu`
**Skupina:** HRA  
**Kdy nastane:** Pokus o akci, která vyžaduje, abyste byli na tahu  
**Řešení:** Počkejte, až přijde na řadu váš tah

```json
{"error":"Nejsi na tahu"}
```

---

#### `Už jsi líznul maximální počet karet`
**Skupina:** HRA  
**Kdy nastane:** Pokus o další líznutí karty, když jste již dosáhli limitu  
**Řešení:** Přejděte k další fázi tahu (hraní karet nebo ukončení tahu)

```json
{"error":"Už jsi líznul maximální počet karet"}
```

---

#### `Tuto kartu nemůžeš odehrát`
**Skupina:** HRA  
**Kdy nastane:** Pokus o odehrání karty, která není povolena pravidly  
**Řešení:** Vyberte jinou kartu nebo akci

```json
{"error":"Tuto kartu nemůžeš odehrát"}
```

---

#### `Musíš zahodit karty`
**Skupina:** HRA  
**Kdy nastane:** Pokus o ukončení tahu s příliš mnoha kartami v ruce  
**Řešení:** Zahoďte karty, dokud nemáte správný počet (obvykle maximální počet životů)

```json
{"error":"Musíš zahodit karty"}
```

---

#### `Nemuzeš ukončit tah` / `NEMUZES_UKONCIT_TAH`
**Skupina:** HRA  
**Kdy nastane:** Pokus o ukončení tahu, když herní pravidla to ještě nepovolují  
**Řešení:** Dokončete povinné akce (např. zahoďte přebytečné karty)

```json
{"error":"Nemůžeš ukončit tah","kod":1,"skupina":"HRA"}
```

---

#### `CHYBA_PROTOKOLU`
**Skupina:** PROTOKOL  
**Kdy nastane:** Neplatný formát zprávy nebo chybějící povinná data  
**Řešení:** Opravte formát zprávy podle dokumentace protokolu

```json
{"error":"Chyba protokolu","kod":2,"skupina":"PROTOKOL"}
```

---

## Kódy chyb

Číselné kódy chyb pro programové zpracování:

| Kód | Skupina | Význam |
|-----|---------|--------|
| 1   | HRA     | Neplatná herní akce |
| 2   | PROTOKOL | Chyba protokolu |
| 100 | PROTOKOL | Obecná chyba protokolu |
| 101 | PRIPOJENI | Chyba připojení |
| 102 | AUTORIZACE | Chyba autorizace |

**Poznámka:** Systém kódů chyb je ve vývoji a může se v budoucnu rozšířit.

## Zpracování chyb na straně klienta

### Doporučené postupy

1. **Vždy zpracovávejte chybové zprávy:**
   ```javascript
   if (type === "error") {
     const errorData = JSON.parse(payload);
     handleError(errorData);
   }
   ```

2. **Rozlišujte mezi typy chyb:**
   - **Kritické chyby:** Vraťte uživatele na hlavní obrazovku (např. "Nejsi připojen ke hře")
   - **Drobné chyby:** Pouze informujte uživatele (např. "Nejsi na tahu")

3. **Používejte skupiny chyb pro rozhodování:**
   ```javascript
   if (errorData.skupina === "PRIPOJENI") {
     // Vrátit na lobby nebo hlavní menu
   } else if (errorData.skupina === "HRA") {
     // Zobrazit info a zůstat ve hře
   }
   ```

4. **Logujte všechny chyby pro debugging:**
   ```javascript
   console.error("Chyba ze serveru:", errorData);
   ```

5. **Zobrazujte uživatelsky přívětivé zprávy:**
   ```javascript
   toast.error(errorData.error);
   ```

### Příklad zpracování

```javascript
function handleError(errorData) {
  // Log pro debugging
  console.error("Server error:", errorData);
  
  // Rozhodnutí podle skupiny
  switch (errorData.skupina) {
    case "PRIPOJENI":
      toast.error(errorData.error);
      navigateToLobby();
      break;
    
    case "HRA":
      toast.warning(errorData.error);
      // Zůstat ve hře
      break;
    
    case "AUTORIZACE":
      toast.error(errorData.error);
      navigateToHome();
      break;
    
    default:
      toast.error(errorData.error || "Neznámá chyba");
      break;
  }
}
```

## Budoucí vylepšení

Systém chyb je stále ve vývoji. Plánované změny:

- Rozšíření kódů chyb pro všechny typy chyb
- Lokalizace chybových zpráv
- Detailnější informace v chybách (např. které pravidlo bylo porušeno)
- Nápovědy a návrhy řešení přímo v chybové zprávě
- Stacktrace pro debugging (pouze ve vývojovém režimu)

## Související dokumentace

- [index.md](index.md) - Kompletní reference protokolu
- [behem-hry.md](behem-hry.md) - Herní fáze a možné chyby
- [predHrou.md](predHrou.md) - Fáze před hrou a možné chyby
