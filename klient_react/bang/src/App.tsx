import React, { Suspense, useEffect } from "react";
import { useGame } from './modules/GameContext';
import WaitingRoom from "./pages/WaitingRoom";
import AfterGamePage from "./pages/AfterGamePage";
import { useTranslation } from "react-i18next";

// Lazy importy těžkých stránek
const GamePage = React.lazy(() => import('./pages/GamePage'));
const LoginPage = React.lazy(() => import('./pages/LoginPage'));
const BeforeGameWaiting = React.lazy(() => import('./pages/BeforeGameWaiting'));


function App() {
  const { gameState } = useGame();
  const { t } = useTranslation();



  // Přednačítání dalších stránek
  useEffect(() => {
    if (!gameState.inGame) {
      // Pokud je uživatel na LoginPage, přednačti BeforeGameWaiting
      import('./pages/BeforeGameWaiting');
    } else if (!gameState.gameStarted) {
      // Pokud je uživatel na BeforeGameWaiting, přednačti GamePage
      import('./pages/GamePage');
    }
  }, [gameState.inGame, gameState.gameStarted]);

  return (
    <Suspense fallback={<WaitingRoom>{t("Načítání...")}</WaitingRoom>}>
      {gameState.inGame ? (
        gameState.gameStarted ? (
          gameState.gameEnded ? (
            <AfterGamePage />
          ) : (
            <GamePage />
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
