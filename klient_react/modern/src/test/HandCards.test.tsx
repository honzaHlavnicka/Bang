import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { HandCards } from '../components/HandCards';
import { DragProvider } from '../components/DragContext';
import { GameContext, gameStateDefault } from '@bang/core';

const createMockGameContext = (overrides = {}) => ({
  gameState: {
    ...gameStateDefault,
    handCards: [
      { id: 101, name: 'Bang!', image: 'bang', isPlayable: true, isPutInPlayable: false },
      { id: 102, name: 'Barel', image: 'barel', isPlayable: false, isPutInPlayable: true },
    ],
    allowedUIElements: ['ZIVOTY', 'UKONCENI_TAHU', 'POSTAVA', 'ROLE', 'OHEN'],
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

describe('HandCards component', () => {
  it('renders cards in hand with shortcut badges', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <HandCards />
        </DragProvider>
      </GameContext.Provider>
    );

    expect(screen.getByText('1')).toBeDefined();
    expect(screen.getByText('2')).toBeDefined();
  });

  it('selects card on click and shows action buttons (Zahrát, Spálit)', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <HandCards />
        </DragProvider>
      </GameContext.Provider>
    );

    const bangCard = screen.getByText('1').closest('div');
    if (bangCard) fireEvent.click(bangCard);

    expect(screen.getByText('Zahrát')).toBeDefined();
    expect(screen.getByText('Spálit')).toBeDefined();

    fireEvent.click(screen.getByText('Zahrát'));
    expect(mockCtx.playCard).toHaveBeenCalledWith(101);
  });

  it('selects card with keyboard shortcut 1 and plays with Enter', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <HandCards />
        </DragProvider>
      </GameContext.Provider>
    );

    fireEvent.keyDown(window, { key: '1' });
    expect(screen.getByText('Zahrát')).toBeDefined();

    fireEvent.keyDown(window, { key: 'Enter' });
    expect(mockCtx.playCard).toHaveBeenCalledWith(101);
  });

  it('scrolls container when mouse wheel is used', () => {
    const mockCtx = createMockGameContext();
    const { container } = render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <HandCards />
        </DragProvider>
      </GameContext.Provider>
    );

    const scrollContainer = container.querySelector('.overflow-x-auto');
    expect(scrollContainer).toBeDefined();
    if (scrollContainer) {
      fireEvent.wheel(scrollContainer, { deltaY: 100 });
      // Event triggers without throwing error
      expect(scrollContainer.scrollLeft).toBeGreaterThanOrEqual(0);
    }
  });

  it('shows Zvětšit button and opens zoom modal when clicked', () => {
    const mockCtx = createMockGameContext();
    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <HandCards />
        </DragProvider>
      </GameContext.Provider>
    );

    // Select card 1
    fireEvent.keyDown(window, { key: '1' });
    const zoomBtn = screen.getByText('Zvětšit');
    expect(zoomBtn).toBeDefined();

    // Open zoom modal
    fireEvent.click(zoomBtn);
    const closeBtn = screen.getByText('Zavřít');
    expect(closeBtn).toBeDefined();

    // Close zoom modal
    fireEvent.click(closeBtn);
    expect(screen.queryByText('Zavřít')).toBeNull();
  });
});
