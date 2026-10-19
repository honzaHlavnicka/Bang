import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { LobbyPage } from '../pages/LobbyPage';
import { GameContext, gameStateDefault } from '@bang/core';
import { setLanguage } from '../i18n';

const createMockGameContext = (overrides = {}) => ({
  gameState: {
    ...gameStateDefault,
    isTokenValid: false,
    gameTypesAvailable: [
      { id: 0, name: 'Bang!', description: 'Klasická přestřelka', url: '' },
      { id: 3, name: 'Prší', description: 'Česká klasika', url: '' },
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

describe('LobbyPage component', () => {
  it('renders title, emblem and language switcher', () => {
    setLanguage('cs');
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <LobbyPage />
      </GameContext.Provider>
    );

    expect(screen.getByText('Karetní hry')).toBeDefined();
    expect(screen.getByText('CZ')).toBeDefined();
    expect(screen.getByText('EN')).toBeDefined();
  });

  it('switches between Join and Create tabs', () => {
    setLanguage('cs');
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <LobbyPage />
      </GameContext.Provider>
    );

    // Default tab is Join
    expect(screen.getByText('Kód hry')).toBeDefined();

    // Click Create tab
    const createTabBtn = screen.getByText('Založit novou hru');
    fireEvent.click(createTabBtn);

    expect(screen.getByText('Vyber hru')).toBeDefined();
    expect(screen.getByText('Vytvořit hru')).toBeDefined();
  });

  it('shows return to game banner when isTokenValid is true', () => {
    setLanguage('cs');
    const mockCtx = createMockGameContext({ isTokenValid: true });
    render(
      <GameContext.Provider value={mockCtx}>
        <LobbyPage />
      </GameContext.Provider>
    );

    const returnBtn = screen.getByText('Vrátit se do hry');
    expect(returnBtn).toBeDefined();

    fireEvent.click(returnBtn);
    expect(mockCtx.returnToGame).toHaveBeenCalled();
  });

  it('allows language switching via language switcher buttons', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <LobbyPage />
      </GameContext.Provider>
    );

    // Switch to EN
    const enBtn = screen.getByText('EN');
    fireEvent.click(enBtn);

    // Should now display english text
    expect(screen.getByText('Card Games')).toBeDefined();
    expect(screen.getAllByText('Join Game').length).toBeGreaterThan(0);

    // Switch back to CZ
    const czBtn = screen.getByText('CZ');
    fireEvent.click(czBtn);

    expect(screen.getByText('Karetní hry')).toBeDefined();
    expect(screen.getAllByText('Vstoupit do hry').length).toBeGreaterThan(0);
  });
});
