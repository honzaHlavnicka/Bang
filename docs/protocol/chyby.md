# Dokumentace chyb

Tento dokument obsahuje podrobné informace o chybových zprávách v protokolu, jejich kódech a skupinách podle implementace v `plugin-sdk/src/main/java/cz/honzaa/bang/sdk/Chyba.java`.

## Formát chybové zprávy

Všechny chyby mají jednotný formát:

```
error:<json>
```

### Dva typy formátu JSON

Server posílá chyby ve dvou formátech:

#### 1. Úplný formát (s kódem a skupinou)
Pro chyby definované v `Chyba.java` enum:

```json
{
  "error": "$error.karta_nejde_zahrat",
  "kod": 4,
  "skupina": 3
}
```

#### 2. Zjednodušený formát (pouze zpráva)
Pro některé starší chyby spojení a protokolu (ze SocketServer.java):

```json
{
  "error": "popis chyby"
}
```

### Pole JSON objektu

- **error** (String, povinné): Překladový klíč začínající `$error.<nazev>` (např. `$error.karta_nejde_zahrat`) nebo lidsky čitelný popis chyby. Klient řetězce začínající `$` překládá přes i18n lokalizační slovník.
- **kod** (Number, volitelné): Číselný kód chyby pro programové zpracování (1–22)
- **skupina** (Number, volitelné): Číselný kód skupiny chyby (0–3)

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
{"error":"Nejsi připojen ke hře.","kod":1,"skupina":2}
```

---

### KARTA_NEEXISTUJE
**Zpráva:** "Tato karta neexistuje"  
**Kód:** 2  
**Skupina:** 2 (chybějící data)

**Kdy nastane:** Pokus o akci s kartou, která neexistuje v ruce hráče

**Příklad:**
```json
{"error":"Tato karta neexistuje","kod":2,"skupina":2}
```

---

### KARTA_NENI_HRATELNA
**Zpráva:** "Tato karta není hratelná."  
**Kód:** 3  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o odehrání karty, která není hratelná

**Příklad:**
```json
{"error":"Tato karta není hratelná.","kod":3,"skupina":1}
```

---

### KARTA_NEJDE_ZAHRAT
**Zpráva:** "Tuto kartu ted nemuzes zahrat."  
**Kód:** 4  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o zahrání karty v nevhodný moment podle herních pravidel

**Příklad:**
```json
{"error":"Tuto kartu ted nemuzes zahrat.","kod":4,"skupina":3}
```

---

### HRA_NEEXISTUJE
**Zpráva:** "Hra, ke které se snažíš připojit neexistuje."  
**Kód:** 5  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o připojení k neexistující hře

**Příklad:**
```json
{"error":"Hra, ke které se snažíš připojit neexistuje.","kod":5,"skupina":1}
```

---

### POSTAVA_NENI_NA_VYBER
**Zpráva:** "Postava není na výběr"  
**Kód:** 6  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o výběr postavy, která není v nabídce

**Příklad:**
```json
{"error":"Postava není na výběr","kod":6,"skupina":1}
```

---

### NEJSI_NA_TAHU
**Zpráva:** "Nejsi na tahu."  
**Kód:** 7  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o akci, která vyžaduje, abyste byli na tahu

**Příklad:**
```json
{"error":"Nejsi na tahu.","kod":7,"skupina":3}
```

---

### NEMUZES_UKONCIT_TAH
**Zpráva:** "Takhle tah ukončit nejde."  
**Kód:** 8  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o ukončení tahu, když herní pravidla to ještě nepovolují (např. máte příliš mnoho karet)

**Příklad:**
```json
{"error":"Takhle tah ukončit nejde.","kod":8,"skupina":3}
```

---

### KARTA_NEJDE_SPALIT
**Zpráva:** "Tuhle kartu bohužel nemůžeš spálit."  
**Kód:** 9  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o spálení karty, která nemůže být spálena

**Příklad:**
```json
{"error":"Tuhle kartu bohužel nemůžeš spálit.","kod":9,"skupina":1}
```

---

### NENI_VYLOZITELNA
**Zpráva:** "Tahle karta není vyložitelná."  
**Kód:** 10  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o vyložení karty, která není vyložitelná

**Příklad:**
```json
{"error":"Tahle karta není vyložitelná.","kod":10,"skupina":1}
```

---

### KARTU_NEJDE_VYLOZIT
**Zpráva:** "Tuhle kartu teď nemůžeš vyložit."  
**Kód:** 11  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o vyložení karty v nevhodný moment podle pravidel

**Příklad:**
```json
{"error":"Tuhle kartu teď nemůžeš vyložit.","kod":11,"skupina":3}
```

---

### CHYBA_PROTOKOLU
**Zpráva:** "Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi."  
**Kód:** 12  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Neplatný formát zprávy nebo chybějící povinná data

**Příklad:**
```json
{"error":"Nastala chyba při komunikaci.\nZkontrolujte, zda používáte správnou verzi.","kod":12,"skupina":1}
```

---

### NEJDE_SI_LIZNOUT
**Zpráva:** "Teď si nemůžeš líznout."  
**Kód:** 13  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o líznutí karty, když to herní pravidla nedovolují (např. již líznutý maximální počet karet)

**Příklad:**
```json
{"error":"Teď si nemůžeš líznout.","kod":13,"skupina":3}
```

---

### SPATNE_HESLO
**Zpráva:** "Špatné heslo."  
**Kód:** 14  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Nesprávné heslo pro přístup k `serverInfo` nebo `restartovatPluginy`

**Příklad:**
```json
{"error":"Špatné heslo.","kod":14,"skupina":1}
```

---

### UZ_PRIPOJEN
**Zpráva:** "Už jsi připojen ke hře."  
**Kód:** 15  
**Skupina:** 2 (chybějící data)

**Kdy nastane:** Pokus o vytvoření nové hry nebo připojení k jiné hře, když jste již ve hře

**Příklad:**
```json
{"error":"Už jsi připojen ke hře.","kod":15,"skupina":2}
```

---

### DOSLI_KARTY_V_BALICKU
**Zpráva:** "Došly karty v balíčku."  
**Kód:** 16  
**Skupina:** 3 (nedodržuje herní pravidla)

**Kdy nastane:** Pokus o líznutí karty, když je balíček prázdný

**Příklad:**
```json
{"error":"Došly karty v balíčku.","kod":16,"skupina":3}
```

---

### NEJSI_ADMIN_HRY
**Zpráva:** "$error.nejsi_admin_hry" ("Nejsi administrátor hry.")  
**Kód:** 17  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o administrátorskou akci (např. `novaHraSHracema`, `pridejBota`, `vyhodHrace`) bez práv administrátora hry

**Příklad:**
```json
{"error":"$error.nejsi_admin_hry","kod":17,"skupina":1}
```

---

### PLNY_SERVER
**Zpráva:** "$error.plny_server" ("Server nebo herní místnost je plná.")  
**Kód:** 18  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o připojení k místnosti nebo přidání bota, když je dosažen limit 30 hráčů

**Příklad:**
```json
{"error":"$error.plny_server","kod":18,"skupina":1}
```

---

### VYHOZEN_ZE_HRY
**Zpráva:** "$error.vyhozen_ze_hry" / "$error.vyhozen_ze_hry_adminem" ("Byl jsi vyhozen ze hry administrátorem.")  
**Kód:** 19  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Administrátor hry vyhodil hráče z lobby před začátkem hry (zpráva `vyhodHrace:<id>`)

**Doporučená reakce klienta:** Odstranit uložený token ze sessionStorage/localStorage, informovat hráče a vrátit se na úvodní obrazovku výběru hry.

**Příklad:**
```json
{"error":"$error.vyhozen_ze_hry_adminem:{\"admin\":\"Honza\"}","kod":19,"skupina":1}
```

---

### HRA_UZ_ZAHAJENA
**Zpráva:** "$error.hra_uz_zahajena" ("Tato hra již byla zahájena.")  
**Kód:** 20  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o přidání bota nebo změnu parametrů v místnosti, kde hra již běží

**Příklad:**
```json
{"error":"$error.hra_uz_zahajena","kod":20,"skupina":1}
```

---

### HRA_NEPODPORUJE
**Zpráva:** "$error.hra_nepodporuje" / "$error.hra_nepodporuje_boty" ("Tato hra tuto funkci nepodporuje.")  
**Kód:** 21  
**Skupina:** 1 (špatný formát zprávy)

**Kdy nastane:** Pokus o akci, kterou vybraný herní plugin nepodporuje (např. přidání bota do hry, jejíž plugin boty nepodporuje)

**Příklad:**
```json
{"error":"$error.hra_nepodporuje_boty","kod":21,"skupina":1}
```

---

### PRIPOJEN_V_JINE_ZALOZCE
**Zpráva:** "$error.pripojen_v_jine_zalozce" ("Připojen v jiné záložce.")  
**Kód:** 22  
**Skupina:** 2 (chybějící data / relace)

**Kdy nastane:** Hráč se se stejným tokenem znovupřipojil v jiné záložce či okně prohlížeče. Server tuto chybu zašle původnímu připojení těsně před tím, než jej odpojí.

**Doporučená reakce klienta:**
- **Okamžitě zablokovat automatický reconnect!** (jinak nastane nekonečná smyčka odpojování mezi záložkami).
- Zobrazit modální dialog s upozorněním: "Hra je otevřena v jiné záložce. Chceš přepnout hru sem?"
- Tlačítko "Přepnout hru sem" teprve vyvolá ruční znovupřipojení (`vraceniSe:<token>`).

**Příklad:**
```json
{"error":"$error.pripojen_v_jine_zalozce","kod":22,"skupina":2}
```

---

## Chyby bez kódu a skupiny

Následující chyby jsou posílány pouze se zprávou, bez kódu a skupiny:

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
**Kdy nastane:** Pokus o herní akci bez připojení ke hře (varianta z SocketServer.java, bez tečky na konci)

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
       handleErrorByCode(errorData.kod, errorData["skupina"], errorData.error);
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

4. **Přistupujte k poli skupina:**
   ```javascript
   const skupina = errorData["skupina"];
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
  
  // Získat překlad chyby (pokud začíná $)
  const zprava = translateServerText(errorData.error);
  
  // Rozhodnout podle kódu nebo zprávy
  if (errorData.kod !== undefined) {
    const kod = errorData.kod;
    const skupina = errorData["skupina"];
    
    // Připojen v jiné záložce - zastavit reconnect smyčku!
    if (kod === 22) {
      stopAutoReconnect();
      showConfirmDialog({
        title: "Připojen v jiné záložce",
        message: "Hra je otevřena v jiné záložce. Chceš přepnout hru sem?",
        confirmText: "Přepnout hru sem",
        onConfirm: () => manualReconnect()
      });
      return;
    }

    // Vyhozen ze hry administrátorem
    if (kod === 19) {
      localStorage.removeItem("gameToken");
      sessionStorage.removeItem("gameToken");
      toast.error(zprava);
      navigateToLobby();
      return;
    }
    
    // Kritické chyby - vrátit na lobby
    if (kod === 1 || kod === 5) {
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

| Kód | Název konstanty | Lokalizační klíč | Výchozí zpráva | Skupina |
|-----|----------------|------------------|----------------|---------|
| 1 | `NEPRIPOJEN_KE_HRE` | `$error.nepripojen_ke_hre` | Nejsi připojen ke hře. | 2 |
| 2 | `KARTA_NEEXISTUJE` | `$error.karta_neexistuje` | Tato karta neexistuje | 2 |
| 3 | `KARTA_NENI_HRATELNA` | `$error.karta_neni_hratelna` | Tato karta není hratelná. | 1 |
| 4 | `KARTA_NEJDE_ZAHRAT` | `$error.karta_nejde_zahrat` | Tuto kartu ted nemuzes zahrat. | 3 |
| 5 | `HRA_NEEXISTUJE` | `$error.hra_neexistuje` | Hra, ke které se snažíš připojit neexistuje. | 1 |
| 6 | `POSTAVA_NENI_NA_VYBER` | `$error.postava_neni_na_vyber` | Postava není na výběr | 1 |
| 7 | `NEJSI_NA_TAHU` | `$error.nejsi_na_tahu` | Nejsi na tahu. | 3 |
| 8 | `NEMUZES_UKONCIT_TAH` | `$error.nemuzes_ukoncit_tah` | Takhle tah ukončit nejde. | 3 |
| 9 | `KARTA_NEJDE_SPALIT` | `$error.karta_nejde_spalit` | Tuhle kartu bohužel nemůžeš spálit. | 1 |
| 10 | `NENI_VYLOZITELNA` | `$error.neni_vylozitelna` | Tahle karta není vyložitelná. | 1 |
| 11 | `KARTU_NEJDE_VYLOZIT` | `$error.kartu_nejde_vylozit` | Tuhle kartu teď nemůžeš vyložit. | 3 |
| 12 | `CHYBA_PROTOKOLU` | `$error.chyba_protokolu` | Nastala chyba při komunikaci. | 1 |
| 13 | `NEJDE_SI_LIZNOUT` | `$error.nejde_si_liznout` | Teď si nemůžeš líznout. | 3 |
| 14 | `SPATNE_HESLO` | `$error.spatne_heslo` | Špatné heslo. | 1 |
| 15 | `UZ_PRIPOJEN` | `$error.uz_pripojen` | Už jsi připojen ke hře. | 2 |
| 16 | `DOSLI_KARTY_V_BALICKU` | `$error.dosli_karty_v_balicku` | Došly karty v balíčku. | 3 |
| 17 | `NEJSI_ADMIN_HRY` | `$error.nejsi_admin_hry` | Nejsi administrátor hry. | 1 |
| 18 | `PLNY_SERVER` | `$error.plny_server` | Server nebo herní místnost je plná. | 1 |
| 19 | `VYHOZEN_ZE_HRY` | `$error.vyhozen_ze_hry` | Byl jsi vyhozen ze hry administrátorem. | 1 |
| 20 | `HRA_UZ_ZAHAJENA` | `$error.hra_uz_zahajena` | Tato hra již byla zahájena. | 1 |
| 21 | `HRA_NEPODPORUJE` | `$error.hra_nepodporuje` | Tato hra tuto funkci nepodporuje. | 1 |
| 22 | `PRIPOJEN_V_JINE_ZALOZCE` | `$error.pripojen_v_jine_zalozce` | Připojen v jiné záložce. | 2 |

## Poznámky k implementaci

### Známé vlastnosti

1. **Dva různé formáty:** Některé chyby mají kód a skupinu, jiné ne. Klient musí být připraven na oba formáty.

2. **Lokalizace chyb:** Všechny chyby z `Chyba.java` v poli `error` vracejí lokalizační klíč začínající `$error.` (např. `$error.pripojen_v_jine_zalozce`), případně doplněný o JSON parametry (např. `$error.vyhozen_ze_hry_adminem:{"admin":"Honza"}`). Klient je překládá pomocí svého lokalizačního slovníku.

3. **Duplicitní zprávy:** Některé chyby existují ve dvou variantách - jedna s kódem (z Chyba.java) a jedna bez (ze SocketServer.java). Například "Nejsi připojen ke hře" (bez kódu, ze SocketServer.java) vs "$error.nepripojen_ke_hre" (s kódem 1, z Chyba.java).

## Související dokumentace

- [index.md](index.md) - Kompletní reference protokolu
- [behem-hry.md](behem-hry.md) - Herní fáze a možné chyby
- [predHrou.md](predHrou.md) - Fáze před hrou a možné chyby
- `plugin-sdk/src/main/java/cz/honzaa/bang/sdk/Chyba.java` - Zdrojový kód definic chyb
