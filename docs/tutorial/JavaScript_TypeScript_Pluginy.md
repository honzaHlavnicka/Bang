# Vývoj JavaScript a TypeScript pluginů

Tento návod vás provede procesem tvorby vlastního pluginu pro herní engine v **JavaScriptu** nebo **TypeScriptu**. 

Engine využívá GraalVM Polyglot k bezpečnému spouštění JS/TS kódu přímo v sandboxu na serveru.

---

## I. Struktura pluginu

Každý JavaScriptový plugin musí být umístěn ve své vlastní podsložce v adresáři `pluginy/` (např. `pluginy/moje-hra-js/`) a musí obsahovat minimálně dva soubory:

1. **`plugin.json`** – Soubor s metadaty pluginu.
2. **Hlavní skript** (např. `main.js` nebo zkompilovaný `dist/main.js`) – Soubor obsahující logiku hry.

### Ukázka `plugin.json`
```json
{
  "nazev": "Prší JS",
  "popis": "Tradiční česká karetní hra Prší implementovaná v JavaScriptu.",
  "autor": "Vývojář",
  "verze": "1.0.0",
  "jazyk": "js",
  "spousteciSoubor": "main.js"
}
```

* **`nazev`**: Název hry zobrazený v nabídce.
* **`jazyk`**: Musí být `"js"`.
* **`spousteciSoubor`**: Relativní cesta k hlavnímu JavaScriptovému souboru.

---

## II. Hlavní objekt `PravidlaPluginu`

Hlavní spouštěcí soubor musí v globálním prostoru nadefinovat objekt jménem **`PravidlaPluginu`**. Tento objekt funguje jako adaptér na rozhraní `HerniPravidla` v Javě a slouží k řízení životního cyklu hry.

### Základní životní cyklus v JS
```javascript
const PravidlaPluginu = {
    // Vrátí pole karet, které se mají zamíchat do balíčku
    getKartyDoBalicku: function() {
        return [
            vytvorKartu("CERVENE", "ESO"),
            vytvorKartu("ZELENE", "SEDMA")
        ];
    },

    // Příprava hráčů na začátku hry (např. rozdání karet)
    pripravitHrace: function(hra, hrac) {
        for (let i = 0; i < 4; i++) {
            hrac.lizni();
        }
    },

    // Akce po spuštění hry (např. otočení první karty na stůl)
    poSpusteniHry: function(hra) {
        hra.otocVrchniKartu();
    },

    // Reakce na odehrání tahu hráčem
    poOdehrani: function(hra, kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
    },

    // Volitelné: zda si hráč může líznout kartu
    hracChceLiznout: function(hra, hrac) {
        if (hrac.jeNaTahu()) {
            hrac.lizni();
            hra.getSpravceTahu().dalsiHracSUpozornenim();
            return true;
        }
        return false;
    }
};
```

---

## III. Implementace karet a efektů

Karta v JavaScriptu je běžný objekt, který se na základě přítomnosti určitých metod automaticky mapuje na příslušný typ Java karty:
* **Hratelná karta** – obsahuje metodu `odehrat(hra, kym)`
* **Vyložitelná karta** – obsahuje metodu `vylozit(predKoho, kym)` a případně implementuje metody rozhraní `Efekt` (např. `getBonusDosahu`, `naZacatekTahu` apod.)

### Příklad tvorby karty:
```javascript
function vytvorKartu(barva, hodnota) {
    return {
        barva: barva,
        hodnota: hodnota,

        getJmeno: function() {
            return "Karta " + hodnota + " " + barva;
        },
        
        getObrazek: function() {
            return "cesta_k_obrazku";
        },

        // Hratelná karta: volá se při zahrání z ruky
        odehrat: function(hra, kym) {
            hra.getKomunikator().posliStavovouZpravu(kym.getJmeno() + " zahrál " + this.getJmeno());
            return true; // true = karta byla úspěšně odehrána
        }
    };
}
```

---

## IV. Vývoj v TypeScriptu

Pro usnadnění vývoje, našeptávání (IntelliSense) a typovou kontrolu doporučujeme psát pluginy v TypeScriptu.

### 1. Stažení typových definic (SDK Types)
Typové definice pro kompletní API enginu jsou připraveny v souboru:
* **[bang-sdk.d.ts](../types/bang-sdk.d.ts)**

Tento soubor zkopírujte do svého projektu (např. do složky `src/` nebo `types/`).

### 2. Konfigurace projektu (`tsconfig.json`)
Vytvořte ve složce svého pluginu soubor `tsconfig.json` s následující konfigurací:

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "None",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "outDir": "./dist"
  },
  "include": ["src/**/*", "types/**/*"]
}
```
> [!IMPORTANT]
> GraalVM sandbox spouští JS v globálním režimu a nepoužívá standardní ES moduly (`import`/`export`) bez dodatečného nastavení bundleru. Proto nastavujeme `"module": "None"`. TypeScript pak zkompiluje všechny soubory do jednoho nebo zachová globální binding.

### 3. Příklad kódu v TypeScriptu (`src/main.ts`)
```typescript
/// <reference path="../types/bang-sdk.d.ts" />

const MojeKarta: JSKarta = {
    getJmeno() {
        return "TS Super Karta";
    },
    getObrazek() {
        return "super_karta";
    },
    odehrat(hra: Hra, kym: Hrac): boolean {
        hra.getKomunikator().posliRychleOznameni("Ahoj z TypeScriptu!", kym);
        return true;
    }
};

// Globální objekt PravidlaPluginu musí odpovídat rozhraní z bang-sdk.d.ts
(globalThis as any).PravidlaPluginu = {
    getKartyDoBalicku() {
        return [MojeKarta];
    },
    pripravitHrace(hra: Hra, hrac: Hrac) {
        hrac.lizni();
    },
    poSpusteniHry(hra: Hra) {
        hra.getKomunikator().posliStavovouZpravu("TS hra zahájena!");
    },
    poOdehrani(hra: Hra, kym: Hrac) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
    }
} as PravidlaPluginu;
```

---

## V. Jak TypeScript zkompilovat

Aby engine mohl TypeScript kód spustit, musíte jej nejprve přeložit (zkompilovat) do čistého JavaScriptu.

### Krok 1: Instalace TypeScript compileru
Pokud ještě nemáte TypeScript nainstalovaný globálně nebo v projektu, nainstalujte jej pomocí Node.js (npm):

```bash
# Globální instalace
npm install -g typescript

# Nebo lokální instalace do vývojářských závislostí
npm install --save-dev typescript
```

### Krok 2: Spuštění kompilace
Spusťte kompilátor `tsc` v kořenovém adresáři vašeho projektu (tam, kde je umístěn soubor `tsconfig.json`):

```bash
# Pro jednorázovou kompilaci
tsc

# Pro automatickou kompilaci při každé změně (Watch mode)
tsc -w
```

Zkompilované soubory se vygenerují do složky definované v `tsconfig.json` pod klíčem `"outDir"`, v našem případě do složky **`dist/`** (např. `dist/main.js`).

### Krok 3: Úprava `plugin.json`
V souboru `plugin.json` musíte odkázat na zkompilovaný JavaScriptový soubor:

```json
{
  "nazev": "Můj TS Plugin",
  "spousteciSoubor": "dist/main.js",
  "jazyk": "js"
  ...
}
```

Následně stačí celou složku vašeho pluginu (např. `pluginy/muj-ts-plugin/`) zkopírovat do složky `pluginy` na serveru a spustit hru!
