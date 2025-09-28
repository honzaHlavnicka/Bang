import { useGame } from "../modules/GameContext";

export default function PlayersWaitingGame() {
    const {gameState} = useGame();
    return (
        <div>
            <h3>Připojení hráči:</h3>
            {gameState.players.map(player => {return(
                <li key={player.id}>
                    {player.name}
                </li>
            )}) }
        </div>
    );
}