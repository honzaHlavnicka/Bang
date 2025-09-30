import GamePage from './pages/GamePage';
import { useGame } from './modules/GameContext';
import LoginPage from './pages/LoginPage';
import WaitingRoom from './pages/WaitingRoom';
import CharacterPicker from './components/CharacterPicker';
import PlayersWaitingGame from './components/PlayersWaitingGame';

function App() {
  const {gameState} = useGame();

  return (
      <>
          {gameState.inGame ? (gameState.gameStarted ? <GamePage /> : <WaitingRoom> čekání na další hráče<br/>Nasdílej jim kód: {gameState.gameCode} <CharacterPicker /><PlayersWaitingGame/></WaitingRoom>) : <LoginPage /> }
      </>
  )
}

export default App
