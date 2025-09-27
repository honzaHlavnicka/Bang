import GamePage from './pages/GamePage';
import LoginPage from './pages/LoginPage'
import { createContext, useContext } from 'react';import WaitingRoom from './pages/WaitingRoom';
import GameContext, { GameProvider } from './modules/GameKontext';


function App() {

  return (
      <>
      <GameProvider>
          <GamePage />
       </GameProvider>

      </>
  )
}

export default App
