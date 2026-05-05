import css from "../styles/waitingPage.module.css";
import DarkModeSwitch from "../components/DarkModeSwitch";
import globalCSS from "../styles/global.module.css";
import Card from "../components/Card";
import { useGame } from "../modules/GameContext";
import { useTranslation } from "react-i18next";
import i18n from "../../i18n";

import { usePostHog } from "@posthog/react";

export default function AfterGamePage(){
    const {gameState} = useGame();
    const {t} = useTranslation();
    const posthog = usePostHog();

    const language = i18n.language;

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
                    <a 
                        className={globalCSS.button} 
                        href="/"
                        onClick={() => posthog?.capture('back_to_home_clicked', { location: 'after_game_page' })}
                    >
                        {t("Zpět na úvodní stránku")}
                    </a>
                </div>
            </div>
            <div className={css.content}>
                <h2><img src="https://world-quiz.com/newlogo.webp" alt="World quiz!" width={50} height={50} style={{ verticalAlign: "middle", marginRight: "8px" }} /> World quiz</h2>
                <a 
                    href={"https://world-quiz.com/" + language + "/game/flags/europe" + "?utm_source=bang.honzaa.cz&utm_medium=referral&utm_campaign=bang&utm_campaign=po_dohrani_bangu_obrazek"} 
                    target="_blank"
                    onClick={() => posthog?.capture('external_link_clicked', { destination: 'world-quiz', location: 'after_game_page', type: 'image' })}
                >
                    <img src="/img/world-quiz-obrazovka.png" alt="World quiz screenshot" style={{width:"100%",borderRadius:"8px"}} />
                </a>
                <p>{t("Pokud už ostatní nechtějí hrát a stále se nudíš, tak si protrénuj své zeměpisné znalosti na webu")} <a 
                    target="_blank" 
                    href={"https://world-quiz.com/" + language + "?utm_source=bang.honzaa.cz&utm_medium=referral&utm_campaign=bang&utm_campaign=po_dohrani_bangu"}
                    onClick={() => posthog?.capture('external_link_clicked', { destination: 'world-quiz', location: 'after_game_page', type: 'link' })}
                >world-quiz.com</a>.</p>
                <p>{t("Největší online databáze vlajek:")} <a 
                    target="_blank" 
                    href={"https://jef.world-quiz.com/" + language + "?utm_source=bang.honzaa.cz&utm_medium=referral&utm_campaign=bang&utm_campaign=po_dohrani_bangu"}
                    onClick={() => posthog?.capture('external_link_clicked', { destination: 'just-enough-flags', location: 'after_game_page', type: 'link' })}
                >jef.world-quiz.com</a>.</p>
            </div>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        </div>
    );
}