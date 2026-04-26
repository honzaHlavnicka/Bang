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
            <div className={css.content}>
                <h2><img src="https://world-quiz.com/newlogo.webp" alt="World quiz!" width={50} height={50} style={{ verticalAlign: "middle", marginRight: "8px" }} /> World quiz</h2>
                <a href="https://world-quiz.com/cs/game/flags/europe" target="_blank" >
                    <img src="/img/world-quiz-obrazovka.png" alt="World quiz screenshot" style={{width:"100%",borderRadius:"8px"}} />
                </a>
                <p>Pokud už ostatní nechtějí hrát a stále se nudíš, tak si protrénuj své zeměpisné znalosti na webu <a target="_blank" href="https://world-quiz.com">world-quiz.com</a></p>

            </div>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        </div>
    );
}