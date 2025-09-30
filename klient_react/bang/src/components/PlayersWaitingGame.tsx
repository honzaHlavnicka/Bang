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
            {gameState.players!.length > 1 ? <button  style={{width:"100%",backgroundColor:"white",color:"black",cursor:"pointer",borderRadius:"50px",fontSize:"1.3em",border:0,outline:0}} onClick={() => {startGame();}}>Spustit hru</button> : null}
        </div>
    );
}