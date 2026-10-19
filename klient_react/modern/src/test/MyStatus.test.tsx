import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MyStatus } from '../components/MyStatus';
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
    character: 'Paul Regret',
    role: 'Šerif',
    health: 3,
    maxHealth: 4,
    turnPlayerId: 0,
    inPlayCards: [
      { id: 201, name: 'Barel', image: 'barel', isPlayable: false, isPutInPlayable: false },
      { id: 202, name: 'Dynamit', image: 'dynamit', isPlayable: true, isPutInPlayable: false },
    ],
    allowedUIElements: ['ZIVOTY', 'POSTAVA', 'ROLE', 'VYLOZENE_KARTY', 'OHEN'],
    characters: [
      { jmeno: 'Paul Regret', obrazek: 'paul_regret', popis: 'Všichni hráči ho vidí na vzdálenost o 1 větší.' },
    ],
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

describe('MyStatus component', () => {
  it('renders character, role, health, and in-play cards', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <MyStatus />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    expect(screen.getByText('Paul Regret')).toBeDefined();
    expect(screen.getByText('Šerif')).toBeDefined();
    expect(screen.getByText('3 / 4')).toBeDefined();
    expect(screen.getByText('Tvůj tah')).toBeDefined();
  });

  it('clicking character opens detail modal with ability description', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <MyStatus />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    fireEvent.click(screen.getByText('Paul Regret'));
    expect(screen.getByText('Všichni hráči ho vidí na vzdálenost o 1 větší.')).toBeDefined();
    expect(screen.getByText('Rozumím')).toBeDefined();
  });

  it('clicking an in-play card opens action modal and allows burning it', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DialogContext.Provider value={mockDialogContext}>
          <DragProvider>
            <MyStatus />
          </DragProvider>
        </DialogContext.Provider>
      </GameContext.Provider>
    );

    // Click on the in-play card container for Barel
    const barelCard = screen.getByTitle(/Barel \(klikni pro akce nebo přetáhni\)/i);
    expect(barelCard).toBeDefined();
    fireEvent.click(barelCard);

    // Check modal appears
    const burnBtn = screen.getByText('Spálit tuto vyloženou kartu');
    expect(burnBtn).toBeDefined();

    // Click burn
    fireEvent.click(burnBtn);
    expect(mockCtx.fireCard).toHaveBeenCalledWith(201);
  });
});
