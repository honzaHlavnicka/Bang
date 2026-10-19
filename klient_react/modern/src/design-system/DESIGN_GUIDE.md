# Návod stylu a Design systém (Bang! Modern)

Tento dokument slouží jako **závazná příručka pro veškerý vizuální a interakční styl** nového klienta. Každá nová komponenta, tlačítko či stránka musí respektovat tyto pravidla, aby aplikace působila uceleně, čistě a přirozeně.

---

## 1. Filozofie a charakter rozhraní

- **Přátelský a komunitní**: Působí jako příjemný večer u deskové hry s přáteli. Žádný korporátní chlad, žádné sterilní šedé SaaS tabulky.
- **Důstojný, bez trapných výplní**: Má svou osobnost díky barvám a typografii, ale nepokouší se o žádné nucené vtipné hlášky, maskoty ani trapný humor.
- **Účelný a nepřeplácaný (Zero visual clutter)**: Každý pixel a každý element na obrazovce musí mít jasný funkční důvod. Žádná zbytečná dekorativní hejblátka, co berou pozornost od karet.
- **Matný, hmatatelný, přírodní**: Vzhled inspirovaný dřevem, plátnem, kůží a matným karetním papírem. Žádný lesklý plast, žádný neonový kyberpunk ani přehnané gradienty.

---

## 2. Barevná paleta a sémantické tokeny

Rozhraní je vyladěno do teplých zemitých tónů (tmavé ořechové a uhlíkové pozadí, medová a jantarová žluť pro akce, šalvějová zeleň pro pozitivní stavy a terakota pro spalování/nebezpečí).

| Sémantický název | Kód barvy | Použití |
| :--- | :--- | :--- |
| **Canvas (Pozadí)** | `#161311` | Hlavní podklad herní plochy (tmavý espresso ořech, neoslňuje) |
| **Surface (Panely)** | `#221c18` | Plochy panelů, lišt a základen |
| **Surface Raised** | `#2d2520` | Vyvýšené karty hráčů, vyskakovací okna, dialogy |
| **Surface Hover** | `#3a302a` | Zvýraznění při najetí myší |
| **Border Subtle** | `#382f28` | Jemné oddělovače |
| **Border Active** | `#54463c` | Aktivní rámečky, focus linky |
| **Text Main** | `#f5ede3` | Hlavní text (teplá slonová kost, vysoký kontrast bez pálení očí) |
| **Text Muted** | `#b5a596` | Doplňkový text, nápovědy, štítky |
| **Text Subtle** | `#7a6c60` | Čísla klávesových zkratek, neaktivní prvky |
| **Accent Amber (Primární)** | `#d97706` / `#f59e0b` | Hlavní tlačítka, hratelné karty, důležitá upozornění |
| **Accent Sage (Pozitivní/Tah)**| `#3f6b4e` / `#508462` | Váš tah, životy, úspěšná potvrzení, online stav |
| **Accent Terracotta (Oheň/Chyba)**| `#b33f2b` / `#cb4b35` | Spalování karet, dropzone ohniště, smazání hry, smrt |

---

## 3. Typografie a písmo

Používáme čistý, čitelný humanistický font bez patiček (systémový nativní stack, který se načte ihned a bez prodlevy):
`font-sans: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif`

- **Číselné hodnoty**: Vždy používáme `tabular-nums` pro životy, počty karet a časomíry, aby čísla neposkakovala při změnách.
- **Škála velikostí**:
  - `text-xs` (11px): Klávesové zkratky, malé odznaky (badges).
  - `text-sm` (13px): Popisky tlačítek na mobilu, doplňkové informace o postavě.
  - `text-base` (15px): Základní text, běžná tlačítka, dialogy.
  - `text-lg` (18px): Jména hráčů, nadpisy panelů.
  - `text-xl` až `text-2xl` (22-28px): Hlavní stav hry, vítězná obrazovka.

---

## 4. Zaoblení (Radii) & Stíny

