import { describe, it, expect } from "vitest";
import {
  getRoleImagePath,
  getCardFanImagePath,
  BULLET_IMAGE_PATH,
  CARD_BACK_IMAGE_PATH,
} from "../utils/gameAssets";

describe("gameAssets utility", () => {
  it("resolves role image paths for all standard Bang! roles", () => {
    expect(getRoleImagePath("SERIF")).toBe("/img/karty/role/SERIF.png");
    expect(getRoleImagePath("šerif")).toBe("/img/karty/role/SERIF.png");
    expect(getRoleImagePath("sheriff")).toBe("/img/karty/role/SERIF.png");

    expect(getRoleImagePath("BANDITA")).toBe("/img/karty/role/BANDITA.png");
    expect(getRoleImagePath("outlaw")).toBe("/img/karty/role/BANDITA.png");

    expect(getRoleImagePath("POMOCNIK")).toBe("/img/karty/role/POMOCNIK.png");
    expect(getRoleImagePath("pomocník")).toBe("/img/karty/role/POMOCNIK.png");
    expect(getRoleImagePath("deputy")).toBe("/img/karty/role/POMOCNIK.png");

    expect(getRoleImagePath("ODPADLIK")).toBe("/img/karty/role/ODPADLIK.png");
    expect(getRoleImagePath("odpadlík")).toBe("/img/karty/role/ODPADLIK.png");
    expect(getRoleImagePath("renegade")).toBe("/img/karty/role/ODPADLIK.png");
  });

  it("returns null for hidden, unknown, or undefined roles", () => {
    expect(getRoleImagePath("neví se")).toBeNull();
    expect(getRoleImagePath("nevi se")).toBeNull();
    expect(getRoleImagePath("undefined")).toBeNull();
    expect(getRoleImagePath("unknown")).toBeNull();
    expect(getRoleImagePath("")).toBeNull();
    expect(getRoleImagePath(null)).toBeNull();
    expect(getRoleImagePath(undefined)).toBeNull();
  });

  it("returns correct card fan images", () => {
    expect(getCardFanImagePath(0)).toBeNull();
    expect(getCardFanImagePath(-1)).toBeNull();
    expect(getCardFanImagePath(1)).toBe("/img/maleKarty/1.png");
    expect(getCardFanImagePath(4)).toBe("/img/maleKarty/4.png");
    expect(getCardFanImagePath(8)).toBe("/img/maleKarty/8.png");
    expect(getCardFanImagePath(15)).toBe("/img/maleKarty/8.png"); // clamped to 8
  });

  it("provides valid constant paths for bullets and card backs", () => {
    expect(BULLET_IMAGE_PATH).toBe("/img/bangNaboj.png");
    expect(CARD_BACK_IMAGE_PATH).toBe("/img/karty/zezadu.png");
  });
});
