import React, { useEffect } from "react";
import { useGame, useDialog, translateServerText } from "@bang/core";
import { CardView, Button, DropZone, Badge } from "../design-system";
import { DiscardDeck } from "./DiscardDeck";
import { useDrag } from "./DragContext";
import { Flame, Layers, CheckCircle2, Info, Sparkles } from "lucide-react";

export const BoardTable: React.FC = () => {
  const { gameState, drawCard, endTurn, clickUIButton, startNewGameAndDeleteThisOne } = useGame();
  const { openDialog } = useDialog();
  const { isDragging, activeDropTarget } = useDrag();

  const isMyTurn = gameState.turnPlayerId === gameState.playerId;
  const talonTopCardUrl = gameState.talonTopCard || "zezadu";
  const discardImages = gameState.discardPile || [];
  const isFireActive = activeDropTarget === "fire";
  const isTableActive = activeDropTarget === "table";

  // Globální klávesová zkratka: Mezerník pro konec tahu, D pro líznutí
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        document.activeElement?.tagName === "INPUT" ||
        document.activeElement?.tagName === "TEXTAREA"
      ) {
        return;
      }

      if (e.code === "Space" && isMyTurn && gameState.allowedUIElements.includes("UKONCENI_TAHU")) {
        e.preventDefault();
        endTurn();
        return;
      }

      if ((e.key === "d" || e.key === "D") && gameState.allowedUIElements.includes("DOBIRACI_BALICEK")) {
        e.preventDefault();
        drawCard();
        return;
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isMyTurn, endTurn, drawCard, gameState.allowedUIElements]);

  return (
    <div className="w-full flex flex-col items-center justify-center p-1.5 sm:p-2 md:p-3 select-none">
      {/* 1. Hlášení ze serveru / Informační stav */}
      {(gameState.gameStateMessege || gameState.gameStateMessegeFull) && (
        <div className="mb-1.5 sm:mb-2 md:mb-3 flex items-center gap-1.5 sm:gap-2 px-3 py-1.5 rounded-xl bg-[#221811] border border-[#382b22] shadow-sm max-w-lg text-center animate-in fade-in duration-150">
          <Info className="w-3.5 h-3.5 sm:w-4 sm:h-4 text-amber-400 shrink-0" />
          <span className="text-[11px] sm:text-xs md:text-sm text-[#f5ede3] font-medium truncate">
            {gameState.gameStateMessege ? translateServerText(gameState.gameStateMessege) : ""}
          </span>
          {gameState.gameStateMessegeFull && (
            <button
              onClick={() =>
                openDialog({
                  type: "INFO",
                  dialogHeader: "Detail stavu hry",
                  data: {
                    message: translateServerText(gameState.gameStateMessegeFull || ""),
                    header: translateServerText(gameState.gameStateMessege || ""),
                  },
                })
              }
              className="text-[11px] sm:text-xs text-amber-400 underline hover:text-amber-300 ml-1 shrink-0 cursor-pointer"
            >
              Více
            </button>
          )}
        </div>
      )}

      {/* 2. Herní stůl – Dobírací balíček, Odhazovací balíček, Spalování */}
      <div className="flex items-end justify-center gap-2.5 sm:gap-6 md:gap-8 py-1 sm:py-2">
        {/* Dobírací balíček (DOBIRACI_BALICEK) */}
        {gameState.allowedUIElements.includes("DOBIRACI_BALICEK") && (
          <div className="flex flex-col items-center">
            <div
              onClick={() => drawCard()}
              className="relative cursor-pointer transition-transform hover:scale-105 active:scale-95 group"
              title="Líznout kartu (klávesa D)"
            >
              <CardView
                image={talonTopCardUrl}
                name="Dobírací balíček"
                size="md"
                className="border-amber-600/40 group-hover:border-amber-400 shadow-xl"
              />
            </div>
            <div className="flex items-center gap-1 sm:gap-1.5 text-[11px] sm:text-xs text-[#bcae9f] font-medium mt-1">
              <Layers className="w-3 h-3 sm:w-3.5 sm:h-3.5" />
              <span className="tabular-nums">{gameState.deckCount || 0}</span>
              <kbd className="hidden sm:inline-block px-1 text-[9px] bg-[#241912] text-[#e8ded3] rounded border border-[#382b22]">
                D
              </kbd>
            </div>
          </div>
        )}

        {/* Odhazovací balíček (ODHAZOVACI_BALICEK) – Realistický stack */}
        {gameState.allowedUIElements.includes("ODHAZOVACI_BALICEK") && (
          <div className="flex flex-col items-center">
            <DiscardDeck
              images={discardImages}
              isDraggingCard={isDragging}
              isActive={isTableActive}
            />
            <span className="text-[11px] sm:text-xs text-[#bcae9f] mt-1 font-medium truncate max-w-[85px] sm:max-w-none">
              Odhazovací balíček
            </span>
          </div>
        )}

        {/* Spalování karet (OHEN) – Přirozený slot v herním stole */}
        {gameState.allowedUIElements.includes("OHEN") && (
          <div className="flex flex-col items-center">
            <div
              id="fire"
              data-dropzone="fire"
              className={`w-16 sm:w-22 md:w-28 aspect-[660/1035] rounded-xl border border-dashed flex flex-col items-center justify-center p-1.5 sm:p-2 text-center transition-all duration-150 cursor-pointer ${
                isFireActive
                  ? "border-red-400 bg-red-950/80 ring-2 ring-red-500 scale-105 shadow-[0_0_20px_rgba(239,68,68,0.7)]"
                  : isDragging
                  ? "border-red-400/80 bg-red-950/50 ring-1 ring-red-500/70 scale-[1.02]"
                  : "border-red-900/50 bg-[#1a0f0d] hover:border-red-700/70 shadow-inner"
              }`}
            >
              <div className="w-6 h-6 sm:w-8 sm:h-8 rounded-full bg-red-950/80 border border-red-800/60 flex items-center justify-center text-red-400 mb-0.5 sm:mb-1">
                <Flame className="w-3.5 h-3.5 sm:w-4 sm:h-4 fill-red-500/40" />
              </div>
              <span className="text-[10px] sm:text-xs font-semibold text-[#f5ede3] leading-tight">
                Spálit kartu
              </span>
              <span className="text-[9px] text-[#bcae9f] mt-0.5 leading-tight hidden sm:block">
                Pusť sem kartu
              </span>
            </div>
            <div className="flex items-center gap-1 text-[11px] sm:text-xs text-red-400/90 font-medium mt-1">
              <span>Spáleniště</span>
              <kbd className="hidden sm:inline-block px-1 text-[9px] bg-[#341410] text-red-300 rounded border border-red-900/50">
                F
              </kbd>
            </div>
          </div>
        )}
      </div>

      {/* 3. Akční tlačítka a dynamická tlačítka */}
      <div className="flex flex-wrap items-center justify-center gap-1.5 sm:gap-2 mt-1.5 sm:mt-2.5">
        {/* Ukončení tahu */}
        {gameState.allowedUIElements.includes("UKONCENI_TAHU") && (
          <Button
            variant={isMyTurn ? "primary" : "secondary"}
            size="md"
            icon={<CheckCircle2 className="w-4 h-4" />}
            shortcutHint="Mezerník"
            disabled={!isMyTurn}
            onClick={() => endTurn()}
            className={`${
              isMyTurn
                ? "ring-2 ring-amber-400/70 shadow-lg animate-pulse"
                : "opacity-60 hidden sm:inline-flex"
            } text-xs sm:text-sm py-1.5 px-3 sm:py-2.5 sm:px-4`}
          >
            {isMyTurn ? "Ukončit svůj tah" : "Čeká se na tah"}
          </Button>
        )}

        {/* Dynamická tlačítka ze serveru (Sid Ketchum, barvy v Prší, UNO) */}
        {gameState.customUIButtons?.map((btn) => (
          <Button
            key={btn.id}
            variant="secondary"
            size="sm"
            disabled={btn.disabled}
            onClick={() => clickUIButton(btn.id)}
            icon={<Sparkles className="w-3.5 h-3.5 text-amber-400" />}
            className="text-xs sm:text-sm py-1 px-2.5 sm:py-2 sm:px-3"
          >
            {translateServerText(btn.text)}
          </Button>
        ))}

        {/* Administrace hry */}
        {gameState.isAdmin && (
          <Button
            variant="ghost"
            size="sm"
            onClick={() => startNewGameAndDeleteThisOne()}
            className="text-[11px] sm:text-xs text-[#b5a596] hover:text-amber-400 py-1 px-2"
          >
            Nová hra / Změnit hru
          </Button>
        )}
      </div>
    </div>
  );
};

export default BoardTable;
