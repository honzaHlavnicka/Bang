import GamePage from './pages/GamePage';
import LoginPage from './pages/LoginPage'
import { useState, createContext, useContext } from 'react';import WaitingRoom from './pages/WaitingRoom';
import Card from './components/Card';
import Cards from './components/Cards';

function App() {
  const GameContext = createContext<{
    //parametry hry:
    jeSpustenaHra: boolean;
  } | null>(null);
  return (
    <GameContext.Provider value={{ jeSpustenaHra: false }}>
      <>
        {useContext(GameContext)?.jeSpustenaHra ? (
          <WaitingRoom>Připojování se k serveru...</WaitingRoom>
        ) : (<>
          {/*<LoginPage spustitHru={() => {}} />*/}
          <GamePage />
          </>
        )}
      </>
    </GameContext.Provider>
  )
}

export default App
