import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { DialogManager } from '../components/DialogManager';
import { DialogContext, type DialogState } from '@bang/core';

describe('DialogManager component', () => {
  it('renders CONFIRM dialog and calls callback with true on confirm', () => {
    const callback = vi.fn();
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'CONFIRM',
      data: {
        title: 'Byl jsi pozván do jiné hry',
        message: 'Chceš se tam připojit?',
        confirmText: 'Připojit',
        cancelText: 'Zůstat',
      },
      callback,
    };

    render(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    expect(screen.getByText('Byl jsi pozván do jiné hry')).toBeDefined();
    expect(screen.getByText('Chceš se tam připojit?')).toBeDefined();

    const confirmBtn = screen.getByText('Připojit');
    fireEvent.click(confirmBtn);

    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith(true);
  });

  it('renders SELECT_CARD dialog and allows card selection', () => {
    const callback = vi.fn();
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'SELECT_CARD',
      dialogHeader: 'Vyber kartu k odhození',
      data: {
        cards: [
          { id: 11, name: 'Bang!', image: 'bang', isPlayable: true, isPutInPlayable: false },
          { id: 12, name: 'Vedle', image: 'missed', isPlayable: true, isPutInPlayable: false },
        ],
        min: 1,
        max: 1,
      },
      callback,
    };

    render(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    expect(screen.getByText('Vyber kartu k odhození')).toBeDefined();

    // Select first card
    const cardImg = screen.getByAltText('Bang!');
    fireEvent.click(cardImg);

    const submitBtn = screen.getByText('Potvrdit výběr (1/1)');
    expect(submitBtn).toBeDefined();
    fireEvent.click(submitBtn);

    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith([11]);
  });

  it('renders LUCKY_WHEEL dialog and allows skipping immediately with callback and toast', () => {
    const callback = vi.fn();
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'LUCKY_WHEEL',
      data: {
        options: [
          { id: 1, name: 'Dynamit explodoval', color: '#b45309' },
          { id: 2, name: 'Dynamit neexplodoval', color: '#15803d' },
        ],
        chosedOptionId: 2,
      },
      callback,
    };

    render(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    expect(screen.getByText('Kolo osudu')).toBeDefined();
    const skipBtn = screen.getByText('Přeskočit animaci (Esc)');
    expect(skipBtn).toBeDefined();

    fireEvent.click(skipBtn);
    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith(2);
  });
});
