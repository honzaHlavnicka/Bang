import { useEffect, useState } from "react";
import { useGame } from "../../modules/GameContext";
import Card from "../Card";
import Cards from "../Cards";
import InPlayCards from "./InPlayCards";
import NameTag from "./NameTag";
import { useTranslation } from 'react-i18next';

export default function MyThings() {
    const {t} = useTranslation();
    const {gameState,playCard} = useGame();
    const role = gameState.role;
    const jmeno = gameState.name;
    const karty = gameState.handCards || [];
    const postava = gameState.character || "TESTOVACI";
    const vylozeneKarty = gameState.inPlayCards || [];
    const zdravy = gameState.health || 0;
    const isDead = zdravy !== undefined && zdravy <= 0;

    function CardClick(e: React.MouseEvent<HTMLDivElement>){
        const cardId = parseInt((e.currentTarget as HTMLDivElement).getAttribute("data-id") || "-1");
        if(cardId !== -1){
            playCard(cardId);
        }
    }
    const hasVerticalSpace = useMediaQuery("(min-height: 1000px)");

    return (
        <div style={{ width: "100%", display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "flex-end", flex: "0 0 auto", }}>
            <div style={{ marginRight: "32px", width: "fit-content" }}>
                <NameTag jmeno={jmeno || t("nepojmenovaný hráč")} isDead={isDead} showDeadIndicator={gameState.allowedUIElements.includes("ZIVOTY")} style={{backgroundColor:(gameState.playerId === gameState.turnPlayerId)?"yellow":"white"}} />
                <div style={{display:"flex",justifyContent:"center"}}>
                    {gameState.allowedUIElements.includes("ROLE") ?    <Card image={`/img/karty/role/${role}.png`} />: null}
                    {gameState.allowedUIElements.includes("POSTAVA") ? <Card image={`/img/karty/postavy/${postava}.png`} />: null}
                    {gameState.allowedUIElements.includes("ZIVOTY") ? <Card image={`/img/velkeZivoty/${zdravy}zivoty.png`} />: null}
                </div>
            </div>
            <div style={{flex: 1, display: "flex", justifyContent: "center",flexDirection:(hasVerticalSpace ? "column" : "row"), zIndex:3 }}>
                {(gameState.allowedUIElements.includes("VYLOZENE_KARTY")) ?
                    <InPlayCards vylozeneKarty={vylozeneKarty} />
                : null}
                <Cards onClickCard={CardClick} cards={karty}/>
            </div>
        </div>
    );
}


function useMediaQuery(query: string): boolean {
    const [matches, setMatches] = useState(false);

    useEffect(() => {
        const media = window.matchMedia(query);
        setMatches(media.matches);
        const listener = (event: MediaQueryListEvent) => {
            setMatches(event.matches);
        };
        media.addEventListener("change", listener);
        return () => media.removeEventListener("change", listener);
    }, [query]);

    return matches;
}