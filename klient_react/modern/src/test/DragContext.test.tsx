import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { DragProvider, useDrag } from '../components/DragContext';
import { GameContext, gameStateDefault } from '@bang/core';

const TestDragComponent: React.FC = () => {
  const { startDrag, draggedCard, activeDropTarget } = useDrag();

  return (
    <div>
      <div
        data-testid="drag-handle"
        onPointerDown={(e) =>
          startDrag(
            { id: 999, name: 'Bang!', image: 'bang', isPlayable: true, isPutInPlayable: false },
            'hand',
            e
          )
        }
      >
        Drag Me
      </div>
      <div data-testid="status">
        {draggedCard ? `Dragging: ${draggedCard.id}` : 'Idle'}
      </div>
      <div data-dropzone="fire" data-testid="fire-zone">
        Fire Zone
      </div>
    </div>
  );
};

describe('DragContext Portal Drag Overlay', () => {
  it('starts drag on pointer down and cleans up on pointer up', () => {
    const mockCtx: any = {
      gameState: gameStateDefault,
      isConnected: true,
      playCard: vi.fn(),
      fireCard: vi.fn(),
      putCardInPlay: vi.fn(),
      putCardInPlayOnPlayer: vi.fn(),
    };

    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <TestDragComponent />
        </DragProvider>
      </GameContext.Provider>
    );

    expect(screen.getByTestId('status').textContent).toBe('Idle');

    const handle = screen.getByTestId('drag-handle');
    fireEvent.pointerDown(handle, { button: 0, clientX: 100, clientY: 200, pointerType: 'mouse' });

    expect(screen.getByTestId('status').textContent).toBe('Dragging: 999');

    // Portal should be attached to body
    const portalCard = document.body.querySelector('[style*="position: fixed"]');
    expect(portalCard).not.toBeNull();

    // Releasing pointer triggers handlePointerUp
    fireEvent.pointerUp(window, { clientX: 100, clientY: 100 });
    expect(screen.getByTestId('status').textContent).toBe('Idle');
  });

  it('triggers fireCard when dropped onto fire zone', () => {
    const mockCtx: any = {
      gameState: gameStateDefault,
      isConnected: true,
      playCard: vi.fn(),
      fireCard: vi.fn(),
      putCardInPlay: vi.fn(),
      putCardInPlayOnPlayer: vi.fn(),
    };

    render(
      <GameContext.Provider value={mockCtx}>
        <DragProvider>
          <TestDragComponent />
        </DragProvider>
      </GameContext.Provider>
    );

    const handle = screen.getByTestId('drag-handle');
    const fireZone = screen.getByTestId('fire-zone');

    // Mock document.elementsFromPoint
    document.elementsFromPoint = vi.fn().mockReturnValue([fireZone]);

    fireEvent.pointerDown(handle, { button: 0, clientX: 100, clientY: 200, pointerType: 'mouse' });
    fireEvent.pointerUp(window, { clientX: 150, clientY: 250 });

    expect(mockCtx.fireCard).toHaveBeenCalledWith(999);
  });

  it('gracefully returns fallback values when useDrag is used outside DragProvider', () => {
    let result: any;
    const TestConsumer = () => {
      result = useDrag();
      return <div data-testid="fallback-test">{result.isDragging ? 'yes' : 'no'}</div>;
    };

    expect(() => render(<TestConsumer />)).not.toThrow();
    expect(screen.getByTestId('fallback-test').textContent).toBe('no');
    expect(result.draggedCard).toBeNull();
    expect(result.source).toBeNull();
    expect(result.activeDropTarget).toBeNull();
    expect(typeof result.startDrag).toBe('function');
  });
});
