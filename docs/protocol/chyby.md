# Dokumentace chyb

Tento dokument obsahuje podrobné informace o chybových zprávách v protokolu, jejich kódech a skupinách podle implementace v `src/main/java/cz/honza/bang/net/Chyba.java`.

## Formát chybové zprávy

Všechny chyby mají jednotný formát:

```
error:<json>
```

### Dva typy formátu JSON

Server posílá chyby ve dvou formátech:

#### 1. Úplný formát (s kódem a skupinou)
Pro chyby definované v Chyba.java enum:

```json
{
  "error": "popis chyby",
  "kod": 1,
  "skupina:": 2
}
```

**Poznámka:** Ve skutečné implementaci je překlep - pole se jmenuje `"skupina:"` místo `"skupina"`.

#### 2. Zjednodušený formát (pouze zpráva)
Pro některé chyby spojení a protokolu:

```json
{
  "error": "popis chyby"
}
```

### Pole JSON objektu

- **error** (String, povinné): Lidsky čitelný popis chyby v češtině
- **kod** (Number, volitelné): Číselný kód chyby pro programové zpracování (1-12)
- **skupina:** (Number, volitelné): Číselný kód skupiny chyby (0-3)

## Skupiny chyb

Chyby jsou rozděleny do skupin podle číselného kódu:

### Skupina 0 - Interní chyba serveru
Interní chyby serveru, které by neměly nastat za běžných okolností.

### Skupina 1 - Špatný formát zprávy
Chyby způsobené neplatným formátem zprávy nebo neplatnou operací.

**Příklady:**
- Neplatný formát zprávy
- Karta není hratelná
- Postava není na výběr
- Karta není vyložitelná
- Chyba protokolu

### Skupina 2 - Chybějící data
Chyby způsobené chybějícími nebo neexistujícími daty.

**Příklady:**
- Nejsi připojen ke hře
- Karta neexistuje

### Skupina 3 - Nedodržuje herní pravidla
Chyby způsobené porušením herních pravidel.

**Příklady:**
- Tuto kartu teď nemůžeš zahrát
- Nejsi na tahu
- Nemůžeš ukončit tah
- Kartu nejde vyložit

## Přehled všech definovaných chyb

Následující chyby jsou definovány v `Chyba.java` enum a jsou posílány s kódem a skupinou:

### NEPRIPOJEN_KE_HRE
**Zpráva:** "Nejsi připojen ke hře."  
**Kód:** 1  
**Skupina:** 2 (chybějící data)

**Kdy nastane:** Pokus o herní akci bez připojení ke hře

**Příklad:**
```json
{"error":"Nejsi připojen ke hře.","kod":1,"skupina:":2}
```

---

### KARTA_NEEXISTUJE
**Zpráva:** "Tato karta neexistuje"  
**Kód:** 2  
**Skupina:** 2 (chybějící data)

**Kdy nastane:** Pokus o akci s kartou, která neexistuje v ruce hráče

**Příklad:**
```json
{"error":"Tato karta neexistuje","kod":2,"skupina:":2}
```

---

### KARTA_NENI_HRATELNA
**Zpráva:** "Tato karta není hratelná."  
**Kód:** 3  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o odehrání karty, která není hratelná

**Příklad:**
```json
{"error":"Tato karta není hratelná.","kod":3,"skupina:":1}
```

---

### KARTA_NEJDE_ZAHRAT
**Zpráva:** "Tuto kartu ted nemuzes zahrat."  
**Kód:** 4  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o zahrání karty v nevhodný moment podle herních pravidel

**Příklad:**
```json
{"error":"Tuto kartu ted nemuzes zahrat.","kod":4,"skupina:":3}
```

---

### HRA_NEEXISTUJE
**Zpráva:** "Hra, ke které se snažíš připojit neexistuje."  
**Kód:** 5  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o připojení k neexistující hře

**Příklad:**
```json
{"error":"Hra, ke které se snažíš připojit neexistuje.","kod":5,"skupina:":1}
```

---

### POSTAVA_NENI_NA_VYBER
**Zpráva:** "Postava není na výběr"  
**Kód:** 6  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o výběr postavy, která není v nabídce

**Příklad:**
```json
{"error":"Postava není na výběr","kod":6,"skupina:":1}
```

---

### NEJSI_NA_TAHU
**Zpráva:** "Nejsi na tahu."  
**Kód:** 7  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o akci, která vyžaduje, abyste byli na tahu

