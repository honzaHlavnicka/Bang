# Návod: Jak vytvořit vlastní hru pro engine

Vytvořte si vlastní hru pro tento engine s využitím programovacího jazyka Java.

Pokud by se vyskytly problémy nebo engine něco nepodporoval, prosím, **vytvořte issue** na [GitHubu](https://github.com/honzaHlavnicka/Bang/issues). Chcete-li svou hru umístit na veřejný server, vytvořte **pull request**.

---

## I. Příprava projektu

1.  **Vytvořte si Maven projekt.**
    * Budete k tomu potřebovat vhodné IDE (např. Netbeans).

2.  **Přidejte `plugin-sdk` jako závislost.**
    * Stáhněte si `plugin-sdk` buď celé z repozitáře, nebo použijte připravený **.jar** soubor.
    * Poté SDK nahrajte jako knihovnu, nebo na něj odkažte v souboru `pom.xml`.

---

## II. Implementace základních tříd

3.  **Implementujte rozhraní `HerniPlugin`.**
    * Pojmenování třídy je libovolné, server si ji najde automaticky.
    * Tato třída musí vracet **jméno** a **popis** vaší hry a umět vytvářet instance hry.
    * **Důležité:** Třída nesmí mít žádný speciální konstruktor.

4.  **Do `HerniPlugin` přidejte `HerniPravidla`.**
    * Tento soubor definuje **logiku hry**, omezuje akce hráčů a určuje, co se jim zobrazuje.

5.  **Vytvořte si karty.**
    * Vytvořte třídy, které budou dědit z abstraktní třídy **`Karta`**.
    * Karty mohou implementovat rozhraní: `HratelnaKarta`, `VylozitelnaKarta`, a/nebo `SpalitelnaKarta`.
    * **Poznámka:** Pokud tato rozhraní neimplementujete, karta nebude mít příslušnou funkčnost. (Karta musí dědit z `Karta` a implementovat potřebná rozhraní).
    * Jedna třída může reprezentovat více karet, ale pro karty s odlišnou logikou je vhodné vytvořit samostatné třídy.

7.  **Vytvořte postavy a role.**
    * **Tento krok zatím není podporován.** Prozatím ho ignorujte.

---

## III. Dokončení a testování

8.  **Dodělejte pravidla hry a sestavte projekt.**
    * **Zajistěte**, aby vaše pravidla vkládala vytvořené karty do balíčku.
    * Sestavte projekt. Výsledný **.jar** soubor nahrajte do složky **`pluginy`** u spuštěného serveru.

9.  **Spusťte a otestujte hru.**
    * Stáhněte a spusťte server.
    * Otevřete prohlížeč na adrese: `honza.svs.gyarab.cz?adress=ws://localhost:port`, kde **`port`** je port vypsaný serverem do konzole.
    * Ověřte, že **vaše hra jde vytvořit**. Otestujte funkčnost a opravte chyby.

---

## IV. Zveřejnění (Volitelné)

10. **Máte hotovo!** Nyní můžete hru hrát.
    * **Lokální síť:** Ostatní se připojí na `honza.svs.gyarab.cz?adress=ws://tvoje lokální IP:port`.
    * **Centrální server (Globální síť):**
        * Vytvořte **Pull Request** na GitHubu.
        * Nahrajte **celý kód**, nikoli pouze `.jar` soubor, pro ověření absence škodlivého obsahu.
    * **Globální síť bez zveřejnění:** Nastudujte si tunelování.
