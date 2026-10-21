import React, { useState } from "react";
import { useGame, translateServerText } from "@bang/core";
import { Button, Badge, CardView, ModalFrame, Input } from "../design-system";
import { CharacterPicker } from "../components/CharacterPicker";
import { Copy, Check, Crown, Play, UserX, UserCheck, Edit2, LogOut, Bot } from "lucide-react";

export const WaitingRoomPage: React.FC = () => {
  const { gameState, startGame, chooseCharacter, kickPlayer, changePlayerName, startNewGameAndDeleteThisOne } = useGame();
  const [copied, setCopied] = useState(false);
  const [isEditNameOpen, setIsEditNameOpen] = useState(false);
  const [newName, setNewName] = useState(gameState.name || "");

  const gameCode = gameState.gameCode || "";
  const players = gameState.players || [];
  const characters = gameState.characters || [];
  const isMyCharacterChosen = !!gameState.character;

  const handleCopyLink = () => {
    const url = `${window.location.origin}${window.location.pathname}?hra=${gameCode}`;
    navigator.clipboard.writeText(url).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

  const handleSaveName = (e: React.FormEvent) => {
    e.preventDefault();
    if (newName.trim() && newName.trim() !== gameState.name) {
      changePlayerName(newName.trim());
    }
    setIsEditNameOpen(false);
  };

  return (
    <div className="min-h-screen p-4 md:p-8 flex flex-col items-center justify-center bg-transparent text-[#f5ede3] selection:bg-amber-600/30 select-none">
      <div className="w-full max-w-2xl bg-[#211710] border border-[#3d2f25] rounded-2xl shadow-2xl p-6 md:p-8 space-y-6">
        {/* Hlavička místnosti s kódem hry */}
        <div className="flex flex-wrap items-center justify-between gap-4 pb-4 border-b border-[#382b22]">
          <div>
            <span className="text-xs text-[#bcae9f]">Čekací místnost</span>
            <h2 className="text-2xl font-bold tracking-tight text-[#f5ede3] flex items-center gap-2.5 mt-0.5">
              <span>Hra</span>
              <span className="font-mono text-amber-200 bg-[#281810] px-3.5 py-0.5 rounded-xl border border-amber-600/50 shadow-sm tracking-widest">
                {gameCode}
              </span>
            </h2>
          </div>

          <div className="flex items-center gap-2">
            <Button
              variant="secondary"
              size="sm"
              icon={copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
              onClick={handleCopyLink}
            >
              {copied ? "Zkopírováno!" : "Pozvat hráče"}
            </Button>
            <Button
              variant="ghost"
              size="sm"
              icon={<Edit2 className="w-4 h-4" />}
              onClick={() => {
                setNewName(gameState.name || "");
                setIsEditNameOpen(true);
              }}
              title="Změnit svou přezdívku"
            >
              Změnit jméno
            </Button>
          </div>
        </div>

        {/* Výběr postavy */}
        <CharacterPicker />

        {/* Vybraná postava po volbě */}
        {gameState.character && gameState.allowedUIElements.includes("POSTAVA") && (
          <div className="p-3.5 rounded-xl bg-[#170e09] border border-[#33241a] flex items-center justify-between shadow-sm">
            <span className="text-xs text-[#bcae9f]">Tvoje vybraná postava:</span>
            <Badge variant="amber" size="md">
              {gameState.character}
            </Badge>
          </div>
        )}

        {/* Seznam připojených hráčů */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-[#f7eee4] flex items-center gap-2">
              <span>Připojení hráči</span>
              <span className="text-xs text-[#bcae9f]">({players.length})</span>
            </h3>
            <span className="text-xs text-[#8c7b6c]">Hra vyžaduje alespoň 2 hráče</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
            {players.map((p) => {
              const isMe = p.id === gameState.playerId;
              return (
                <div
                  key={p.id}
                  className={`flex items-center justify-between p-3 rounded-xl border shadow-xs transition-all ${
                    isMe
                      ? "bg-[#281a11] border-amber-500/60 text-[#f5ede3]"
                      : "bg-[#18100a] border-[#33241a] text-[#bcae9f]"
                  }`}
                >
                  <div className="flex items-center gap-2.5 min-w-0">
                    <div
                      className={`w-2.5 h-2.5 rounded-full shrink-0 ${
                        p.isOnline === false
                          ? "bg-red-500 shadow-[0_0_6px_rgba(239,68,68,0.5)]"
                          : "bg-emerald-400 shadow-[0_0_6px_rgba(52,211,153,0.5)]"
                      }`}
                      title={p.isOnline === false ? "Odpojen" : "Připojen"}
                    />
                    <span className="font-semibold text-sm truncate text-[#f5ede3]">
                      {p.name} {isMe && "(Ty)"}
                    </span>
                    {p.isOnline === false && (
                      <span className="text-[10px] text-red-400 font-medium">(Odpojen)</span>
                    )}
                    {p.isBot && (
                      <span title="Počítačový hráč (Bot)">
                        <Bot className="w-4 h-4 text-sky-400 shrink-0" />
                      </span>
                    )}
                    {p.isAdmin && (
                      <span title="Správce místnosti">
                        <Crown className="w-4 h-4 text-amber-400 shrink-0" />
                      </span>
                    )}
                  </div>

                  {gameState.isAdmin && !isMe && (
                    <button
                      onClick={() => kickPlayer(p.id)}
                      className="p-1 text-red-400 hover:text-red-300 hover:bg-red-950/40 rounded transition-colors cursor-pointer"
                      title="Vyloučit hráče"
                    >
                      <UserX className="w-4 h-4" />
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Spodní akční lišta pro zahájení */}
        <div className="pt-4 border-t border-[#382b22] flex flex-wrap items-center justify-between gap-3">
          <Button
            variant="ghost"
            size="sm"
            icon={<LogOut className="w-4 h-4" />}
            onClick={() => {
              if (window.confirm("Opravdu chceš opustit místnost?")) {
                sessionStorage.removeItem("gameToken");
                localStorage.removeItem("gameToken");
                sessionStorage.removeItem("autoconnect");
                sessionStorage.removeItem("autoreturn");
                window.location.href = window.location.pathname;
              }
            }}
          >
            Opustit hru
          </Button>

          {gameState.isAdmin ? (
            <Button
              variant="primary"
              size="lg"
              icon={<Play className="w-4 h-4 fill-white" />}
              onClick={() => startGame()}
              disabled={players.length < 2}
            >
              {players.length < 2 ? "Čeká se na další hráče" : "Spustit hru"}
            </Button>
          ) : (
            <div className="text-xs text-[#bcae9f] flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
              <span>Čeká se na správce místnosti pro spuštění hry...</span>
            </div>
          )}
        </div>
      </div>

      {/* Dialog změny jména */}
      <ModalFrame
        isOpen={isEditNameOpen}
        onClose={() => setIsEditNameOpen(false)}
        title="Změna přezdívky"
        maxWidth="sm"
      >
        <form onSubmit={handleSaveName} className="space-y-4">
          <Input
            label="Nové jméno"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            autoFocus
          />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="ghost" onClick={() => setIsEditNameOpen(false)}>
              Zrušit
            </Button>
            <Button type="submit" variant="primary">
              Uložit
            </Button>
          </div>
        </form>
      </ModalFrame>
    </div>
  );
};

export default WaitingRoomPage;
