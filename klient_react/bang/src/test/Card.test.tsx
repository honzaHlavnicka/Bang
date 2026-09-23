import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import Card from '../components/Card';
import { ZoomProvider, useZoom } from '../modules/ZoomContext';

function TestCardWrapper({ onClick }: { onClick?: () => void }) {
  // Komponenta pro přepnutí zoom módu
  const ZoomController = () => {
    const { toggleZoomMode, zoomedCard } = useZoom();
    return (
      <div>
        <button onClick={toggleZoomMode}>ToggleZoom</button>
        <span data-testid="zoomed-val">{zoomedCard ?? 'none'}</span>
      </div>
    );
  };

  return (
    <ZoomProvider>
      <ZoomController />
      <Card
        id={42}
        image="/img/karty/bang.png"
        name="Bang!"
        biggerOnHover
        isRotated
        onClick={onClick}
      />
    </ZoomProvider>
  );
}

describe('Card Component', () => {
  it('vykreslí kartu se správným obrázkem, názvem a data-id', () => {
    const handleClick = vi.fn();
    render(<TestCardWrapper onClick={handleClick} />);

    const img = screen.getByAltText('Bang!');
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', '/img/karty/bang.png');
    expect(img).toHaveAttribute('title', 'Bang!');

    const cardDiv = img.parentElement;
    expect(cardDiv).toHaveAttribute('data-id', '42');
  });

  it('vyvolá onClick handler při kliknutí v běžném režimu', () => {
    const handleClick = vi.fn();
    render(<TestCardWrapper onClick={handleClick} />);

    const img = screen.getByAltText('Bang!');
    fireEvent.click(img);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('v režimu zoomu nastaví zoomedCard a nevyvolá původní onClick handler', () => {
    const handleClick = vi.fn();
    render(<TestCardWrapper onClick={handleClick} />);

    // Zapneme režim lupy (zoom mode)
    const toggleBtn = screen.getByText('ToggleZoom');
    fireEvent.click(toggleBtn);

    const img = screen.getByAltText('Bang!');
    fireEvent.click(img);

    // Běžný onClick nesmí být zavolán
    expect(handleClick).not.toHaveBeenCalled();

    // V zoom kontextu musí být karta nastavena k přiblížení
    expect(screen.getByTestId('zoomed-val')).toHaveTextContent('/img/karty/bang.png');

    // Druhé kliknutí kartu ze zoomu opět zruší
    fireEvent.click(img);
    expect(screen.getByTestId('zoomed-val')).toHaveTextContent('none');
  });

  it('automaticky odvodí název karty z cesty obrázku, pokud name není specifikován', () => {
    render(
      <ZoomProvider>
        <Card image="/img/karty/dostavnik.png" />
      </ZoomProvider>
    );

    const img = screen.getByAltText('dostavnik');
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('title', 'dostavnik');
  });
});