**Příklad:**
```json
{"error":"Nejsi na tahu.","kod":7,"skupina:":3}
```

---

### NEMUZES_UKONCIT_TAH
**Zpráva:** "Takhle tah ukončit nejde."  
**Kód:** 8  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o ukončení tahu, když herní pravidla to ještě nepovolují (např. máte příliš mnoho karet)

**Příklad:**
```json
{"error":"Takhle tah ukončit nejde.","kod":8,"skupina:":3}
```

---

### KARTA_NEJDE_SPALIT
**Zpráva:** "Tuhle kartu bohužel nemůžeš spálit."  
**Kód:** 9  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o spálení karty, která nemůže být spálena

**Příklad:**
```json
{"error":"Tuhle kartu bohužel nemůžeš spálit.","kod":9,"skupina:":1}
```

---

### NENI_VYLOZITELNA
**Zpráva:** "Tahle karta není vyložitelná."  
**Kód:** 10  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o vyložení karty, která není vyložitelná

**Příklad:**
```json
{"error":"Tahle karta není vyložitelná.","kod":10,"skupina:":1}
```

---

### KARTU_NEJDE_VYLOZIT
**Zpráva:** "Tuhle kartu teď nemůžeš vyložit."  
**Kód:** 11  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o vyložení karty v nevhodný moment podle pravidel

**Příklad:**
```json
{"error":"Tuhle kartu teď nemůžeš vyložit.","kod":11,"skupina:":3}
```

---

### CHYBA_PROTOKOLU
**Zpráva:** "Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi."  
**Kód:** 12  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Neplatný formát zprávy nebo chybějící povinná data

**Příklad:**
```json
{"error":"Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi.","kod":12,"skupina:":1}
```

---

## Chyby bez kódu a skupiny

Následující chyby jsou posílány pouze se zprávou, bez kódu a skupiny:

### `už jsi připojen ke hře`
**Kdy nastane:** Pokus o vytvoření nové hry nebo připojení k jiné hře, když jste již ve hře

```json
{"error":"už jsi připojen ke hře"}
```

---

### `Hra neexistuje`
**Kdy nastane:** Pokus o připojení k hře s neplatným kódem (varianta z SocketServer.java)

```json
{"error":"Hra neexistuje"}
```

---

### `tato hra už byla zahájena. Bohužel se už nejde připojit.`
**Kdy nastane:** Pokus o připojení k hře, která již běží

```json
{"error":"tato hra už byla zahájena. Bohužel se už nejde připojit."}
```

---

### `Nejsi připojen ke hře`
**Kdy nastane:** Pokus o herní akci bez připojení ke hře (varianta z SocketServer.java)

```json
{"error":"Nejsi připojen ke hře"}
```

---

### `hráč v této hře nenalezen`
**Kdy nastane:** Pokus o znovupřipojení s neplatným tokenem

```json
{"error":"hráč v této hře nenalezen"}
```

---

### `hra do které se snažíš připojit neexistuje`
**Kdy nastane:** Pokus o znovupřipojení k neexistující hře

```json
{"error":"hra do které se snažíš připojit neexistuje"}
```

---

### `špatné heslo`
**Kdy nastane:** Neplatné heslo pro přístup k serverInfo

```json
{"error":"špatné heslo"}
```

---

## Zpracování chyb na straně klienta

### Doporučené postupy

1. **Zpracovávejte oba formáty chyb:**
   ```javascript
   if (type === "error") {
     const errorData = JSON.parse(payload);
     handleError(errorData);
   }
   ```

2. **Kontrolujte přítomnost kódu a skupiny:**
   ```javascript
   function handleError(errorData) {
     console.error("Server error:", errorData);
     
     if (errorData.kod !== undefined) {
       // Chyba s kódem - použijte kód pro rozhodování
       handleErrorByCode(errorData.kod, errorData["skupina:"], errorData.error);
     } else {
       // Chyba bez kódu - použijte text zprávy
       handleErrorByMessage(errorData.error);
     }
   }
   ```

3. **Používejte skupiny pro rozhodování o závažnosti:**
   ```javascript
   function handleErrorByCode(kod, skupina, zprava) {
     switch (skupina) {
       case 0: // Interní chyba serveru
         toast.error("Chyba serveru: " + zprava);
         break;
       
       case 1: // Špatný formát zprávy
         toast.error(zprava);
         break;
       
       case 2: // Chybějící data
         toast.error(zprava);
         if (kod === 1) { // NEPRIPOJEN_KE_HRE
           navigateToLobby();
         }
         break;
       
       case 3: // Nedodržuje herní pravidla
         toast.warning(zprava);
         // Zůstat ve hře
         break;
       
       default:
         toast.error(zprava);
         break;
     }
   }
   ```

