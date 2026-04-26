import css from "../styles/waitingPage.module.css";
import DarkModeSwitch from "../components/DarkModeSwitch";
import globalCSS from "../styles/global.module.css";
import Card from "../components/Card";
import { useGame } from "../modules/GameContext";
import { useTranslation } from "react-i18next";

export default function AfterGamePage(){
    const {gameState} = useGame();
    const {t} = useTranslation();

    return (
        <div className={css.container}>
            <Card image="/img/karty/zezadu.png"  />
            <div className={css.content}>
                <h1>{t("Hra skončila")}</h1>
                <h2>{t("Výsledková tabulka")}</h2>
                <ol>
                    {gameState.winningPlaces?.map((place, index) => (
                        <li key={index}>
                            {place.map((playerId) => (
                                <span key={playerId}>{gameState.players?.find(player => player.id === playerId)?.name}, </span>
                            ))} {t("na {{index}}. místě", { index: index + 1 })}
                        </li>
                    ))}
                </ol>
                <div style={{textAlign:"center"}} >
                    <a className={globalCSS.button} href="/">{t("Zpět na úvodní stránku")}</a>
                </div>
            </div>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        </div>
    );
}