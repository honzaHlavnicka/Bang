import React, { useState } from "react";
import { useGame, useDialog, type Player, type CardType, translateServerText } from "@bang/core";
import { CardView, Badge, ModalFrame, Button } from "../design-system";
import { useDrag } from "./DragContext";
import { Heart, Layers, Crown, Shield, User, HelpCircle, Eye, Bot, WifiOff } from "lucide-react";
import { getCharacterImagePath } from "../utils/characterImage";

export const PlayersArea: React.FC = () => {
  const { gameState } = useGame();
  const { openDialog } = useDialog();
  const { isDragging, activeDropTarget } = useDrag();
  const [inspectingPlayer, setInspectingPlayer] = useState<Player | null>(null);

  const otherPlayers = (gameState.players || []).filter((p) => p.id !== gameState.playerId);

  if (otherPlayers.length === 0) {
    return null;
  }

  // Pomocná funkce pro vyhledání schopnosti postavy z gameState.characters
  const getCharacterDesc = (characterName?: string) => {
    if (!characterName) return null;
    return gameState.characters?.find(
      (c) =>
        c.jmeno.toLowerCase() === characterName.toLowerCase() ||
        c.obrazek.toLowerCase() === characterName.toLowerCase()
    );
  };

  return (
    <div className="w-full px-2 py-1 select-none">
      <div className="flex items-stretch justify-start md:justify-center gap-2 sm:gap-3 lg:gap-4 overflow-x-auto pb-2 scroll-smooth">
        {otherPlayers.map((player: Player) => {
          const isTurn = player.id === gameState.turnPlayerId;
          const dropZoneId = `player-${player.id}`;
          const hasLives = gameState.allowedUIElements.includes("ZIVOTY");
          const showCharacter = gameState.allowedUIElements.includes("POSTAVA");
          const hasRole = gameState.allowedUIElements.includes("ROLE");
          const isDead = hasLives && player.health <= 0;
          const isOffline = player.isOnline === false;
          const charInfo = getCharacterDesc(player.character);
          const charImg = getCharacterImagePath(player.character, gameState.characters);

          const isPlayerHovered = activeDropTarget === dropZoneId;

          return (
            <div
              key={player.id}
              id={dropZoneId}
              data-dropzone={dropZoneId}
              className={`shrink-0 relative flex flex-col justify-start p-1.5 sm:p-2 md:p-2.5 rounded-xl border transition-all duration-150 min-w-[130px] sm:min-w-[160px] md:min-w-[180px] max-w-[260px] ${
                isPlayerHovered
                  ? "bg-[#341b10] border-amber-400 ring-2 ring-amber-400 shadow-[0_0_20px_rgba(245,158,11,0.5)] scale-[1.02]"
                  : isDead
                  ? "bg-[#160e0a]/80 border-[#2e2017] opacity-55 grayscale-[0.6]"
                  : isOffline
                  ? "bg-[#160e0a]/80 border-[#33251c] opacity-50 grayscale-[0.5]"
                  : isTurn
                  ? "bg-[#2c1e15] border-amber-500/80 shadow-[0_0_15px_rgba(217,119,6,0.25)] ring-1 ring-amber-400/50"
                  : "bg-[#201711] border-[#382b22] hover:border-[#4d3c30]"
              } ${isDragging && !isPlayerHovered ? "hover:border-amber-400/80" : ""}`}
            >
              {/* 1. Hlavička hráče: jméno, admin a indikátor tahu */}
              <div className="flex items-center justify-between gap-1 mb-1 sm:mb-1.5">
                <div className="flex items-center gap-1 sm:gap-1.5 min-w-0">
                  {player.isAdmin && (
                    <span title="Správce místnosti">
                      <Crown className="w-3 h-3 sm:w-3.5 sm:h-3.5 text-amber-400 shrink-0" />
                    </span>
                  )}
                  {player.isBot && (
                    <span title="Počítačový hráč (Bot)">
                      <Bot className="w-3 h-3 sm:w-3.5 sm:h-3.5 text-sky-400 shrink-0" />
                    </span>
                  )}
                  <span className="font-bold text-xs md:text-sm text-[#f5ede3] truncate">
                    {player.name}
                  </span>
                </div>
                <div className="flex items-center gap-1 shrink-0">
                  {isOffline && (
                    <Badge variant="neutral" size="sm" className="bg-red-950/70 text-red-300 border-red-800/60 text-[9px] px-1 py-0">
                      Off
                    </Badge>
                  )}
                  {isTurn && (
                    <Badge variant="amber" size="sm" dot className="animate-pulse text-[9px] sm:text-xs px-1 sm:px-1.5 py-0">
                      <span className="sm:hidden">Tah</span>
                      <span className="hidden sm:inline">Na tahu</span>
                    </Badge>
                  )}
                  {isDead && (
                    <Badge variant="terracotta" size="sm" className="text-[9px] sm:text-xs px-1 sm:px-1.5 py-0">
                      Mrtvý
                    </Badge>
                  )}
                </div>
              </div>

              {/* 2. Herní stanoviště: Postava a statistiky */}
              <div className="flex items-center gap-1.5 sm:gap-2 mb-1 sm:mb-1.5">
                {/* Karta postavy (pouze pokud je povolena v UI) */}
                {showCharacter && player.character && (
                  <div
                    onClick={() => setInspectingPlayer(player)}
                    className="relative cursor-pointer transition-transform hover:scale-105 group shrink-0"
                    title="Klikni pro detail schopnosti postavy"
                  >
                    <CardView
                      image={charImg}
                      name={player.character}
                      size="xs"
                      className="border-amber-600/50 group-hover:border-amber-400"
                    />
                    <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 flex items-center justify-center rounded-[4px] transition-opacity">
                      <Eye className="w-3 h-3 sm:w-3.5 sm:h-3.5 text-white drop-shadow" />
                    </div>
                  </div>
                )}

                <div className="flex-1 min-w-0 text-left">
                  {showCharacter && player.character && (
                    <div className="flex items-center justify-between">
                      <span
                        onClick={() => setInspectingPlayer(player)}
                        className="font-semibold text-[11px] sm:text-xs text-[#f5ede3] hover:text-amber-400 cursor-pointer truncate block"
                      >
                        {translateServerText(player.character)}
                      </span>
                    </div>
                  )}

                  {hasRole && player.role && (
                    <span className="inline-block text-[9px] sm:text-[10px] font-medium px-1 sm:px-1.5 py-0 sm:py-0.5 rounded bg-[#2b221c] border border-[#3d3229] text-amber-300 mt-0.5 truncate max-w-[80px] sm:max-w-none">
                      {translateServerText(player.role)}
                    </span>
                  )}

                  {/* Životy a karty v ruce */}
                  <div className="flex items-center gap-2 sm:gap-3 mt-1 text-xs">
                    {hasLives && (
                      <div className="flex items-center gap-0.5 sm:gap-1 text-red-400 font-bold tabular-nums text-[11px] sm:text-xs">
                        <Heart className="w-3 h-3 sm:w-3.5 sm:h-3.5 fill-red-500 text-red-500" />
                        <span>{player.health}</span>
                      </div>
                    )}
                    <div className="flex items-center gap-0.5 sm:gap-1 text-[#b5a596] font-medium tabular-nums text-[11px] sm:text-xs">
                      <Layers className="w-3 h-3 sm:w-3.5 sm:h-3.5" />
                      <span>{player.cardsInHand}</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* 3. Vyložené karty před hráčem (VYLOZENE_KARTY) */}
              {gameState.allowedUIElements.includes("VYLOZENE_KARTY") &&
                player.inPlayCards &&
                player.inPlayCards.length > 0 && (
                  <div className="mt-1 sm:mt-1.5 pt-1 sm:pt-1.5 border-t border-[#332922]">
                    <div className="flex items-center gap-1 sm:gap-1.5 overflow-x-auto py-0.5">
                      {player.inPlayCards.map((card: CardType) => (
                        <div
                          key={card.id}
                          onClick={() => setInspectingPlayer(player)}
                          className="shrink-0 cursor-pointer hover:scale-105 transition-transform"
                          title={`${translateServerText(card.name)} (klikni pro detail)`}
                        >
                          <CardView
                            image={card.image}
                            name={translateServerText(card.name)}
                            size="xs"
                          />
                        </div>
                      ))}
                    </div>
                  </div>
                )}
            </div>
          );
        })}
      </div>

      {/* Detailní modal pro prohlížení schopnosti protihráče */}
      {inspectingPlayer && (
        <ModalFrame
          isOpen={true}
          onClose={() => setInspectingPlayer(null)}
          title={`Hráč: ${inspectingPlayer.name}`}
          maxWidth="sm"
        >
          <div className="flex flex-col items-center text-center space-y-3 py-2">
            {gameState.allowedUIElements.includes("POSTAVA") && inspectingPlayer.character && (
              <>
                <CardView
                  image={getCharacterImagePath(inspectingPlayer.character, gameState.characters)}
                  name={inspectingPlayer.character}
                  size="lg"
                  className="shadow-2xl"
                />
                <div>
                  <h4 className="text-base font-bold text-[#f5ede3]">
                    {translateServerText(inspectingPlayer.character)}
                  </h4>
                  {gameState.allowedUIElements.includes("ROLE") && inspectingPlayer.role && (
                    <Badge variant="amber" size="sm" className="mt-1">
                      Role: {translateServerText(inspectingPlayer.role)}
                    </Badge>
                  )}
                </div>
                {getCharacterDesc(inspectingPlayer.character)?.popis && (
                  <p className="text-xs text-[#bcae9f] bg-[#170f0a] p-3 rounded-xl border border-[#3d2c1e] leading-relaxed shadow-inner">
                    {getCharacterDesc(inspectingPlayer.character)!.popis}
                  </p>
                )}
              </>
            )}
            <div className="flex items-center gap-4 text-xs font-semibold pt-1">
              {gameState.allowedUIElements.includes("ZIVOTY") && (
                <span className="text-red-400 flex items-center gap-1">
                  <Heart className="w-4 h-4 fill-red-500" /> Životy: {inspectingPlayer.health}
                </span>
              )}
              <span className="text-[#b5a596] flex items-center gap-1">
                <Layers className="w-4 h-4" /> Karty v ruce: {inspectingPlayer.cardsInHand}
              </span>
            </div>
            <Button
              variant="secondary"
              size="sm"
              className="w-full mt-2"
              onClick={() => setInspectingPlayer(null)}
            >
              Zavřít
            </Button>
          </div>
        </ModalFrame>
      )}
    </div>
  );
};

export default PlayersArea;
