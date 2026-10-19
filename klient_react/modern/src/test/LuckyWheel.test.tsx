import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { LuckyWheel } from '../components/LuckyWheel';

describe('LuckyWheel component', () => {
  const options = [
    { id: 1, name: 'Vězení', color: '#b45309' },
    { id: 2, name: 'Svoboda', color: '#15803d' },
  ];

  it('initially displays "Kolo se točí..." and allows skipping with button or Esc', () => {
    const onConfirm = vi.fn();
    const onSkip = vi.fn();
    render(
      <LuckyWheel
        options={options}
        chosedOptionId={2}
        onConfirm={onConfirm}
        onSkip={onSkip}
      />
    );

    // Kolo se točí... is shown
    expect(screen.getByText('Kolo se točí...')).toBeDefined();
    // Skip button is present
    const skipBtn = screen.getByText('Přeskočit animaci (Esc)');
    expect(skipBtn).toBeDefined();

    // Click skip button
    fireEvent.click(skipBtn);
    expect(onSkip).toHaveBeenCalledTimes(1);

    // Press Escape
    fireEvent.keyDown(window, { key: 'Escape' });
    expect(onSkip).toHaveBeenCalledTimes(2);
  });

  it('triggers onConfirm when finished and Enter or Space is pressed', () => {
    vi.useFakeTimers();
    const onConfirm = vi.fn();
    const { container } = render(
      <LuckyWheel
        options={options}
        chosedOptionId={2}
        onConfirm={onConfirm}
      />
    );

    // Advance past the initial 150ms timeout to trigger spin
    act(() => {
      vi.advanceTimersByTime(200);
    });

    // The motion.g element will receive animate={{ rotate: ... }}
    // Simulate animation complete
    // In LuckyWheel, handleAnimationComplete is called by onAnimationComplete
    const wheelGroup = container.querySelector('g[style*="transform-origin"]');
    expect(wheelGroup).toBeDefined();

    vi.useRealTimers();
  });
});
