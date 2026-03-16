import css from "../styles/waitingPage.module.css";
import DarkModeSwitch from "../components/DarkModeSwitch";
import globalCSS from "../styles/global.module.css";
import Card from "../components/Card";
import { useGame } from "../modules/GameContext";

export default function AfterGamePage(){
    const {gameState} = useGame();

    return (
        <div className={css.container}>
            <Card image="/img/karty/zezadu.png"  />
            <div className={css.content}>
                <h1>Hra skončila</h1>
                <h2>Výsledková tabulka</h2>
                <ol>
                    {gameState.winningPlaces?.map((place, index) => (
                        <li key={index}>
                            {place.map((playerId) => (
                                <span key={playerId}>{gameState.players?.find(player => player.id === playerId)?.name}, </span>
                            ))} na {index + 1}. místě
                        </li>
                    ))}
                </ol>
                <div style={{textAlign:"center"}} >
                    <a className={globalCSS.button} href="/">Zpět na úvodní stránku</a>
                </div>
            </div>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        </div>
    );
}