4. **Pozor na překlep v poli skupina:**
   ```javascript
   // Pole se jmenuje "skupina:" (s dvojtečkou)
   const skupina = errorData["skupina:"];
   ```

5. **Logujte všechny chyby pro debugging:**
   ```javascript
   console.error("Chyba ze serveru:", errorData);
   ```

### Příklad kompletního zpracování

```javascript
function handleError(errorData) {
  // Log pro debugging
  console.error("Server error:", errorData);
  
  // Zobrazit zprávu uživateli
  const zprava = errorData.error;
  
  // Rozhodnout podle kódu nebo zprávy
  if (errorData.kod !== undefined) {
    const kod = errorData.kod;
    const skupina = errorData["skupina:"];
    
    // Kritické chyby - vrátit na lobby
    if (kod === 1) { // NEPRIPOJEN_KE_HRE
      toast.error(zprava);
      navigateToLobby();
      return;
    }
    
    // Herní pravidla - pouze informovat
    if (skupina === 3) {
      toast.warning(zprava);
      return;
    }
    
    // Ostatní chyby
    toast.error(zprava);
  } else {
    // Chyby bez kódu
    if (zprava.includes("už jsi připojen")) {
      toast.error(zprava);
      return;
    }
    
    if (zprava.includes("neexistuje") || zprava.includes("nenalezen")) {
      toast.error(zprava);
      navigateToLobby();
      return;
    }
    
    toast.error(zprava);
  }
}
```

## Tabulka kódů chyb

| Kód | Název konstanty | Zpráva | Skupina |
|-----|----------------|--------|---------|
| 1 | NEPRIPOJEN_KE_HRE | Nejsi připojen ke hře. | 2 |
| 2 | KARTA_NEEXISTUJE | Tato karta neexistuje | 2 |
| 3 | KARTA_NENI_HRATELNA | Tato karta není hratelná. | 1 |
| 4 | KARTA_NEJDE_ZAHRAT | Tuto kartu ted nemuzes zahrat. | 3 |
| 5 | HRA_NEEXISTUJE | Hra, ke které se snažíš připojit neexistuje. | 1 |
| 6 | POSTAVA_NENI_NA_VYBER | Postava není na výběr | 1 |
| 7 | NEJSI_NA_TAHU | Nejsi na tahu. | 3 |
| 8 | NEMUZES_UKONCIT_TAH | Takhle tah ukončit nejde. | 3 |
| 9 | KARTA_NEJDE_SPALIT | Tuhle kartu bohužel nemůžeš spálit. | 1 |
| 10 | NENI_VYLOZITELNA | Tahle karta není vyložitelná. | 1 |
| 11 | KARTU_NEJDE_VYLOZIT | Tuhle kartu teď nemůžeš vyložit. | 3 |
| 12 | CHYBA_PROTOKOLU | Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi. | 1 |

## Poznámky k implementaci

### Známé problémy

1. **Překlep v názvu pole:** Pole pro skupinu se jmenuje `"skupina:"` (s dvojtečkou) místo `"skupina"`. Jedná se o chybu v implementaci serveru.

2. **Dva různé formáty:** Některé chyby mají kód a skupinu, jiné ne. Klient musí být připraven na oba formáty.

3. **Duplicitní zprávy:** Některé chyby existují ve dvou variantách - jedna s kódem (z Chyba.java) a jedna bez (z SocketServer.java). Například "Nejsi připojen ke hře" vs "Nejsi připojen ke hře."

### Doporučení pro budoucí verze

- Opravit překlep v názvu pole `"skupina:"` → `"skupina"`
- Sjednotit formát všech chyb (vždy včetně kódu a skupiny)
- Sjednotit podobné chybové zprávy
- Přidat více chyb do Chyba.java enum pro konzistenci

## Související dokumentace

- [index.md](index.md) - Kompletní reference protokolu
- [behem-hry.md](behem-hry.md) - Herní fáze a možné chyby
- [predHrou.md](predHrou.md) - Fáze před hrou a možné chyby
- `src/main/java/cz/honza/bang/net/Chyba.java` - Zdrojový kód definic chyb
