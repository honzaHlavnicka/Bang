import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CharacterPicker from '../components/CharacterPicker';
import * as GameContextModule from '../modules/GameContext';
import { ZoomProvider } from '../modules/ZoomContext';

vi.mock('@posthog/react', () => ({
  usePostHog: () => ({ capture: vi.fn() }),
}));

const mockCharacters = [
  { jmeno: 'Bart Cassidy', obrazek: 'bart_cassidy', popis: 'Při ztrátě života líže kartu' },
  { jmeno: 'Paul Regret', obrazek: 'paul_regret', popis: 'Vzdálenost +1' },
];

function renderWithZoom(component: React.ReactElement) {
  return render(<ZoomProvider>{component}</ZoomProvider>);
}

describe('CharacterPicker Component', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('zobrazí načítání pokud characters ještě nejsou k dispozici', () => {
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        character: null,
        characters: null as any,
      },
      chooseCharacter: vi.fn(),
    } as any);

    renderWithZoom(<CharacterPicker />);
    expect(screen.getByText('Načítání postav...')).toBeInTheDocument();
  });

  it('nevykreslí nic, pokud již hráč má zvolenou postavu', () => {
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        character: 'bart_cassidy',
        characters: mockCharacters,
      },
      chooseCharacter: vi.fn(),
    } as any);

    const { container } = renderWithZoom(<CharacterPicker />);
    expect(container).toBeEmptyDOMElement();
  });

  it('vykreslí nabídku postav a umožní vybrat kliknutím na kartu', () => {
    const mockChooseCharacter = vi.fn();
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        character: null,
        characters: mockCharacters,
      },
      chooseCharacter: mockChooseCharacter,
    } as any);

    renderWithZoom(<CharacterPicker />);

    expect(screen.getByText('vyber si jednu z těchto postav')).toBeInTheDocument();
    
    // Zkontrolujeme zobrazení obou karet podle alt nebo title
    const bartImg = screen.getByAltText(/Bart Cassidy/i);
    expect(bartImg).toBeInTheDocument();
    expect(bartImg).toHaveAttribute('src', '/img/karty/postavy/bart_cassidy.png');

    const paulImg = screen.getByAltText(/Paul Regret/i);
    expect(paulImg).toBeInTheDocument();

    // Kliknutí na kartu vybere danou postavu
    fireEvent.click(bartImg);
    expect(mockChooseCharacter).toHaveBeenCalledTimes(1);
    expect(mockChooseCharacter).toHaveBeenCalledWith('bart_cassidy');
  });

  it('umožní vybrat postavu klávesovými zkratkami 1 a 2', () => {
    const mockChooseCharacter = vi.fn();
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        character: null,
        characters: mockCharacters,
      },
      chooseCharacter: mockChooseCharacter,
    } as any);

    renderWithZoom(<CharacterPicker />);

    // Stisk klávesy '1'
    fireEvent.keyDown(window, { key: '1' });
    expect(mockChooseCharacter).toHaveBeenCalledWith('bart_cassidy');

    // Stisk klávesy '2'
    fireEvent.keyDown(window, { key: '2' });
    expect(mockChooseCharacter).toHaveBeenCalledWith('paul_regret');
  });

  it('nevybere postavu klávesovou zkratkou, pokud je aktivní element vstupní pole (input)', () => {
    const mockChooseCharacter = vi.fn();
    vi.spyOn(GameContextModule, 'useGame').mockReturnValue({
      gameState: {
        ...GameContextModule.gameStateDefault,
        character: null,
        characters: mockCharacters,
      },
      chooseCharacter: mockChooseCharacter,
    } as any);

    renderWithZoom(
      <div>
        <input data-testid="chat-input" />
        <CharacterPicker />
      </div>
    );

    const input = screen.getByTestId('chat-input');
    input.focus();

    fireEvent.keyDown(window, { key: '1' });
    expect(mockChooseCharacter).not.toHaveBeenCalled();
  });
});
