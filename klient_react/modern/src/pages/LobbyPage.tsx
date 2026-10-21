import React, { useState, useEffect } from "react";
import { useGame, translateServerText } from "@bang/core";
import { t, useCurrentLanguage, getLanguage } from "../i18n";
import { Button, Input, Badge } from "../design-system";
import { CardsEmblem } from "../components/CardsEmblem";
import { LanguageSwitcher } from "../components/LanguageSwitcher";
import {
  Play,
  PlusCircle,
  RotateCcw,
  ExternalLink,
} from "lucide-react";

export const LobbyPage: React.FC = () => {
  useCurrentLanguage();
  const currentLang = getLanguage();
  const { gameState, connectToGame, createGame, returnToGame, isConnected } = useGame();

  const [name, setName] = useState(() => {
    if (typeof localStorage === "undefined") return "";
    return localStorage.getItem("savedName") || localStorage.getItem("playerName") || "";
  });
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

  const savePlayerName = (newName: string) => {
    setName(newName);
    if (typeof localStorage !== "undefined") {
      localStorage.setItem("playerName", newName);
      localStorage.setItem("savedName", newName);
    }
  };

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
    savePlayerName(name.trim());
    connectToGame(gameCode.trim(), name.trim());
  };

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError(t("lobby.errorNickname"));
      return;
    }
    setError(null);
    savePlayerName(name.trim());
    createGame(selectedGameTypeId, name.trim());
  };

  const gameTypes = gameState.gameTypesAvailable || [
    { id: 0, name: "Bang!", description: "Klasická divokozápadní karetní přestřelka pro 4 až 7 hráčů." },
    { id: 1, name: "Bang! – neomezené karty, pivo vždy", description: "Verze Bangu s volnějšími pravidly." },
    { id: 3, name: "Prší", description: "Tradiční česká karetní hra na svršky a esa." },
    { id: 6, name: "UNO!", description: "Svižná karetní párty hra." },
  ];

  return (
    <div className="min-h-screen flex flex-col text-[#f5ede3] selection:bg-amber-600/30">
      {/* 1. Horní navigační lišta */}
      <header className="w-full border-b border-[#382b22] bg-[#16100c]/90 px-4 sm:px-8 py-3 flex items-center justify-between z-20 shadow-md">
        <div className="flex items-center gap-3">
          <CardsEmblem className="w-8 h-8 sm:w-9 sm:h-9" />
          <span className="font-bold text-lg sm:text-xl tracking-tight text-[#f5ede3]">
            {t("lobby.title")}
          </span>
        </div>

        <div className="flex items-center gap-3 sm:gap-4">
          {/* Indikátor spojení se serverem */}
          <div className="hidden sm:flex items-center gap-2 text-xs text-[#bcae9f] bg-[#221710] px-3 py-1 rounded-xl border border-[#382b22] shadow-xs">
            <span
              className={`w-2 h-2 rounded-full ${
                isConnected ? "bg-emerald-500 shadow-[0_0_6px_rgba(16,185,129,0.7)]" : "bg-amber-500 animate-ping"
              }`}
            />
            <span>{isConnected ? t("lobby.connectedStatus") : t("lobby.connectingStatus")}</span>
          </div>

          {/* Přepínač jazyků */}
          <LanguageSwitcher />
        </div>
      </header>

      {/* 2. Hlavní obsah: Soustředěný herní stůl */}
      <main className="flex-1 max-w-xl w-full mx-auto px-4 py-6 sm:py-10 flex flex-col justify-center">
        {/* Banner pro návrat do rozehrané hry */}
        {gameState.isTokenValid && (
          <div className="mb-6 p-4 rounded-2xl bg-[#261910] border border-amber-500/70 shadow-lg flex items-center justify-between gap-3 animate-in fade-in">
            <div className="min-w-0">
              <div className="flex items-center gap-1.5 text-amber-300 font-bold text-sm">
                <RotateCcw className="w-4 h-4 animate-spin-reverse" />
                <span>{t("lobby.activeGameTitle")}</span>
              </div>
              <span className="text-xs text-[#bcae9f] mt-0.5 block truncate">
                {t("lobby.activeGameDesc")}
              </span>
            </div>
            <Button
              type="button"
              variant="primary"
              size="md"
              icon={<RotateCcw className="w-4 h-4" />}
              onClick={() => returnToGame()}
              className="shrink-0 shadow-md ring-2 ring-amber-400/50"
            >
              {t("lobby.returnToGame")}
            </Button>
          </div>
        )}

        {/* Hlavní herní karta */}
        <div className="bg-[#211710] border border-[#3d2f25] rounded-2xl shadow-2xl p-5 sm:p-7">
          {/* Přepínač záložek: Připojit / Založit */}
          <div className="grid grid-cols-2 p-1 bg-[#140c08] rounded-xl border border-[#33241a] mb-5">
            <button
              type="button"
              onClick={() => {
                setActiveTab("join");
                setError(null);
              }}
              className={`py-2 text-xs sm:text-sm font-bold rounded-lg transition-all cursor-pointer ${
                activeTab === "join"
                  ? "bg-[#2c1d13] text-[#fff2e0] shadow-sm border border-amber-600/40"
                  : "text-[#aa9a8c] hover:text-[#f5ede3]"
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
              className={`py-2 text-xs sm:text-sm font-bold rounded-lg transition-all cursor-pointer ${
                activeTab === "create"
                  ? "bg-[#2c1d13] text-[#fff2e0] shadow-sm border border-amber-600/40"
                  : "text-[#aa9a8c] hover:text-[#f5ede3]"
              }`}
            >
              {t("lobby.createTab")}
            </button>
          </div>

          {/* Chybová hláška */}
          {error && (
            <div className="mb-4 p-3 rounded-xl bg-red-950/70 border border-red-800/70 text-red-200 text-xs text-center animate-in fade-in font-medium">
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
                onChange={(e) => savePlayerName(e.target.value)}
                autoFocus={!name}
              />
              <Input
                label={t("lobby.gameCodeLabel")}
                placeholder={t("lobby.gameCodePlaceholder")}
                value={gameCode}
                onChange={(e) => setGameCode(e.target.value.toUpperCase())}
                autoFocus={!!name}
                className="tracking-widest font-mono text-center font-bold"
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
                onChange={(e) => savePlayerName(e.target.value)}
                autoFocus={!name}
              />

              <div className="space-y-1.5 text-left">
                <label className="block text-xs font-semibold text-[#bcae9f]">
                  {t("lobby.selectGameLabel")}
                </label>
                <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
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
                        className={`p-3.5 rounded-xl border text-left transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-amber-400 focus-visible:outline-none ${
                          isSel
                            ? "bg-[#2d1c12] border-amber-500 shadow-sm ring-1 ring-amber-400/50"
                            : "bg-[#170e09] border-[#33241a] hover:border-[#4d3626]"
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
                          <p className="text-xs text-[#bcae9f] mt-1 line-clamp-2">
                            {type.description}
                          </p>
                        )}
                      </div>
                    );
                  })}
                </div>

                <div className="pt-2 text-xs text-[#bcae9f]">
                  <span>{currentLang === "en" ? "Want to create your own card game? " : "Chceš vytvořit vlastní hru nebo pravidla? "}</span>
                  <a
                    href="https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-amber-400 hover:text-amber-300 underline inline-flex items-center gap-1 font-medium"
                  >
                    <span>{currentLang === "en" ? "Plugin tutorial on GitHub" : "Návod pro tvorbu pluginů na GitHubu"}</span>
                    <ExternalLink className="w-3 h-3" />
                  </a>
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
        </div>
      </main>

      {/* 3. Poctivá patička s reálnými odkazy na komunitu a projekt */}
      <footer className="w-full py-4 px-4 text-center text-xs text-[#9d8d7e] border-t-2 border-[#3c2a1c] bg-[#140d08]/80">
        <nav className="flex flex-wrap justify-center items-center gap-x-3 gap-y-1.5">
          <a
            href={currentLang === "en" ? "https://bang.honzaa.cz/en/about-games" : "https://bang.honzaa.cz/o-hrach"}
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#f5ede3] transition-colors"
          >
            {currentLang === "en" ? "About Games & Rules" : "O hrách a pravidla"}
          </a>
          <span>•</span>
          <a
            href="https://github.com/honzaHlavnicka/Bang"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#f5ede3] transition-colors"
          >
            GitHub
          </a>
          <span>•</span>
          <a
            href="https://discord.gg/WYmtDmMdJS"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#f5ede3] transition-colors"
          >
            Discord
          </a>
          <span>•</span>
          <a
            href="https://honzaa.itch.io/card-games"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#f5ede3] transition-colors"
          >
            itch.io
          </a>
          <span>•</span>
          <a
            href="https://bang.honzaa.cz/privacy"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#f5ede3] transition-colors"
          >
            {currentLang === "en" ? "Privacy" : "Soukromí"}
          </a>
        </nav>
      </footer>
    </div>
  );
};

export default LobbyPage;
