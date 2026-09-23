import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import BeforeGameWaiting from '../pages/BeforeGameWaiting';
import * as GameContextModule from '../modules/GameContext';

vi.mock('@posthog/react', () => ({
  usePostHog: () => ({ capture: vi.fn() }),
}));

describe('BeforeGameWaiting Component', () => {
  it('vykreslí čekací místnost a kód hry', () => {
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        gameCode: '9876',
        players: [{ id: 1, name: 'Hrac 1', health: 4, isOnline: true }],
        isAdmin: true,
        playerId: 1,
      },
      startGame: vi.fn(),
      kickPlayer: vi.fn(),
    } as any);

    render(<BeforeGameWaiting />);

    expect(screen.getByText('Čekání na další hráče')).toBeInTheDocument();
    expect(screen.getByText('9876')).toBeInTheDocument();
  });

  it('nezobrazí tlačítko pro spuštění hry, pokud je v lobby jen 1 hráč', () => {
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        gameCode: '9876',
        players: [{ id: 1, name: 'Hrac 1', health: 4, isOnline: true }],
        isAdmin: true,
        playerId: 1,
      },
      startGame: vi.fn(),
      kickPlayer: vi.fn(),
    } as any);

    render(<BeforeGameWaiting />);

    expect(screen.queryByRole('button', { name: /spustit hru/i })).not.toBeInTheDocument();
  });

  it('zobrazí tlačítko pro spuštění hry pro administrátora při >= 2 hráčích a vyvolá startGame', () => {
    const mockStartGame = vi.fn();
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        gameCode: '9876',
        players: [
          { id: 1, name: 'Admin', health: 4, isOnline: true },
          { id: 2, name: 'Host', health: 4, isOnline: true },
        ],
        isAdmin: true,
        playerId: 1,
      },
      startGame: mockStartGame,
      kickPlayer: vi.fn(),
    } as any);

    render(<BeforeGameWaiting />);

    const startBtn = screen.getByRole('button', { name: /spustit hru/i });
    expect(startBtn).toBeInTheDocument();

    fireEvent.click(startBtn);
    expect(mockStartGame).toHaveBeenCalledTimes(1);
  });

  it('nezobrazí tlačítko spuštění běžnému hráči, i když je v lobby více hráčů', () => {
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        gameCode: '9876',
        players: [
          { id: 1, name: 'Admin', health: 4, isOnline: true },
          { id: 2, name: 'Host', health: 4, isOnline: true },
        ],
        isAdmin: false,
        playerId: 2,
      },
      startGame: vi.fn(),
      kickPlayer: vi.fn(),
    } as any);

    render(<BeforeGameWaiting />);

    expect(screen.queryByRole('button', { name: /spustit hru/i })).not.toBeInTheDocument();
  });
});
