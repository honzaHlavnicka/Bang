import React, { useState } from "react";
import { useGame, useDialog, translateServerText, type CardType } from "@bang/core";
import { CardView, Badge, ModalFrame, Button } from "../design-system";
import { Heart, Shield, User, HelpCircle, Eye, Flame, Play } from "lucide-react";
import { useDrag } from "./DragContext";
import { getCharacterImagePath } from "../utils/characterImage";

export const MyStatus: React.FC = () => {
  const { gameState, fireCard, playCard } = useGame();
  const { startDrag, draggedCard } = useDrag();
  const { openDialog } = useDialog();
  const [isCharacterModalOpen, setIsCharacterModalOpen] = useState(false);
  const [selectedInPlayCard, setSelectedInPlayCard] = useState<CardType | null>(null);

  const health = gameState.health ?? 0;
  const maxHealth = gameState.maxHealth ?? health;
  const isMyTurn = gameState.turnPlayerId === gameState.playerId;

  // Hledání popisu postavy z gameState.characters
  const characterInfo = gameState.characters?.find(
    (c) =>
      c.jmeno.toLowerCase() === gameState.character?.toLowerCase() ||
      c.obrazek.toLowerCase() === gameState.character?.toLowerCase()
  );

  const characterImage = getCharacterImagePath(gameState.character, gameState.characters);

  const handleInPlayPointerDown = (card: CardType, e: React.PointerEvent) => {
    if (e.pointerType === "mouse") {
      if (e.button === 0) {
        startDrag(card, "inplay", e);
      }
      return;
    }

    // Dotyková gesta pro vyložené karty
    const startY = e.clientY;
    let dragTriggered = false;

    const onPointerMove = (evt: PointerEvent) => {
      const dy = evt.clientY - startY;
      if (!dragTriggered && dy < -12) {
        dragTriggered = true;
        cleanUp();
        startDrag(card, "inplay", evt as unknown as React.PointerEvent);
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

  return (
    <div className="w-full px-2 sm:px-3 py-1 sm:py-1.5 flex flex-wrap sm:flex-nowrap items-center justify-between gap-1.5 sm:gap-2.5 text-xs border-t border-[#2e2620] bg-[#1a1512]/95 select-none z-20">
      {/* 1. Postava a Role */}
      <div className="flex items-center gap-1.5 sm:gap-2.5">
        {/* Postava (POSTAVA) */}
        {gameState.allowedUIElements.includes("POSTAVA") && gameState.character && (
          <div
            onClick={() => setIsCharacterModalOpen(true)}
            className="flex items-center gap-1.5 sm:gap-2 px-2 sm:px-2.5 py-0.5 sm:py-1 rounded-xl bg-[#261f1a] border border-[#3f342b] hover:border-amber-500/70 transition-colors cursor-pointer group shadow-sm"
            title="Klikni pro zobrazení schopnosti postavy"
          >
            <div className="w-5 h-7 sm:w-6 sm:h-8 rounded-[4px] overflow-hidden shrink-0 border border-amber-600/40">
              <img
                src={characterImage}
                alt={gameState.character}
                className="w-full h-full object-cover"
              />
            </div>
            <span className="font-bold text-xs md:text-sm text-[#f5ede3] group-hover:text-amber-300 transition-colors max-w-[90px] sm:max-w-none truncate">
              {translateServerText(gameState.character)}
            </span>
            <HelpCircle className="w-3.5 h-3.5 text-amber-400/80 shrink-0" />
          </div>
        )}

        {/* Role (ROLE) */}
        {gameState.allowedUIElements.includes("ROLE") && gameState.role && (
          <div className="flex items-center gap-1 sm:gap-1.5 px-2 sm:px-2.5 py-0.5 sm:py-1 rounded-xl bg-[#261f1a] border border-[#3f342b]">
            <Shield className="w-3 h-3 sm:w-3.5 sm:h-3.5 text-amber-400 shrink-0" />
            <span className="text-[#b5a596] hidden sm:inline">Role:</span>
            <span className="font-semibold text-[#f5ede3] text-[11px] sm:text-xs">
              {translateServerText(gameState.role)}
            </span>
          </div>
        )}

        {/* Indikátor vlastního tahu */}
        {isMyTurn && (
          <Badge variant="sage" size="sm" dot className="animate-pulse">
            Tvůj tah
          </Badge>
        )}
      </div>

      {/* 2. Životy (ZIVOTY) */}
      {gameState.allowedUIElements.includes("ZIVOTY") && (
        <div className="flex items-center gap-1.5 sm:gap-2 px-2 sm:px-3 py-0.5 sm:py-1 rounded-xl bg-[#261f1a] border border-[#3f342b]">
          <Heart className="w-3.5 h-3.5 sm:w-4 sm:h-4 fill-red-500 text-red-500 shrink-0" />
          <span className="font-bold text-xs sm:text-sm text-[#f5ede3] tabular-nums">
            {health} / {maxHealth}
          </span>
          <div className="hidden sm:flex items-center gap-1 ml-1">
            {Array.from({ length: Math.max(0, maxHealth) }).map((_, i) => (
              <span
                key={i}
                className={`w-2.5 h-2.5 rounded-full transition-colors ${
                  i < health ? "bg-red-500 shadow-[0_0_6px_rgba(239,68,68,0.5)]" : "bg-[#3d2e28]"
                }`}
              />
            ))}
          </div>
        </div>
      )}

      {/* 3. Vyložené karty přede mnou (VYLOZENE_KARTY) */}
      {gameState.allowedUIElements.includes("VYLOZENE_KARTY") && (
        <div
          id="inplay"
          data-dropzone="inplay"
          className="flex items-center gap-1.5 p-1 rounded-xl border border-dashed border-[#3d3229] bg-[#211a15]/80 min-h-[40px] sm:min-h-[48px] 2xl:min-w-[360px] 3xl:min-w-[480px] 2xl:flex-1 2xl:max-w-xl 2xl:justify-center 2xl:py-2.5 2xl:px-4"
          title="Tvoje vyložené karty (klikni na kartu pro akce nebo ji přetáhni)"
        >
          {gameState.inPlayCards && gameState.inPlayCards.length > 0 ? (
            <div className="flex items-center gap-1.5 2xl:gap-3">
              {gameState.inPlayCards.map((card) => {
                const isBeingDragged = draggedCard?.id === card.id;

                return (
                  <div
                    key={card.id}
                    tabIndex={0}
                    role="button"
                    aria-label={`Vyložená karta: ${translateServerText(card.name)}`}
                    onPointerDown={(e) => handleInPlayPointerDown(card, e)}
                    onClick={() => setSelectedInPlayCard(card)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " " || e.key === "Spacebar") {
                        e.preventDefault();
                        setSelectedInPlayCard(card);
                      }
                    }}
                    className={`cursor-pointer shrink-0 select-none rounded-[5px] focus-visible:ring-2 focus-visible:ring-amber-400 focus-visible:outline-none ${
                      isBeingDragged ? "opacity-30 scale-95" : "hover:-translate-y-1 hover:z-20"
                    }`}
                    title={`${translateServerText(card.name)} (klikni pro akce nebo přetáhni)`}
                  >
                    <CardView image={card.image} name={translateServerText(card.name)} size="sm" />
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="flex items-center gap-1.5 text-[#7a6c60] px-2">
              <span className="text-[11px] 2xl:text-xs">Vyložené karty</span>
              <span className="hidden 2xl:inline text-[11px] text-[#54463c]">
                (přetáhni sem modrou nebo zelenou kartu)
              </span>
            </div>
          )}
        </div>
      )}

      {/* Modal pro akce s vyloženou kartou (spálení, zahrání) */}
      {selectedInPlayCard && (
        <ModalFrame
          isOpen={true}
          onClose={() => setSelectedInPlayCard(null)}
          title={`Vyložená karta: ${translateServerText(selectedInPlayCard.name)}`}
          maxWidth="sm"
        >
          <div className="flex flex-col items-center text-center space-y-4 py-2">
            <CardView
              image={selectedInPlayCard.image}
              name={translateServerText(selectedInPlayCard.name)}
              size="lg"
              className="shadow-2xl"
            />
            <div className="space-y-1">
              <h4 className="text-base font-bold text-[#f5ede3]">
                {translateServerText(selectedInPlayCard.name)}
              </h4>
              <p className="text-xs text-[#b5a596]">
                Tato karta je aktuálně vyložená před tebou na stole.
              </p>
            </div>

            <div className="w-full flex flex-col gap-2 pt-2">
              {gameState.allowedUIElements.includes("OHEN") && (
                <Button
                  variant="terracotta"
                  size="md"
                  className="w-full justify-center"
                  icon={<Flame className="w-4 h-4" />}
                  shortcutHint="F"
                  onClick={() => {
                    fireCard(selectedInPlayCard.id);
                    setSelectedInPlayCard(null);
                  }}
                >
                  Spálit tuto vyloženou kartu
                </Button>
              )}

              {selectedInPlayCard.isPlayable && (
                <Button
                  variant="primary"
                  size="md"
                  className="w-full justify-center"
                  icon={<Play className="w-4 h-4" />}
                  onClick={() => {
                    playCard(selectedInPlayCard.id);
                    setSelectedInPlayCard(null);
                  }}
                >
                  Zahrát kartu
                </Button>
              )}

              <Button
                variant="secondary"
                size="md"
                className="w-full justify-center"
                onClick={() => setSelectedInPlayCard(null)}
              >
                Zavřít
              </Button>
            </div>
          </div>
        </ModalFrame>
      )}

      {/* Detailní modal vlastní postavy */}
      {isCharacterModalOpen && gameState.character && (
        <ModalFrame
          isOpen={true}
          onClose={() => setIsCharacterModalOpen(false)}
          title={`Tvoje postava: ${gameState.character}`}
          maxWidth="sm"
        >
          <div className="flex flex-col items-center text-center space-y-3 py-2">
            <CardView
              image={characterImage}
              name={gameState.character}
              size="lg"
              className="shadow-2xl"
            />
            {characterInfo?.popis ? (
              <p className="text-xs text-[#b5a596] bg-[#1a1410] p-3 rounded-xl border border-[#382f28] leading-relaxed">
                {characterInfo.popis}
              </p>
            ) : (
              <p className="text-xs text-[#b5a596] italic">
                Schopnost postavy se aktivuje automaticky během hry.
              </p>
            )}
            <Button
              variant="primary"
              size="sm"
              className="w-full mt-2"
              onClick={() => setIsCharacterModalOpen(false)}
            >
              Rozumím
            </Button>
          </div>
        </ModalFrame>
      )}
    </div>
  );
};

export default MyStatus;
