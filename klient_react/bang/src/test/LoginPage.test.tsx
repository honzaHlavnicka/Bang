import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import LoginPage from '../pages/LoginPage';
import * as GameContextModule from '../modules/GameContext';

vi.mock('@posthog/react', () => ({
  usePostHog: () => ({ capture: vi.fn() }),
}));

describe('LoginPage Component', () => {
  let mockConnectToGame: any;
  let mockCreateGame: any;

  beforeEach(() => {
    mockConnectToGame = vi.fn();
    mockCreateGame = vi.fn();

    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        startedConection: true,
        gameTypesAvailable: [
          { id: 0, name: 'Bang!', description: 'Střílečka', url: 'bang.png' },
          { id: 1, name: 'Prší', description: 'Karetní hra', url: 'sedma.png' },
        ],
      },
      connectToGame: mockConnectToGame,
      createGame: mockCreateGame,
      returnToGame: vi.fn(),
      isConnected: true,
    } as any);

    localStorage.clear();
    sessionStorage.clear();
  });

  it('vykreslí hlavní přepínače pro připojení a vytvoření hry', () => {
    render(<LoginPage />);

    expect(screen.getByRole('button', { name: /připojit se ke hře/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /vytvořit novou hru/i })).toBeInTheDocument();
  });

  it('přepne na formulář vytvoření hry, zadá jméno a založí novou hru', () => {
    render(<LoginPage />);

    // Klik na záložku Vytvořit novou hru
    const openCreateTab = screen.getByRole('button', { name: /vytvořit novou hru/i });
    fireEvent.click(openCreateTab);

    // Zkontrolujeme nabídku her v selectu
    expect(screen.getByRole('combobox')).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'Bang!' })).toBeInTheDocument();

    // Zadáme validní jméno (3-15 znaků)
    const inputs = screen.getAllByRole('textbox');
    // Jméno je v textboxu
    const nameInput = inputs[inputs.length - 1];
    fireEvent.change(nameInput, { target: { value: 'Kovboj' } });

    // Odeslání formuláře tlačítkem "Vytvořit hru"
    const submitBtn = screen.getByRole('button', { name: /^vytvořit hru$/i });
    fireEvent.click(submitBtn);

    expect(mockCreateGame).toHaveBeenCalledWith(0, 'Kovboj');
  });

  it('přepne na formulář připojení přes kód a připojí se se 6místným kódem', () => {
    render(<LoginPage />);

    // Klik na záložku Připojit se ke hře
    const openJoinTab = screen.getByRole('button', { name: /připojit se ke hře/i });
    fireEvent.click(openJoinTab);

    const inputs = screen.getAllByRole('textbox');
    expect(inputs.length).toBeGreaterThanOrEqual(2);

    const codeInput = inputs[0];
    const nameInput = inputs[1];

    fireEvent.change(codeInput, { target: { value: '123456' } });
    fireEvent.change(nameInput, { target: { value: 'Bandita' } });

    // Odeslání formuláře
    const submitBtn = screen.getByRole('button', { name: /^připojit se ke hře$/i });
    fireEvent.click(submitBtn);

    expect(mockConnectToGame).toHaveBeenCalledWith('123456', 'Bandita');
  });
});
