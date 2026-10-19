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

  it('confirms SELECT_CARD dialog with Enter key', () => {
    const callback = vi.fn();
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'SELECT_CARD',
      data: {
        cards: [
          { id: 21, name: 'Pivo', image: 'beer', isPlayable: true, isPutInPlayable: false },
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

    // Select the card
    const card = screen.getByAltText('Pivo');
    fireEvent.click(card);

    // Press Enter to confirm
    fireEvent.keyDown(window, { key: 'Enter' });

    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith([21]);
  });

  it('confirms CONFIRM dialog with Enter key and cancels with Escape', () => {
    const callback = vi.fn();
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'CONFIRM',
      data: {
        title: 'Dotaz',
        message: 'Opravdu chceš ukončit hru?',
      },
      callback,
    };

    const { rerender } = render(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    // Press Enter
    fireEvent.keyDown(window, { key: 'Enter' });
    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith(true);

    // Reset mocks and test Escape
    vi.clearAllMocks();
    rerender(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    fireEvent.keyDown(window, { key: 'Escape' });
    expect(closeDialog).toHaveBeenCalled();
    expect(callback).toHaveBeenCalledWith(false);
  });

  it('closes INFO dialog with Enter or Space key', () => {
    const closeDialog = vi.fn();

    const dialogState: DialogState = {
      type: 'INFO',
      data: {
        header: 'Nápověda',
        message: 'Tohle je vysvětlující text.',
      },
    };

    render(
      <DialogContext.Provider value={{ dialog: dialogState, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    expect(screen.getByText('Tohle je vysvětlující text.')).toBeDefined();

    // Press Enter to dismiss
    fireEvent.keyDown(window, { key: 'Enter' });
    expect(closeDialog).toHaveBeenCalled();
  });

  it('transitions between null and multiple dialog types without hook order violations', () => {
    const closeDialog = vi.fn();
    const { rerender } = render(
      <DialogContext.Provider value={{ dialog: null, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );

    // Open SELECT_CARD
    rerender(
      <DialogContext.Provider
        value={{
          dialog: {
            type: 'SELECT_CARD',
            data: {
              cards: [{ id: 1, name: 'Bang!', image: 'bang', isPlayable: true, isPutInPlayable: false }],
              min: 1,
              max: 1,
            },
          },
          openDialog: vi.fn(),
          closeDialog,
        }}
      >
        <DialogManager />
      </DialogContext.Provider>
    );
    expect(screen.getByText('Vyber kartu')).toBeDefined();

    // Close to null
    rerender(
      <DialogContext.Provider value={{ dialog: null, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );
    expect(screen.queryByText('Vyber kartu')).toBeNull();

    // Open INFO dialog
    rerender(
      <DialogContext.Provider
        value={{
          dialog: {
            type: 'INFO',
            data: { header: 'Info Test', message: 'Zpráva' },
          },
          openDialog: vi.fn(),
          closeDialog,
        }}
      >
        <DialogManager />
      </DialogContext.Provider>
    );
    expect(screen.getByText('Info Test')).toBeDefined();

    // Close back to null
    rerender(
      <DialogContext.Provider value={{ dialog: null, openDialog: vi.fn(), closeDialog }}>
        <DialogManager />
      </DialogContext.Provider>
    );
    expect(screen.queryByText('Info Test')).toBeNull();
  });
});

