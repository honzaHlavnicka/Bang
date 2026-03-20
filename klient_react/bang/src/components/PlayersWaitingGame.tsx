import { useGame } from "../modules/GameContext";
import { useEffect } from "react";

export default function PlayersWaitingGame() {
    const {gameState ,startGame} = useGame();
    const isAdmin = gameState.isAdmin ?? false;
    
    // Obsluha Enter klávesy pro spuštění hry
    useEffect(() => {
        if (!isAdmin || !gameState.players || gameState.players.length < 2) {
            return;
        }

        const handleKeyDown = (e: KeyboardEvent) => {
            // Ignoruj klávesy pokud je fokus na inputu, textareaě nebo selectu
            if (document.activeElement?.tagName === "INPUT" || 
                document.activeElement?.tagName === "TEXTAREA" ||
                document.activeElement?.tagName === "SELECT") {
                return;
            }

            if (e.key === "Enter") {
                e.preventDefault();
                startGame();
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [isAdmin, gameState.players, startGame]);
    
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
            {gameState.players && gameState.players.length >= 2 && isAdmin ?
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