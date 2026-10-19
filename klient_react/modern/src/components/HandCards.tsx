import React, { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useGame, type CardType, translateServerText } from "@bang/core";
import { CardView, Button, Badge, ModalFrame } from "../design-system";
import { useDrag } from "./DragContext";
import { Flame, Play, LayoutGrid, X, ChevronLeft, ChevronRight, Maximize2 } from "lucide-react";

export const HandCards: React.FC = () => {
  const { gameState, playCard, fireCard, putCardInPlay } = useGame();
  const { startDrag, draggedCard } = useDrag();
  const [selectedCardId, setSelectedCardId] = useState<number | null>(null);
  const [isZoomOpen, setIsZoomOpen] = useState(false);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  const hand = gameState.handCards || [];
  const selectedCard = hand.find((c) => c.id === selectedCardId);

  // Klávesové zkratky:
  // 1-9: výběr karty
  // Enter / P: zahrát kartu
  // V / Space (když je karta vybrána): vyložit kartu
  // F / Delete: spálit kartu
  // Esc: zrušit výběr
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        document.activeElement?.tagName === "INPUT" ||
        document.activeElement?.tagName === "TEXTAREA"
      ) {
        return;
      }

      // 1-9 výběr karty
      const num = parseInt(e.key, 10);
      if (!isNaN(num) && num >= 1 && num <= Math.min(hand.length, 9)) {
        e.preventDefault();
        setSelectedCardId((prev) => (prev === hand[num - 1].id ? null : hand[num - 1].id));
        return;
      }

      // Esc pro zrušení výběru
      if (e.key === "Escape") {
        setSelectedCardId(null);
        return;
      }

      // Akce s vybranou kartou
      if (selectedCardId !== null) {
        // F / Delete pro spálení
        if (e.key === "f" || e.key === "F" || e.key === "Delete") {
          e.preventDefault();
          fireCard(selectedCardId);
          setSelectedCardId(null);
          return;
        }

        // Enter / P pro zahrání
        if (e.key === "Enter" || e.key === "p" || e.key === "P") {
          e.preventDefault();
          playCard(selectedCardId);
          setSelectedCardId(null);
          return;
        }

        // V pro vyložení
        if (e.key === "v" || e.key === "V") {
          e.preventDefault();
          putCardInPlay(selectedCardId);
          setSelectedCardId(null);
          return;
        }
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [hand, selectedCardId, fireCard, playCard, putCardInPlay]);

  // Posun vějíře kolečkem myši (přirozené rolování)
  const handleWheel = (e: React.WheelEvent) => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollLeft += e.deltaY;
    }
  };

  // Zahájení přetažení karty:
  // - Na PC: klik a tah myší ihned zahájí Portal Drag (bez zpoždění a lagu)
  // - Na mobilu: horizontální posun roluje vějíř, vertikální tah nahoru zvedá kartu
  const handlePointerDown = (card: CardType, e: React.PointerEvent) => {
    if (e.pointerType === "mouse") {
      if (e.button === 0) {
        startDrag(card, "hand", e);
      }
      return;
    }

    // Dotyková zařízení: detekce gesta
    const startX = e.clientX;
    const startY = e.clientY;
    let dragTriggered = false;

    const onPointerMove = (evt: PointerEvent) => {
      const dy = evt.clientY - startY;
      const dx = Math.abs(evt.clientX - startX);

      // Pokud hráč táhne kartu nahoru směrem k herní ploše (dy < -12px)
      if (!dragTriggered && dy < -12 && Math.abs(dy) > dx * 0.7) {
        dragTriggered = true;
        cleanUp();
        startDrag(card, "hand", evt as unknown as React.PointerEvent);
      }
    };

    const cleanUp = () => {
      window.removeEventListener("pointermove", onPointerMove);
      window.removeEventListener("pointerup", cleanUp);
      window.removeEventListener("pointercancel", cleanUp);
    };

    window.addEventListener("pointermove", onPointerMove, { passive: true });
    window.addEventListener("pointerup", cleanUp, { once: true });
    window.addEventListener("pointercancel", cleanUp, { once: true });
  };

  if (hand.length === 0) {
    return (
      <div className="w-full py-3 text-center text-xs text-[#7a6c60] italic">
        V ruce nemáš žádné karty
      </div>
    );
  }

  return (
    <div className="w-full relative flex flex-col items-center select-none z-30">
      {/* Rychlá akční lišta pro vybranou kartu s klávesovými zkratkami – plave nad vějířem bez poskakování */}
      <AnimatePresence>
        {selectedCard && (
          <motion.div
            initial={{ opacity: 0, y: 8, x: "-50%" }}
            animate={{ opacity: 1, y: 0, x: "-50%" }}
            exit={{ opacity: 0, y: 8, x: "-50%" }}
            className="absolute -top-12 left-1/2 flex items-center gap-1 sm:gap-1.5 p-1 sm:p-1.5 bg-[#261e18] border border-amber-500/70 rounded-xl shadow-2xl z-40 max-w-[96vw] overflow-x-auto whitespace-nowrap"
          >
            <span className="text-xs font-semibold px-1.5 sm:px-2 text-[#f5ede3] max-w-[85px] sm:max-w-[130px] truncate">
              {translateServerText(selectedCard.name)}
            </span>
            <Button
              size="sm"
              variant="primary"
              icon={<Play className="w-3.5 h-3.5" />}
              shortcutHint="Enter"
              className="px-2 py-1 sm:px-3 sm:py-1.5 text-xs shrink-0"
              onClick={() => {
                playCard(selectedCard.id);
                setSelectedCardId(null);
              }}
            >
              Zahrát
            </Button>
            {selectedCard.isPutInPlayable && (
              <Button
                size="sm"
                variant="secondary"
                icon={<LayoutGrid className="w-3.5 h-3.5" />}
                shortcutHint="V"
                className="px-2 py-1 sm:px-3 sm:py-1.5 text-xs shrink-0"
                onClick={() => {
                  putCardInPlay(selectedCard.id);
                  setSelectedCardId(null);
                }}
              >
                Vyložit
              </Button>
            )}
            {gameState.allowedUIElements.includes("OHEN") && (
              <Button
                size="sm"
                variant="terracotta"
                icon={<Flame className="w-3.5 h-3.5" />}
                shortcutHint="F"
                className="px-2 py-1 sm:px-3 sm:py-1.5 text-xs shrink-0"
                onClick={() => {
                  fireCard(selectedCard.id);
                  setSelectedCardId(null);
                }}
              >
                Spálit
              </Button>
            )}
            {/* Decentní tlačítko pro zvětšení karty */}
            <Button
              size="sm"
              variant="ghost"
              icon={<Maximize2 className="w-3.5 h-3.5 text-[#b5a596]" />}
              onClick={() => setIsZoomOpen(true)}
              title="Zvětšit kartu pro detailní náhled"
              className="text-[#b5a596] hover:text-[#f5ede3] hover:bg-[#382f28]/60 px-2 py-1 sm:px-2.5 sm:py-1.5 text-xs shrink-0"
            >
              Zvětšit
            </Button>
            <button
              onClick={() => setSelectedCardId(null)}
              className="p-1 text-[#b5a596] hover:text-[#f5ede3] rounded-lg transition-colors ml-0.5 cursor-pointer shrink-0"
              title="Zavřít výběr (Esc)"
            >
              <X className="w-4 h-4" />
            </button>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Horizontální vějíř karet:
          - touch-action: pan-x umožňuje 100% přirozené rolování na mobilu
          - onWheel umožňuje okamžité horizontální posouvání kolečkem myši
          - min-w-full + w-max řeší flexbox data loss bug (vždy lze doscrolovat úplně doleva k první kartě) */}
      <div className="w-full relative flex items-center justify-center">
        {/* Mobilní pomocné šipky pro rolování karet v ruce */}
        {hand.length > 3 && (
          <>
            <button
              type="button"
              onClick={() => {
                scrollContainerRef.current?.scrollBy({ left: -140, behavior: "smooth" });
              }}
              className="absolute left-1 bottom-8 z-40 p-1.5 rounded-full bg-[#201813]/90 border border-amber-600/50 text-amber-300 hover:text-white transition-all shadow-lg active:scale-90"
              title="Posunout doleva"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={() => {
                scrollContainerRef.current?.scrollBy({ left: 140, behavior: "smooth" });
              }}
              className="absolute right-1 bottom-8 z-40 p-1.5 rounded-full bg-[#201813]/90 border border-amber-600/50 text-amber-300 hover:text-white transition-all shadow-lg active:scale-90"
              title="Posunout doprava"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </>
        )}

        <div
          ref={scrollContainerRef}
          onWheel={handleWheel}
          className="w-full overflow-x-auto overflow-y-visible pb-2 pt-2 sm:pb-3 sm:pt-3 md:pb-5 md:pt-4 px-2 sm:px-6 md:px-10 scroll-smooth touch-pan-x"
          style={{
            WebkitOverflowScrolling: "touch",
            scrollbarWidth: "none",
          }}
        >
          <div className="flex items-end min-w-full w-max justify-center gap-1.5 sm:gap-3 md:gap-4 lg:gap-5 px-1 sm:px-2">
            <AnimatePresence mode="popLayout">
              {hand.map((card, index) => {
                const isSel = selectedCardId === card.id;
                const isBeingDragged = draggedCard?.id === card.id;
                const shortcutKey = index < 9 ? `${index + 1}` : undefined;

                return (
                  <div
                    key={card.id}
                    tabIndex={0}
                    role="button"
                    aria-label={translateServerText(card.name)}
                    aria-pressed={isSel}
                    onPointerDown={(e) => handlePointerDown(card, e)}
                    onClick={() => setSelectedCardId(isSel ? null : card.id)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " " || e.key === "Spacebar") {
                        e.preventDefault();
                        setSelectedCardId(isSel ? null : card.id);
                      }
                    }}
                    className={`shrink-0 relative cursor-grab active:cursor-grabbing select-none transition-transform duration-100 rounded-[6px] focus-visible:ring-2 focus-visible:ring-amber-400 focus-visible:outline-none ${
                      isBeingDragged ? "opacity-30 scale-95" : "hover:-translate-y-2 hover:z-20"
                    }`}
                    style={{ touchAction: "pan-x" }}
                  >
                    <CardView
                      image={card.image}
                      name={translateServerText(card.name)}
                      size="md"
                      isPlayable={card.isPlayable}
                      isSelected={isSel}
                      badge={
                        shortcutKey ? (
                          <Badge
                            variant="neutral"
                            size="sm"
                            className="bg-black/75 text-[#f5ede3] text-[9px] px-1 py-0 font-mono"
                          >
                            {shortcutKey}
                          </Badge>
                        ) : undefined
                      }
                    />
                  </div>
                );
              })}
            </AnimatePresence>
          </div>
        </div>
      </div>

      {/* Detailní zvětšení vybrané karty pro snadné čtení */}
      {isZoomOpen && selectedCard && (
        <ModalFrame
          isOpen={true}
          onClose={() => setIsZoomOpen(false)}
          title={translateServerText(selectedCard.name)}
          maxWidth="sm"
        >
          <div className="flex flex-col items-center justify-center p-3 space-y-4">
            <CardView
              image={selectedCard.image}
              name={translateServerText(selectedCard.name)}
              size="lg"
              className="shadow-2xl"
            />
            <div className="w-full flex justify-end">
              <Button variant="secondary" onClick={() => setIsZoomOpen(false)}>
                Zavřít
              </Button>
            </div>
          </div>
        </ModalFrame>
      )}
    </div>
  );
};

export default HandCards;
