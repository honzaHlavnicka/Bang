import CharacterPicker from "../components/CharacterPicker";
import PlayersWaitingGame from "../components/PlayersWaitingGame";
import { useGame } from "../modules/GameContext";
import WaitingRoom from "./WaitingRoom";
import css from "../styles/waitingPage.module.css";
import { useTranslation } from "react-i18next";

export default function BeforeGameWaiting() {
    const {gameState} = useGame();
    const {t} = useTranslation();
    return (
        <WaitingRoom> 
            <h1>{t("Čekání na další hráče")}</h1>
            {t("Nasdílej jim kód:")} <a className={css.gameCode} href={"/?code=" + gameState.gameCode} target="_blank" > {gameState.gameCode} </a>
            {gameState.allowedUIElements.includes("POSTAVA") ?
            <CharacterPicker />: null}
            <PlayersWaitingGame/>
        </WaitingRoom>
    );
}