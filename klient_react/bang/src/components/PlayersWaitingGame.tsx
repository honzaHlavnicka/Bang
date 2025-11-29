import { useGame } from "../modules/GameContext";

export default function PlayersWaitingGame() {
    const {gameState ,startGame} = useGame();
    return (
        <div >
            <h3 style={{marginBottom:"0px"}}>Připojení hráči:</h3>
            <ul style={{marginTop:"3px"}}>
            {gameState.players?.map(player => {return(
                <li key={player.id}>
                    {player.name}
                </li>
            )}) }
            </ul>
            {gameState.players && gameState.players.length >= 2 ?
            <button
                style={{
                    width: "100%",
                    backgroundColor: "white",
                    color: "black",
                    cursor: "pointer",
                    borderRadius: "50px",
                    fontSize: "1.3em",
                    border: 0
                }}
                onClick={() => startGame()}
                onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        startGame();
                    }
                }}
                tabIndex={0}
                aria-label="Spustit hru"
            >
                Spustit hru
            </button>: null}
        </div>
    );
}