- **Karty**: `rounded-xl` (10-12px) s jemným 1px ohraničením `border-[#382f28]`.
- **Tlačítka a vstupy**: `rounded-xl` (10-12px) pro přátelský, měkký pocit na stisk.
- **Odznaky a čítače**: `rounded-md` (6px) nebo plně kulaté `rounded-full`.
- **Dialogy a karty hráčů**: `rounded-2xl` (16px).
- **Stíny**: Matné, ambientní, měkké: `shadow-md` a `shadow-lg` v barvě `rgba(0, 0, 0, 0.4)`. Žádné ostré černé stíny ani barevné neony.

---

## 5. Pravidla pro komponenty (DRY)

1. **Tlačítka (`Button.tsx`)**:
   - `variant="primary"`: Sytá teplá jantarová barva. Určeno pro hlavní akci (Ukončit tah, Vstoupit do hry).
   - `variant="secondary"`: Tmavý ořech s teplým okrajem. Pro doplňkové akce.
   - `variant="terracotta"`: Ohnivá cihlová. Pro spalování karet a rizikové akce.
   - `variant="ghost"`: Bez pozadí, pouze hover efekt.
   - Každé tlačítko má jemný efekt stisku (`active:scale-[0.98]`) a jasně definovaný focus ring pro klávesnici (`focus-visible:ring-2 focus-visible:ring-amber-500`).
2. **Karetní prvek (`CardView.tsx`)**:
   - Respektuje poměr stran karet (~1 : 1.45).
   - Pokud je karta hratelná (`isPlayable`), má jemný teplý rámeček a reaguje na hover mírným povytažením nahoru.
   - Pokud není hratelná, nezvedá se zbytečně, aby hráč okamžitě podvědomě věděl, co může použít.
3. **Zóny pro Drag & Drop (`DropZone.tsx`)**:
   - Stůl (zahraní karty), Ohniště (spálení karty), Hráč (zahrání karty na cizí postavu).
   - Při přetažení karty nad zónu se zóna jemně rozsvítí a pulzuje.

---

## 6. Mobilní UX & Drag-and-Drop pravidla

- **Výzva**: Na telefonu je potřeba mít vějíř karet, který se posouvá do strany, a zároveň umožnit tažení karet ven.
- **Řešení**:
  1. **Směrová detekce**: Vodorovný tah scrolluje lištu karet. Svislý tah vzhůru zahájí přetažení karty na stůl / do ohně.
  2. **Alternativa jedním klepnutím (Tap to Action)**:
     - Klepnutím na kartu v ruce se karta vysune a přímo nad ní se zobrazí miniaturní akční lišta: `[Zahrát]` `[Vyložit]` `[Spálit 🔥]`.
     - Hráč tak může pohodlně hrát i na malém 360px telefonu v jedné ruce v tramvaji bez nutnosti složitého drag & dropu.

---

## 7. Podpora více her (`allowedUIElements`)

Klient je plně univerzální pro všechny hry podporované jádrem:
- `ZIVOTY`: Životy hráče.
- `UKONCENI_TAHU`: Tlačítko ukončení tahu (zvýrazněné, když je hráč na řadě).
- `POSTAVA`: Karta postavy a její speciální schopnost.
- `ROLE`: Tajná nebo veřejná role.
- `VYLOZENE_KARTY`: Zbraně, modré karty, vyložené věci před hráčem.
- `ODHAZOVACI_BALICEK`: Odkládací balíček s vrchní kartou.
- `DOBIRACI_BALICEK`: Líznutí karty.
- `OHEN`: Ohniště / spalování karet (`fireCard`).
- `customUIButtons`: Dynamická tlačítka generovaná serverem (např. schopnost Sida Ketchuma, výběr barev v Prší, UNO).

---

## 8. Klávesové zkratky a psychologie ovládání

- `Mezerník` / `Enter`: Ukončení tahu (když je hráč na řadě).
- `1` až `9`: Rychlý výběr karty z ruky.
- `D`: Lízat kartu z balíčku.
- `F` / `Delete`: Spálit vybranou kartu v ohni.
- `Esc`: Zrušit výběr karty nebo zavřít dialog.
