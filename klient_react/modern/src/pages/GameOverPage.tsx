import React from "react";
import { useGame, translateServerText } from "@bang/core";
import { Button, Badge } from "../design-system";
import { Trophy, RotateCcw, Home, Sparkles, Users } from "lucide-react";

export const GameOverPage: React.FC = () => {
  const { gameState, startNewGameAndDeleteThisOne } = useGame();

  const players = gameState.players || [];
  const winningPlaces = gameState.winningPlaces || [];

  const getPlayerName = (id: number) => {
    return players.find((p) => p.id === id)?.name || `Hráč #${id}`;
  };

  const getPlayerRole = (id: number) => {
    return players.find((p) => p.id === id)?.role;
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-transparent text-[#f5ede3] selection:bg-amber-600/30 select-none">
      <div className="w-full max-w-lg bg-[#211710] border border-[#3d2f25] rounded-2xl shadow-2xl p-6 md:p-8 text-center space-y-6 animate-in fade-in zoom-in-95 duration-200">
        {/* Trofej a titul */}
        <div>
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-[#281810] border border-amber-500/40 text-amber-300 mb-3 shadow-md">
            <Trophy className="w-7 h-7" />
          </div>
          <h2 className="text-2xl md:text-3xl font-extrabold text-[#f5ede3] tracking-tight">
            Konec hry!
          </h2>
          <p className="text-xs text-[#bcae9f] mt-1">
            Zde je konečné pořadí hráčů této partie.
          </p>
        </div>

        {/* Pódium vítězů */}
        <div className="space-y-2.5 py-2">
          {winningPlaces.map((placeGroup, placeIndex) => {
            const medal =
              placeIndex === 0
                ? "🥇 1. místo"
                : placeIndex === 1
                ? "🥈 2. místo"
                : "🥉 3. místo";
            const isWinner = placeIndex === 0;
            const isSecond = placeIndex === 1;

            const placeClass = isWinner
              ? "bg-[#281810] border-amber-500/70 shadow-sm ring-1 ring-amber-500/30"
              : isSecond
              ? "bg-[#1c130d] border-stone-500/30 shadow-xs"
              : "bg-[#18100b] border-[#38281d] shadow-xs";

            return (
              <div
                key={placeIndex}
                className={`p-3.5 rounded-xl border flex items-center justify-between transition-all ${placeClass}`}
              >
                <div className="flex items-center gap-3">
                  <span className="font-bold text-xs uppercase tracking-wider text-amber-300">
                    {medal}
                  </span>
                  <div className="text-left">
                    {placeGroup.map((playerId) => (
                      <div key={playerId} className="font-semibold text-sm text-[#f5ede3]">
                        {getPlayerName(playerId)}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="text-right">
                  {placeGroup.map((playerId) => {
                    const role = getPlayerRole(playerId);
                    return role ? (
                      <span
                        key={playerId}
                        className="text-[11px] px-2 py-0.5 rounded-md bg-[#160e09] border border-[#38281d] text-amber-200/90 font-medium"
                      >
                        {translateServerText(role)}
                      </span>
                    ) : null;
                  })}
                </div>
              </div>
            );
          })}
        </div>

        {/* Akční tlačítka */}
        <div className="pt-4 border-t border-[#382b22] flex flex-col sm:flex-row items-center justify-center gap-3">
          <Button
            variant="secondary"
            icon={<Home className="w-4 h-4" />}
            onClick={() => {
              window.location.href = window.location.pathname;
            }}
          >
            Zpět do lobby
          </Button>

          {gameState.isAdmin ? (
            <Button
              variant="primary"
              icon={<RotateCcw className="w-4 h-4" />}
              onClick={() => startNewGameAndDeleteThisOne()}
            >
              Pozvat ostatní do nové hry
            </Button>
          ) : (
            <div className="text-xs text-[#bcae9f] flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
              <span>Čeká se, až správce založí novou hru...</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GameOverPage;
