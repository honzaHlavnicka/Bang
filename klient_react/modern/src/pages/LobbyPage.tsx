import React, { useState, useEffect } from "react";
import { useGame, translateServerText } from "@bang/core";
import { t, useCurrentLanguage } from "../i18n";
import { Button, Input, Badge } from "../design-system";
import { CardsEmblem } from "../components/CardsEmblem";
import { LanguageSwitcher } from "../components/LanguageSwitcher";
import {
  Play,
  PlusCircle,
  Users,
  RotateCcw,
  User,
  Sparkles,
  CheckCircle2,
  Zap,
  Shield,
  Layers,
} from "lucide-react";

export const LobbyPage: React.FC = () => {
  useCurrentLanguage();
  const { gameState, connectToGame, createGame, returnToGame, isConnected } = useGame();

  const [name, setName] = useState(() => (typeof localStorage !== "undefined" ? localStorage.getItem("playerName") || "" : ""));
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
      setError(t("lobby.errorNickname"));
      return;
    }
    if (!gameCode.trim()) {
      setError(t("lobby.errorCode"));
      return;
    }
    setError(null);
    localStorage.setItem("playerName", name.trim());
    connectToGame(gameCode.trim(), name.trim());
  };

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError(t("lobby.errorNickname"));
      return;
    }
    setError(null);
    localStorage.setItem("playerName", name.trim());
    createGame(selectedGameTypeId, name.trim());
  };

  const gameTypes = gameState.gameTypesAvailable || [
    { id: 0, name: "Bang!", description: "Klasická divokozápadní karetní přestřelka pro 4 až 7 hráčů." },
    { id: 1, name: "Bang! – neomezené karty, pivo vždy", description: "Verze Bangu s volnějšími pravidly." },
    { id: 3, name: "Prší", description: "Tradiční česká karetní hra na svršky a esa." },
    { id: 6, name: "UNO!", description: "Svižná karetní párty hra." },
  ];

  return (
    <div className="min-h-screen flex flex-col bg-[#14100d] text-[#f5ede3] selection:bg-amber-600/30">
      {/* 1. Horní navigační lišta */}
      <header className="w-full border-b border-[#2e241e] bg-[#1a1410]/90 backdrop-blur-md px-4 sm:px-8 py-3 flex items-center justify-between z-20">
        <div className="flex items-center gap-3">
          <CardsEmblem className="w-8 h-8 sm:w-9 sm:h-9" />
          <div className="flex items-center gap-2">
            <span className="font-bold text-lg sm:text-xl tracking-tight text-[#f5ede3]">
              {t("lobby.title")}
            </span>
            <Badge variant="amber" size="sm" className="hidden sm:inline-flex text-[10px] uppercase font-mono">
              Modern
            </Badge>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {/* Indikátor spojení se serverem */}
          <div className="hidden sm:flex items-center gap-1.5 text-xs text-[#b5a596] bg-[#221a15] px-2.5 py-1 rounded-xl border border-[#382f28]">
            <span
              className={`w-2 h-2 rounded-full ${
                isConnected ? "bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.7)]" : "bg-amber-500 animate-ping"
              }`}
            />
            <span>{isConnected ? t("lobby.connectedStatus") : t("lobby.connectingStatus")}</span>
          </div>

          {/* Přepínač jazyků */}
          <LanguageSwitcher />
        </div>
      </header>

      {/* 2. Hlavní obsah: Dvou-sloupcový na desktopu, přehledný na mobilu */}
      <main className="flex-1 max-w-6xl w-full mx-auto p-4 sm:p-6 md:p-8 flex items-center justify-center">
        <div className="w-full grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-10 items-center">
          {/* LEVÝ SLOUPEC: Uvítání, představení her a banner rozehrané hry */}
          <div className="lg:col-span-6 flex flex-col justify-center space-y-5 text-left">
            <div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-600/10 border border-amber-500/30 text-amber-400 text-xs font-semibold mb-3">
                <Sparkles className="w-3.5 h-3.5" />
                <span>{t("lobby.heroTagline")}</span>
              </div>
              <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold tracking-tight text-[#f5ede3] leading-tight">
                Hraj karetní hry s přáteli online
              </h1>
              <p className="text-sm sm:text-base text-[#b5a596] mt-2.5 leading-relaxed max-w-lg">
                {t("lobby.heroDesc")}
              </p>
            </div>

            {/* Banner pro návrat do rozehrané hry */}
            {gameState.isTokenValid && (
              <div className="p-4 rounded-2xl bg-gradient-to-r from-amber-950/60 to-[#281e18] border-2 border-amber-500/80 shadow-[0_0_25px_rgba(217,119,6,0.25)] flex items-center justify-between gap-3 animate-in fade-in">
                <div className="min-w-0">
                  <div className="flex items-center gap-1.5 text-amber-300 font-bold text-sm">
                    <RotateCcw className="w-4 h-4 animate-spin-reverse" />
                    <span>{t("lobby.activeGameTitle")}</span>
                  </div>
                  <span className="text-xs text-[#b5a596] mt-0.5 block truncate">
                    {t("lobby.activeGameDesc")}
                  </span>
                </div>
                <Button
                  type="button"
                  variant="primary"
                  size="md"
                  icon={<RotateCcw className="w-4 h-4" />}
                  onClick={() => returnToGame()}
                  className="shrink-0 shadow-lg ring-2 ring-amber-400/50"
                >
                  {t("lobby.returnToGame")}
                </Button>
              </div>
            )}

            {/* Přehled podporovaných herních stolů */}
            <div className="space-y-2 pt-1 hidden sm:block">
              <span className="text-xs uppercase tracking-wider text-[#7a6c60] font-bold">
                Populární herní stoly
              </span>
              <div className="grid grid-cols-2 gap-2.5">
                <div className="p-3 rounded-xl bg-[#1c1613] border border-[#332921] hover:border-amber-600/40 transition-colors">
                  <div className="flex items-center gap-2">
                    <span className="text-base">🤠</span>
                    <span className="font-bold text-sm text-[#f5ede3]">Bang!</span>
                  </div>
                  <p className="text-[11px] text-[#b5a596] mt-1 leading-snug">
                    Šerif, banditi, přestřelky a tajné role.
                  </p>
                </div>
                <div className="p-3 rounded-xl bg-[#1c1613] border border-[#332921] hover:border-amber-600/40 transition-colors">
                  <div className="flex items-center gap-2">
                    <span className="text-base">🌧️</span>
                    <span className="font-bold text-sm text-[#f5ede3]">Prší</span>
                  </div>
                  <p className="text-[11px] text-[#b5a596] mt-1 leading-snug">
                    Rychlá česká hospodská klasika na svršky a esa.
                  </p>
                </div>
              </div>
            </div>

            {/* Výhody / Feature odznaky */}
            <div className="flex flex-wrap items-center gap-3 pt-2 text-xs text-[#b5a596]">
              <span className="flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                <span>{t("lobby.features.noInstall")}</span>
              </span>
              <span className="flex items-center gap-1.5">
                <Zap className="w-4 h-4 text-amber-400" />
                <span>{t("lobby.features.realtime")}</span>
              </span>
              <span className="flex items-center gap-1.5">
                <Users className="w-4 h-4 text-sky-400" />
                <span>{t("lobby.features.community")}</span>
              </span>
            </div>
          </div>

          {/* PRAVÝ SLOUPEC: Přihlašovací akční karta */}
          <div className="lg:col-span-6 w-full max-w-md mx-auto">
            <div className="bg-[#201915] border border-[#3d3229] rounded-3xl shadow-2xl p-6 sm:p-8 relative overflow-hidden">
              {/* Dekorační teplá záře v rohu */}
              <div className="absolute -top-12 -right-12 w-32 h-32 bg-amber-600/10 rounded-full blur-2xl pointer-events-none" />

              {/* Přepínač záložek: Připojit / Založit */}
              <div className="grid grid-cols-2 p-1 bg-[#14100d] rounded-2xl border border-[#332921] mb-6">
                <button
                  type="button"
                  onClick={() => {
                    setActiveTab("join");
                    setError(null);
                  }}
                  className={`py-2.5 text-xs sm:text-sm font-bold rounded-xl transition-all cursor-pointer ${
                    activeTab === "join"
                      ? "bg-[#2b211a] text-[#f5ede3] shadow-md border border-amber-600/40"
                      : "text-[#b5a596] hover:text-[#f5ede3]"
                  }`}
                >
                  {t("lobby.joinTab")}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setActiveTab("create");
                    setError(null);
                  }}
                  className={`py-2.5 text-xs sm:text-sm font-bold rounded-xl transition-all cursor-pointer ${
                    activeTab === "create"
                      ? "bg-[#2b211a] text-[#f5ede3] shadow-md border border-amber-600/40"
                      : "text-[#b5a596] hover:text-[#f5ede3]"
                  }`}
                >
                  {t("lobby.createTab")}
                </button>
              </div>

              {/* Chybová hláška */}
              {error && (
                <div className="mb-4 p-3 rounded-xl bg-red-950/50 border border-red-800/60 text-red-200 text-xs text-center animate-in fade-in">
                  {error}
                </div>
              )}

              {/* Formulář A: Vstoupit do hry */}
              {activeTab === "join" ? (
                <form onSubmit={handleJoin} className="space-y-4 text-left">
                  <Input
                    label={t("lobby.nicknameLabel")}
                    placeholder={t("lobby.nicknamePlaceholder")}
                    value={name}
                    onChange={(e) => {
                      setName(e.target.value);
                      if (typeof localStorage !== "undefined") {
                        localStorage.setItem("playerName", e.target.value);
                      }
                    }}
                    autoFocus={!name}
                  />
                  <Input
                    label={t("lobby.gameCodeLabel")}
                    placeholder={t("lobby.gameCodePlaceholder")}
                    value={gameCode}
                    onChange={(e) => setGameCode(e.target.value.toUpperCase())}
                    autoFocus={!!name}
                  />
                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    className="w-full justify-center mt-3 py-3"
                    icon={<Play className="w-4 h-4 fill-white" />}
                  >
                    {t("lobby.joinBtn")}
                  </Button>
                </form>
              ) : (
                /* Formulář B: Založit novou hru */
                <form onSubmit={handleCreate} className="space-y-4 text-left">
                  <Input
                    label={t("lobby.nicknameLabel")}
                    placeholder={t("lobby.nicknamePlaceholder")}
                    value={name}
                    onChange={(e) => {
                      setName(e.target.value);
                      if (typeof localStorage !== "undefined") {
                        localStorage.setItem("playerName", e.target.value);
                      }
                    }}
                    autoFocus
                  />

                  <div className="space-y-1.5 text-left">
                    <label className="block text-xs font-semibold text-[#b5a596]">
                      {t("lobby.selectGameLabel")}
                    </label>
                    <div className="space-y-2 max-h-52 overflow-y-auto pr-1">
                      {gameTypes.map((type) => {
                        const isSel = selectedGameTypeId === type.id;
                        return (
                          <div
                            key={type.id}
                            tabIndex={0}
                            role="button"
                            aria-pressed={isSel}
                            onClick={() => setSelectedGameTypeId(type.id)}
                            onKeyDown={(e) => {
                              if (e.key === "Enter" || e.key === " " || e.key === "Spacebar") {
                                e.preventDefault();
                                setSelectedGameTypeId(type.id);
                              }
                            }}
                            className={`p-3 rounded-xl border text-left transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-amber-400 focus-visible:outline-none ${
                              isSel
                                ? "bg-amber-600/20 border-amber-500 ring-1 ring-amber-500 shadow-sm"
                                : "bg-[#16110e] border-[#382f28] hover:border-[#4d4036]"
                            }`}
                          >
                            <div className="flex items-center justify-between">
                              <span className="font-bold text-sm text-[#f5ede3]">{type.name}</span>
                              {isSel && (
                                <Badge variant="amber" size="sm">
                                  {t("lobby.selectedBadge")}
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
                    className="w-full justify-center mt-3 py-3"
                    icon={<PlusCircle className="w-4 h-4" />}
                  >
                    {t("lobby.createBtn")}
                  </Button>
                </form>
              )}

              {/* Spodní zápatí karty */}
              <div className="mt-6 pt-4 border-t border-[#2e241e] text-center text-[11px] text-[#7a6c60]">
                {t("lobby.footerNotice")}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default LobbyPage;
