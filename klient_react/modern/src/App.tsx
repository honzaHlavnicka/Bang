import React from "react";
import { useGame } from "@bang/core";
import { LobbyPage } from "./pages/LobbyPage";
import { WaitingRoomPage } from "./pages/WaitingRoomPage";
import { GamePlayPage } from "./pages/GamePlayPage";
import { GameOverPage } from "./pages/GameOverPage";
import { DialogManager } from "./components/DialogManager";
import { GlobalNotifications } from "./components/GlobalNotifications";
import { DragProvider } from "./components/DragContext";

export default function App() {
  const { gameState } = useGame();

  const renderContent = () => {
    // Čekání na první navázání spojení
    if (!gameState.startedConection) {
      return (
        <div className="min-h-screen flex flex-col items-center justify-center p-4 bg-[#161311] text-[#f5ede3] text-center select-none">
          <div className="w-10 h-10 border-3 border-amber-500/30 border-t-amber-500 rounded-full animate-spin mb-4" />
          <h2 className="text-lg font-semibold text-[#f5ede3]">Připojování k hernímu serveru...</h2>
          <p className="text-xs text-[#b5a596] mt-1 max-w-sm">
            Pokud se obrazovka nezmění během několika sekund, zkontroluj prosím své internetové připojení.
          </p>
        </div>
      );
    }

    // 1. Hráč ještě není v žádné místnosti: Lobby
    if (!gameState.inGame) {
      return <LobbyPage />;
    }

    // 2. V místnosti před spuštěním hry: Čekací místnost
    if (!gameState.gameStarted) {
      return <WaitingRoomPage />;
    }

    // 3. Po skončení hry: Vyhlášení vítězů
    if (gameState.gameEnded) {
      return <GameOverPage />;
    }

    // 4. Probíhající hra: Herní stůl
    return <GamePlayPage />;
  };

  return (
    <DragProvider>
      {/* Rychlá vyskakovací hlášení ze serveru (GlobalNotifications) */}
      <GlobalNotifications />

      {/* Globální modální dialogy z herního jádra (dostupné na všech stránkách včetně konce hry) */}
      <DialogManager />

      {/* Aktuální stránka */}
      {renderContent()}
    </DragProvider>
  );
}
