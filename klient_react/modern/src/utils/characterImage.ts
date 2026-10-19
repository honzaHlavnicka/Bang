/**
 * Normalizace a mapování obrázků postav pro Bang! a jeho rozšíření.
 * Soubory v /img/karty/postavy/ jsou na disku uloženy s přesnou velikostí písmen (Linux je case-sensitive).
 */

const CHARACTER_FILENAME_MAP: Record<string, string> = {
  // CamelCase a UPPERCASE názvy souborů na disku
  bartcassidy: "bartCassidy",
  kitcarlson: "kitCarlson",
  paulregret: "paulRegret",
  rosedoolan: "roseDoolan",
  jourdonnais: "jourdonnais",
  sidketchum: "sidketchum",
  suzylafaytte: "SUZY_LAFAYTTE",
  suzylafayette: "SUZY_LAFAYTTE",
  vulturesam: "VULTURE_SAM",
  willythekid: "WILLY_THE_KID",
  nesmrtelnybill: "NESMRTELNY_BILL",
  testovaci: "TESTOVACI",
  testovaci2: "TESTOVACI2",
};

export function getCharacterImagePath(
  characterNameOrId?: string,
  charactersList?: Array<{ jmeno: string; obrazek: string; popis?: string }> | null
): string {
  if (!characterNameOrId || typeof characterNameOrId !== "string") {
    return "/img/postava.png";
  }

  const trimmed = characterNameOrId.trim();
  if (!trimmed) return "/img/postava.png";

  // 1. Zkusit najít postavu v seznamu postav z herního jádra (obsahuje přesný název obrázku)
  if (charactersList && charactersList.length > 0) {
    const found = charactersList.find(
      (c) =>
        c.obrazek.toLowerCase() === trimmed.toLowerCase() ||
        c.jmeno.toLowerCase() === trimmed.toLowerCase()
    );
    if (found?.obrazek) {
      return `/img/karty/postavy/${found.obrazek}.png`;
    }
  }

  // 2. Normalizace klíče (odstranění mezer, podtržítek a převod na malá písmena pro vyhledání v mapě)
  const normalizedKey = trimmed.toLowerCase().replace(/[^a-z0-9]/g, "");
  if (CHARACTER_FILENAME_MAP[normalizedKey]) {
    return `/img/karty/postavy/${CHARACTER_FILENAME_MAP[normalizedKey]}.png`;
  }

  // 3. Pokud již řetězec odpovídá formátu URL nebo cesty
  if (trimmed.startsWith("/") || trimmed.startsWith("http")) {
    return trimmed;
  }

  // 4. Fallback – přímý pokus s původním jménem
  return `/img/karty/postavy/${trimmed}.png`;
}
