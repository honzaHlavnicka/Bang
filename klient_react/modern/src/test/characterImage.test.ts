import { describe, it, expect } from 'vitest';
import { getCharacterImagePath } from '../utils/characterImage';

describe('getCharacterImagePath utility', () => {
  it('resolves standard camelCase and uppercase Bang characters accurately', () => {
    expect(getCharacterImagePath('bartCassidy')).toBe('/img/karty/postavy/bartCassidy.png');
    expect(getCharacterImagePath('paulRegret')).toBe('/img/karty/postavy/paulRegret.png');
    expect(getCharacterImagePath('kitCarlson')).toBe('/img/karty/postavy/kitCarlson.png');
    expect(getCharacterImagePath('roseDoolan')).toBe('/img/karty/postavy/roseDoolan.png');
    expect(getCharacterImagePath('jourdonnais')).toBe('/img/karty/postavy/jourdonnais.png');
    expect(getCharacterImagePath('sidketchum')).toBe('/img/karty/postavy/sidketchum.png');
    expect(getCharacterImagePath('SUZY_LAFAYTTE')).toBe('/img/karty/postavy/SUZY_LAFAYTTE.png');
    expect(getCharacterImagePath('VULTURE_SAM')).toBe('/img/karty/postavy/VULTURE_SAM.png');
    expect(getCharacterImagePath('WILLY_THE_KID')).toBe('/img/karty/postavy/WILLY_THE_KID.png');
    expect(getCharacterImagePath('NESMRTELNY_BILL')).toBe('/img/karty/postavy/NESMRTELNY_BILL.png');
  });

  it('resolves human-readable names with spaces and lowercase', () => {
    expect(getCharacterImagePath('Bart Cassidy')).toBe('/img/karty/postavy/bartCassidy.png');
    expect(getCharacterImagePath('Paul Regret')).toBe('/img/karty/postavy/paulRegret.png');
    expect(getCharacterImagePath('bart_cassidy')).toBe('/img/karty/postavy/bartCassidy.png');
    expect(getCharacterImagePath('willy the kid')).toBe('/img/karty/postavy/WILLY_THE_KID.png');
  });

  it('resolves character from gameState.characters list', () => {
    const list = [
      { jmeno: 'Moje Postava', obrazek: 'customCharacter', popis: 'Popis schopnosti' },
    ];
    expect(getCharacterImagePath('Moje Postava', list)).toBe('/img/karty/postavy/customCharacter.png');
    expect(getCharacterImagePath('customCharacter', list)).toBe('/img/karty/postavy/customCharacter.png');
  });

  it('returns fallback for empty or undefined input', () => {
    expect(getCharacterImagePath(undefined)).toBe('/img/postava.png');
    expect(getCharacterImagePath('')).toBe('/img/postava.png');
  });
});
