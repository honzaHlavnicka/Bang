import React, { useState, useEffect } from "react";
import { useGame, translateServerText } from "@bang/core";
import { Button, Input, Badge } from "../design-system";
import { Play, PlusCircle, Users, Sparkles, Dices, RotateCcw } from "lucide-react";

export const LobbyPage: React.FC = () => {
  const { gameState, connectToGame, createGame, returnToGame, isConnected } = useGame();

  const [name, setName] = useState(() => localStorage.getItem("playerName") || "");
  const [gameCode, setGameCode] = useState("");
  const [selectedGameTypeId, setSelectedGameTypeId] = useState<number>(0);
  const [activeTab, setActiveTab] = useState<"join" | "create">("join");
  const [error, setError] = useState<string | null>(null);

  // Automatický reconnect a pozvánka do další hry (nahradHru)
  useEffect(() => {
    if (!isConnected) return;

    const autoreturn = sessionStorage.getItem("autoreturn");
    if (autoreturn === "true") {
      sessionStorage.removeItem("autoreturn");
      returnToGame();
      return;
    }

    const autoconnect = sessionStorage.getItem("autoconnect");
    if (autoconnect && autoconnect.includes(",")) {
      const [code, pName] = autoconnect.split(",", 2);
      if (code && pName) {
        setName(pName);
        setGameCode(code);
        sessionStorage.removeItem("autoconnect");
        connectToGame(code, pName);
        return;
      }
    }
  }, [isConnected, connectToGame, returnToGame]);

  // Načtení kódu hry z URL parametrů (?hra=... nebo ?game=...)
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const codeParam = params.get("hra") || params.get("game") || params.get("gameCode");
    if (codeParam) {
      setGameCode(codeParam.trim().toUpperCase());
      setActiveTab("join");
    }
  }, []);

  const handleJoin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError("Zadej prosím své jméno");
      return;
    }
    if (!gameCode.trim()) {
      setError("Zadej kód hry");
      return;
    }
    setError(null);
    localStorage.setItem("playerName", name.trim());
    connectToGame(gameCode.trim(), name.trim());
  };

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError("Zadej prosím své jméno");
      return;
    }
    setError(null);
    localStorage.setItem("playerName", name.trim());
    createGame(selectedGameTypeId, name.trim());
  };

  const gameTypes = gameState.gameTypesAvailable || [
    { id: 0, name: "Bang!", description: "Klasická divokozápadní karetní přestřelka." },
  ];

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-[#161311] text-[#f5ede3] selection:bg-amber-600/30">
      <div className="w-full max-w-md bg-[#221c18] border border-[#382f28] rounded-2xl shadow-2xl p-6 md:p-8 relative overflow-hidden">
        {/* Hlavička */}
        <div className="text-center mb-6">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-amber-600/15 border border-amber-500/30 text-amber-400 mb-3">
            <Dices className="w-6 h-6" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f5ede3]">Karetní hry</h1>
          <p className="text-xs text-[#b5a596] mt-1">
            Hraj Bang!, Prší a další společenské hry online s přáteli.
          </p>
        </div>

        {/* Banner pro návrat do rozehrané hry */}
        {gameState.isTokenValid && (
          <div className="mb-5 p-3.5 rounded-xl bg-amber-950/40 border border-amber-500/60 shadow-lg flex items-center justify-between gap-3 text-left animate-in fade-in">
            <div className="min-w-0">
              <span className="text-xs font-bold text-amber-300 block truncate">
                Máš rozehranou hru!
              </span>
              <span className="text-[11px] text-[#b5a596] block truncate">
                Můžeš se ihned vrátit ke stolu
              </span>
            </div>
            <Button
              type="button"
              variant="primary"
              size="sm"
              icon={<RotateCcw className="w-3.5 h-3.5" />}
              onClick={() => returnToGame()}
              className="shrink-0"
            >
              Vrátit se
            </Button>
          </div>
        )}

        {/* Přepínač záložek: Připojit / Založit */}
        <div className="grid grid-cols-2 p-1 bg-[#1a1512] rounded-xl border border-[#332a24] mb-5">
          <button
            type="button"
            onClick={() => {
              setActiveTab("join");
              setError(null);
            }}
            className={`py-2 text-xs font-semibold rounded-lg transition-all cursor-pointer ${
              activeTab === "join"
                ? "bg-[#2d2520] text-[#f5ede3] shadow-sm border border-[#423830]"
                : "text-[#b5a596] hover:text-[#f5ede3]"
            }`}
          >
            Vstoupit do hry
          </button>
          <button
            type="button"
            onClick={() => {
              setActiveTab("create");
              setError(null);
            }}
            className={`py-2 text-xs font-semibold rounded-lg transition-all cursor-pointer ${
              activeTab === "create"
                ? "bg-[#2d2520] text-[#f5ede3] shadow-sm border border-[#423830]"
                : "text-[#b5a596] hover:text-[#f5ede3]"
            }`}
          >
            Založit novou hru
          </button>
        </div>

        {/* Chybová hláška */}
        {error && (
          <div className="mb-4 p-2.5 rounded-xl bg-red-950/40 border border-red-800/50 text-red-300 text-xs text-center">
            {error}
          </div>
        )}

        {/* Formulář: Vstoupit do hry */}
        {activeTab === "join" ? (
          <form onSubmit={handleJoin} className="space-y-4">
            <Input
              label="Tvoje přezdívka"
              placeholder="Např. Honza"
              value={name}
              onChange={(e) => setName(e.target.value)}
              autoFocus={!name}
            />
            <Input
              label="Kód hry"
              placeholder="Např. ABCD"
              value={gameCode}
              onChange={(e) => setGameCode(e.target.value.toUpperCase())}
              autoFocus={!!name}
            />
            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full justify-center mt-2"
              icon={<Play className="w-4 h-4 fill-white" />}
            >
              Vstoupit do hry
            </Button>
          </form>
        ) : (
          /* Formulář: Založit novou hru */
          <form onSubmit={handleCreate} className="space-y-4">
            <Input
              label="Tvoje přezdívka"
              placeholder="Např. Honza"
              value={name}
              onChange={(e) => setName(e.target.value)}
              autoFocus
            />

            <div className="space-y-1.5 text-left">
              <label className="block text-xs font-medium text-[#b5a596]">Vyber hru</label>
              <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
                {gameTypes.map((type) => {
                  const isSel = selectedGameTypeId === type.id;
                  return (
                    <div
                      key={type.id}
                      onClick={() => setSelectedGameTypeId(type.id)}
                      className={`p-3 rounded-xl border text-left transition-all cursor-pointer ${
                        isSel
                          ? "bg-amber-600/15 border-amber-500 ring-1 ring-amber-500/80"
                          : "bg-[#1a1512] border-[#382f28] hover:border-[#4d4036]"
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-semibold text-sm text-[#f5ede3]">{type.name}</span>
                        {isSel && (
                          <Badge variant="amber" size="sm">
                            Zvoleno
                          </Badge>
                        )}
                      </div>
                      {type.description && (
                        <p className="text-xs text-[#b5a596] mt-1 line-clamp-2">
                          {type.description}
                        </p>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>

            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full justify-center mt-2"
              icon={<PlusCircle className="w-4 h-4" />}
            >
              Vytvořit hru
            </Button>
          </form>
        )}

        {/* Spodní nápověda */}
        <div className="mt-6 pt-4 border-t border-[#332a24] text-center text-[11px] text-[#7a6c60]">
          Hra běží ve vašem prohlížeči • Žádná instalace ani registrace
        </div>
      </div>
    </div>
  );
};

export default LobbyPage;
