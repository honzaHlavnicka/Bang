import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handleGameMessage, translateServerText } from '../modules/gameActions';
import { gameStateDefault, type GameStateType } from '../modules/GameContext';
import type { RefObject } from 'react';

describe('gameActions - translateServerText', () => {
  it('vrací běžný text beze změny', () => {
    expect(translateServerText('Ahoj')).toBe('Ahoj');
    expect(translateServerText('')).toBe('');
    expect(translateServerText(null as any)).toBe(null);
  });

  it('zpracovává text začínající na $ jako překladový klíč', () => {
    const result = translateServerText('$common.unknown_key_xyz');
    expect(result).toBe('common.unknown_key_xyz');
  });

  it('zpracovává parametry v JSON formátu za dvojtečkou', () => {
    const result = translateServerText('$nonexistent.key:{"name":"Karel"}');
    expect(result).toBe('nonexistent.key');
  });
});

describe('gameActions - handleGameMessage', () => {
  let state: GameStateType;
  let stateRef: RefObject<GameStateType>;
  let setGameState: (updater: (prev: GameStateType) => GameStateType) => void;
  let openDialog: any;
  let notify: any;
  let socket: any;

  beforeEach(() => {
    state = { ...gameStateDefault };
    stateRef = { current: state };
    setGameState = vi.fn((updater) => {
      state = updater(state);
      stateRef.current = state;
    });
    openDialog = vi.fn();
    notify = vi.fn();
    socket = { send: vi.fn(), readyState: 1 };
  });

  const dispatch = (data: string) => {
    handleGameMessage(
      { data } as MessageEvent,
      setGameState,
      stateRef,
      openDialog,
      socket,
      notify
    );
  };

  it('zpracuje zprávu welcome, nastaví startedConection a pošle infoHer', () => {
    dispatch('welcome');

    expect(stateRef.current?.startedConection).toBe(true);
    expect(socket.send).toHaveBeenCalledWith('infoHer');
  });

  it('zpracuje zprávu infoHer a naplní gameTypesAvailable', () => {
    const infoPayload = JSON.stringify({
      verze: '1.0',
      hry: [{ id: 0, jmeno: 'Bang', popis: 'Divoký západ', url: 'bang.png' }],
    });

    dispatch(`infoHer:${infoPayload}`);

    expect(state.gameTypesAvailable?.length).toBe(1);
    expect(state.gameTypesAvailable?.[0].name).toBe('Bang');
  });

  it('zpracuje zprávu novaHra a nastaví kód hry a stav inGame', () => {
    dispatch('novaHra:4567');

    expect(setGameState).toHaveBeenCalled();
    expect(state.gameCode).toBe('4567');
    expect(state.inGame).toBe(true);
  });

  it('zpracuje zprávu pripojenKeHre pro hráče připojujícího se do hry', () => {
    dispatch('pripojenKeHre');

    expect(setGameState).toHaveBeenCalled();
    expect(state.inGame).toBe(true);
    expect(socket.send).toHaveBeenCalledWith(expect.stringContaining('nactiPreklady'));
  });

  it('zpracuje zprávu noveIdHrace a nastaví playerId', () => {
    dispatch('noveIdHrace:42');

    expect(state.playerId).toBe(42);
  });

  it('zpracuje zprávu tahZacal a aktualizuje turnPlayerId', () => {
    dispatch('tahZacal:3');

    expect(state.turnPlayerId).toBe(3);
  });

  it('zpracuje zprávu tvujTahZacal a nastaví tah pro aktuálního hráče', () => {
    state.playerId = 5;
    stateRef.current = state;

    dispatch('tvujTahZacal');

    expect(state.turnPlayerId).toBe(5);
  });

  it('zpracuje zprávu hraZacala a nastaví gameStarted', () => {
    dispatch('hraZacala');

    expect(state.gameStarted).toBe(true);
  });

  it('zpracuje zprávu novaKarta a přidá kartu do ruky handCards', () => {
    const cardJson = JSON.stringify({
      id: 10,
      jmeno: 'Bang!',
      obrazek: 'bang',
      hratelna: true,
      vylozitelna: false,
    });

    dispatch(`novaKarta:${cardJson}`);

    expect(state.handCards?.length).toBe(1);
    expect(state.handCards?.[0].id).toBe(10);
    expect(state.handCards?.[0].name).toBe('Bang!');
    expect(state.handCards?.[0].isPlayable).toBe(true);
  });

  it('zpracuje zprávu pocetZivotu a aktualizuje životy daného hráče', () => {
    state.players = [
      { id: 1, name: 'Hrac 1', health: 4, role: 'SERIF', cardsInHand: 4, inPlayCards: [] } as any,
      { id: 2, name: 'Hrac 2', health: 3, role: 'BANDITA', cardsInHand: 3, inPlayCards: [] } as any,
    ];
    stateRef.current = state;

    dispatch('pocetZivotu:1,2'); // Hráč 1 klesne na 2 životy

    const p1 = state.players?.find((p) => p.id === 1);
    expect(p1?.health).toBe(2);
  });

  it('zpracuje zprávu konecHry a nastaví gameEnded', () => {
    dispatch('konecHry');

    expect(state.gameEnded).toBe(true);
  });

  it('zpracuje zprávu koloStesti a otevře dialog kola štěstí', () => {
    const rouletteData = JSON.stringify({
      moznosti: [{ text: 'Zásah' }, { text: 'Minutí' }],
      vybranaMoznost: 0,
    });

    dispatch(`koloStesti:${rouletteData}`);

    expect(openDialog).toHaveBeenCalled();
  });

  it('odolnost: nevalidní zpráva nebo neplatný JSON nezpůsobí pád aplikace', () => {
    expect(() => {
      dispatch('novaKarta:INVALID_JSON_CORRUPTED');
      dispatch('pocetZivotu:NEPLATNE');
      dispatch('neznamaZprava:1234');
      dispatch('');
    }).not.toThrow();
  });
});
