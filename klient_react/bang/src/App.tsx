import { Suspense, useEffect } from "react";
import { useGame } from './modules/GameContext';
import WaitingRoom from "./pages/WaitingRoom";
import AfterGamePage from "./pages/AfterGamePage";
import { useTranslation } from "react-i18next";

import { lazyWithRetry } from "./utils/lazyWithRetry";

// Lazy importy těžkých stránek s automatickým retry/reloadem při chybě načtení assetů
const GamePageWrapper = lazyWithRetry(() => import('./pages/GamePageWrapper'));
const LoginPage = lazyWithRetry(() => import('./pages/LoginPage'));
const BeforeGameWaiting = lazyWithRetry(() => import('./pages/BeforeGameWaiting'));


function App() {
  const { gameState } = useGame();
  const { t } = useTranslation();



  // Přednačítání dalších stránek
  useEffect(() => {
    if (!gameState.inGame) {
      // Pokud je uživatel na LoginPage, přednačti BeforeGameWaiting
      import('./pages/BeforeGameWaiting').catch(err => console.warn('Preload BeforeGameWaiting failed:', err));
    } else if (!gameState.gameStarted) {
      // Pokud je uživatel na BeforeGameWaiting, přednačti GamePageWrapper
      import('./pages/GamePageWrapper').catch(err => console.warn('Preload GamePageWrapper failed:', err));
    }
  }, [gameState.inGame, gameState.gameStarted]);

  return (
    <Suspense fallback={<WaitingRoom>{t("Načítání...")}</WaitingRoom>}>
      {gameState.inGame ? (
        gameState.gameStarted ? (
          gameState.gameEnded ? (
            <AfterGamePage />
          ) : (
            <GamePageWrapper />
          )
        ) : (
          <BeforeGameWaiting />
        )
      ) : (
        <SafeLoginPage startedConection={gameState.startedConection} />
      )}
    </Suspense>
  );
}

function SafeLoginPage({ startedConection }: { startedConection: boolean }) {
  const { t } = useTranslation();
  if (!startedConection) {
    return <WaitingRoom>
      <h1>{t("Probíhá připojování k serveru...")}</h1>
      <hr />
      {t("Pokud se tato obrazovka nezmění během několika sekund, zkontroluj prosím připojení k internetu a zda není server vypnutý.")}
      </WaitingRoom>;
  } else {
    return <LoginPage />;
  }
};

export default App;
