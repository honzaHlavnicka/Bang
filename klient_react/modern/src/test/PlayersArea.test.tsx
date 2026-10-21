import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { PlayersArea } from '../components/PlayersArea';
import { DragProvider } from '../components/DragContext';
import { GameContext, DialogContext, gameStateDefault } from '@bang/core';

const mockDialogContext = {
  dialog: null,
  openDialog: vi.fn(),
  closeDialog: vi.fn(),
};

const createMockGameContext = (overrides = {}): any => ({
  gameState: {
    ...gameStateDefault,
    playerId: 0,
    players: [
      { id: 0, name: 'Honza (Ja)', role: '', isCurrentTurn: false, health: 4, cardsInHand: 4, inPlayCards: [] },
      { id: 1, name: 'Petr', character: 'Bart Cassidy', role: '', isCurrentTurn: false, health: 0, cardsInHand: 2, inPlayCards: [] },
    ],
    allowedUIElements: ['ZIVOTY', 'POSTAVA'],
    ...overrides,
  },
  isConnected: true,
  connectToGame: vi.fn(),
  changePlayerName: vi.fn(),
  startGame: vi.fn(),
  endTurn: vi.fn(),
  chooseCharacter: vi.fn(),
  createGame: vi.fn(),
  drawCard: vi.fn(),
  playCard: vi.fn(),
  returnToGame: vi.fn(),
  fireCard: vi.fn(),
  putCardInPlay: vi.fn(),
  putCardInPlayOnPlayer: vi.fn(),
  clickUIButton: vi.fn(),
  startNewGameAndDeleteThisOne: vi.fn(),
  kickPlayer: vi.fn(),
});

describe('PlayersArea component', () => {
  it('marks player as dead when ZIVOTY is allowed and health <= 0', () => {
    const mockCtx = createMockGameContext({
      allowedUIElements: ['ZIVOTY', 'POSTAVA'],
    });
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <PlayersArea />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    expect(screen.getByText('Mrtvý')).toBeDefined();
    expect(screen.getByText('Bart Cassidy')).toBeDefined();
  });

  it('does NOT mark player as dead when ZIVOTY is NOT in allowedUIElements', () => {
    const mockCtx = createMockGameContext({
      allowedUIElements: ['POSTAVA'], // Např. hra bez životů
    });
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <PlayersArea />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    expect(screen.queryByText('Mrtvý')).toBeNull();
  });

  it('does NOT show character name when POSTAVA is NOT in allowedUIElements', () => {
    const mockCtx = createMockGameContext({
      allowedUIElements: ['ZIVOTY'], // Např. Prší / Uno bez postav
    });
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <PlayersArea />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    expect(screen.queryByText('Bart Cassidy')).toBeNull();
  });

  it('provides player dropzone data attribute', () => {
    const mockCtx = createMockGameContext();
    const { container } = render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <PlayersArea />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    const dropZone = container.querySelector('[data-dropzone="player-1"]');
    expect(dropZone).toBeDefined();
  });

  it('opens character inspection modal portalled to document.body when clicking on a player', () => {
    const mockCtx = createMockGameContext({
      allowedUIElements: ['ZIVOTY', 'POSTAVA'],
    });
    const { container } = render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <PlayersArea />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    // Click on Bart Cassidy (character name)
    const characterName = screen.getByText('Bart Cassidy');
    fireEvent.click(characterName);

    // Modal should be opened and present in document.body
    expect(screen.getByText('Hráč: Petr')).toBeDefined();
    // Modal is in document.body, outside of component container
    const dialog = document.body.querySelector('[role="dialog"]');
    expect(dialog).toBeDefined();
    expect(container.contains(dialog)).toBe(false);
  });

  it('renders safely without throwing when used without DragProvider', () => {
    const mockCtx = createMockGameContext();
    expect(() =>
      render(
        <GameContext.Provider value={mockCtx}>
          <DialogContext.Provider value={mockDialogContext}>
            <PlayersArea />
          </DialogContext.Provider>
        </GameContext.Provider>
      )
    ).not.toThrow();

    expect(screen.getByText('Petr')).toBeDefined();
    expect(screen.getByText('Bart Cassidy')).toBeDefined();
  });
});
