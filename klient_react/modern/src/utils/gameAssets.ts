/**
 * Pomocné funkce a mapování pro herní assety Bang! a obecných karetních her.
 * Obsahuje mapování rolí, vějířů karet z rubu a nábojů / životů.
 */

const ROLE_MAP: Record<string, string> = {
  serif: "SERIF",
  "šerif": "SERIF",
  sheriff: "SERIF",
  bandita: "BANDITA",
  bandit: "BANDITA",
  outlaw: "BANDITA",
  pomocnik: "POMOCNIK",
  "pomocník": "POMOCNIK",
  deputy: "POMOCNIK",
  odpadlik: "ODPADLIK",
  "odpadlík": "ODPADLIK",
  renegade: "ODPADLIK",
};

export function getRoleImagePath(role?: string | null): string | null {
  if (!role || typeof role !== "string") return null;
  const clean = role.trim().toLowerCase();
  if (
    clean === "undefined" ||
    clean === "neví se" ||
    clean === "nevi se" ||
    clean === "unknown" ||
    clean === ""
  ) {
    return null;
  }
  const mapped = ROLE_MAP[clean];
  if (mapped) {
    return `/img/karty/role/${mapped}.png`;
  }
  const upper = role.trim().toUpperCase();
  if (["SERIF", "BANDITA", "POMOCNIK", "ODPADLIK"].includes(upper)) {
    return `/img/karty/role/${upper}.png`;
  }
  return null;
}

export function getCardFanImagePath(count: number): string | null {
  if (count <= 0) return null;
  const clamped = Math.min(Math.max(1, count), 8);
  return `/img/maleKarty/${clamped}.png`;
}

export const BULLET_IMAGE_PATH = "/img/bangNaboj.png";
export const CARD_BACK_IMAGE_PATH = "/img/karty/zezadu.png";
