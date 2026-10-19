import React, { createContext, useContext, useState, useEffect, useRef } from "react";
import { createPortal } from "react-dom";
import { useGame, type CardType, translateServerText } from "@bang/core";
import { CardView } from "../design-system";

export type DragSource = "hand" | "inplay";

interface DragContextType {
  draggedCard: CardType | null;
  source: DragSource | null;
  activeDropTarget: string | null;
  startDrag: (card: CardType, source: DragSource, event: React.PointerEvent) => void;
  isDragging: boolean;
}

const DragContext = createContext<DragContextType | null>(null);

export const useDrag = () => {
  const ctx = useContext(DragContext);
  if (!ctx) {
    throw new Error("useDrag must be used within a DragProvider");
  }
  return ctx;
};

export const DragProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { playCard, fireCard, putCardInPlay, putCardInPlayOnPlayer } = useGame();
  const [draggedCard, setDraggedCard] = useState<CardType | null>(null);
  const [source, setSource] = useState<DragSource | null>(null);
  const [pointerPos, setPointerPos] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
  const [activeDropTarget, setActiveDropTarget] = useState<string | null>(null);

  const dragRef = useRef<{
    card: CardType | null;
    source: DragSource | null;
    hasMoved: boolean;
    startX: number;
    startY: number;
  }>({ card: null, source: null, hasMoved: false, startX: 0, startY: 0 });

  const startDrag = (card: CardType, dragSource: DragSource, event: React.PointerEvent) => {
    // Pouze levé tlačítko myši nebo dotyk
    if (event.button !== 0 && event.pointerType === "mouse") return;

    dragRef.current = {
      card,
      source: dragSource,
      hasMoved: false,
      startX: event.clientX,
      startY: event.clientY,
    };
    setDraggedCard(card);
    setSource(dragSource);
    setPointerPos({ x: event.clientX, y: event.clientY });
  };

  useEffect(() => {
    if (!draggedCard) return;

    const handlePointerMove = (e: PointerEvent) => {
      dragRef.current.hasMoved = true;
      setPointerPos({ x: e.clientX, y: e.clientY });

      // Detekce zóny pod kurzorem bez jakéhokoliv zpoždění
      const elements = typeof document.elementsFromPoint === "function"
        ? document.elementsFromPoint(e.clientX, e.clientY)
        : [];
      const dropEl = elements.find((el) => el.getAttribute("data-dropzone"));
      const dropId = dropEl ? dropEl.getAttribute("data-dropzone") : null;
      setActiveDropTarget(dropId);
    };

    const handlePointerUp = (e: PointerEvent) => {
      const card = dragRef.current.card;
      const currentSource = dragRef.current.source;

      if (card) {
        const elements = typeof document.elementsFromPoint === "function"
          ? document.elementsFromPoint(e.clientX, e.clientY)
          : [];
        const dropEl = elements.find((el) => el.getAttribute("data-dropzone"));
        const dropId = dropEl ? dropEl.getAttribute("data-dropzone") : null;

        if (dropId === "fire") {
          fireCard(card.id);
        } else if (dropId === "inplay" && currentSource === "hand") {
          putCardInPlay(card.id);
        } else if (dropId?.startsWith("player-")) {
          const playerId = parseInt(dropId.replace("player-", ""), 10);
          if (!isNaN(playerId)) {
            putCardInPlayOnPlayer(card.id, playerId);
          }
        } else if (dropId === "table" || e.clientY < window.innerHeight * 0.68) {
          // Puštěno na stůl nebo směrem vzhůru na herní plochu
          if (currentSource === "hand") {
            playCard(card.id);
          } else if (currentSource === "inplay" && card.isPlayable) {
            playCard(card.id);
          }
        }
      }

      // Reset stavu přetažení
      dragRef.current = { card: null, source: null, hasMoved: false, startX: 0, startY: 0 };
      setDraggedCard(null);
      setSource(null);
      setActiveDropTarget(null);
    };

    window.addEventListener("pointermove", handlePointerMove, { passive: true });
    window.addEventListener("pointerup", handlePointerUp);
    window.addEventListener("pointercancel", handlePointerUp);

    return () => {
      window.removeEventListener("pointermove", handlePointerMove);
      window.removeEventListener("pointerup", handlePointerUp);
      window.removeEventListener("pointercancel", handlePointerUp);
    };
  }, [draggedCard, playCard, fireCard, putCardInPlay, putCardInPlayOnPlayer]);

  return (
    <DragContext.Provider
      value={{
        draggedCard,
        source,
        activeDropTarget,
        startDrag,
        isDragging: !!draggedCard,
      }}
    >
      {children}

      {/* PORTAL DRAG OVERLAY: Fyzicky ukotveno v document.body */}
      {/* Vždy plave nad celým UI se z-indexem 9999999, bez jakéhokoliv ořezu a bez lagu */}
      {typeof document !== "undefined" &&
        draggedCard &&
        createPortal(
          <div
            style={{
              position: "fixed",
              left: pointerPos.x,
              top: pointerPos.y,
              transform: "translate(-50%, -50%) scale(1.15)",
              pointerEvents: "none",
              zIndex: 9999999,
              transition: "none", // Klíčové: žádné lagování v pohybu!
              willChange: "left, top",
            }}
            className="filter drop-shadow-[0_20px_35px_rgba(0,0,0,0.85)] cursor-grabbing"
          >
            <CardView
              image={draggedCard.image}
              name={translateServerText(draggedCard.name)}
              size="md"
              className="ring-4 ring-amber-400 border-amber-300 shadow-2xl transition-none"
            />
          </div>,
          document.body
        )}
    </DragContext.Provider>
  );
};

export default DragProvider;
