import React, { Suspense, useEffect } from "react";
import { useGame } from './modules/GameContext';
import WaitingRoom from "./pages/WaitingRoom";

// Lazy importy těžkých stránek
const GamePage = React.lazy(() => import('./pages/GamePage'));
const LoginPage = React.lazy(() => import('./pages/LoginPage'));
const BeforeGameWaiting = React.lazy(() => import('./pages/BeforeGameWaiting'));

function App() {
  const { gameState } = useGame();

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
    <Suspense fallback={<WaitingRoom>Náčítání...</WaitingRoom>}>
      {gameState.inGame ? (
        gameState.gameStarted ? (
          <GamePage />
        ) : (
          <BeforeGameWaiting />
        )
      ) : (
        <LoginPage />
      )}
    </Suspense>
  );
}

export default App;
