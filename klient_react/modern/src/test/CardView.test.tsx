import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { CardView } from '../design-system/CardView';

describe('CardView component', () => {
  it('renders card image with correct aspect ratio and object-contain', () => {
    const { container } = render(<CardView image="bang" name="Bang!" size="md" />);
    const cardEl = container.firstChild as HTMLElement;
    expect(cardEl.className).toContain('aspect-[660/1035]');
    expect(cardEl.className).toContain('w-18');

    const img = screen.getByRole('img');
    expect(img).toBeDefined();
    expect(img.getAttribute('src')).toBe('/img/karty/bang.png');
    expect(img.className).toContain('object-contain');
  });

  it('renders all size variants with aspect-[660/1035]', () => {
    const { rerender, container } = render(<CardView image="barel" size="xs" />);
    expect((container.firstChild as HTMLElement).className).toContain('aspect-[660/1035]');
    expect((container.firstChild as HTMLElement).className).toContain('w-10');

    rerender(<CardView image="barel" size="sm" />);
    expect((container.firstChild as HTMLElement).className).toContain('w-14');

    rerender(<CardView image="barel" size="lg" />);
    expect((container.firstChild as HTMLElement).className).toContain('w-36');
  });

  it('shows checkmark indicator when isSelected is true', () => {
    const { container } = render(<CardView image="dynamit" name="Dynamit" isSelected={true} />);
    const cardEl = container.firstChild as HTMLElement;
    expect(cardEl.className).toContain('border-amber-300');
    expect(cardEl.className).toContain('ring-4');
  });

  it('disables transitions when transition-none is present in className', () => {
    const { container } = render(
      <CardView image="dynamit" name="Dynamit" className="transition-none" />
    );
    const cardEl = container.firstChild as HTMLElement;
    expect(cardEl.className).toContain('transition-none');
    expect(cardEl.className).not.toContain('transition-all');
  });

  it('renders fallback when image fails to load', () => {
    render(<CardView image="non_existent_card" name="Neznámá karta" />);
    const img = screen.getByRole('img');
    fireEvent.error(img);

    expect(screen.getByText('Neznámá karta')).toBeDefined();
    expect(screen.getByText('Bang!')).toBeDefined();
  });
});
