import GamePage from './pages/GamePage';
import { useGame } from './modules/GameContext';
import LoginPage from './pages/LoginPage';

import BeforeGameWaiting from './pages/BeforeGameWaiting';

function App() {
  const {gameState} = useGame();

  return (
      <>
          {gameState.inGame ? (gameState.gameStarted ? <GamePage /> : <BeforeGameWaiting/>) : <LoginPage /> }
      </>
  )
}

export default App
