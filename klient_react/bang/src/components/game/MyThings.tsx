import { useEffect, useState } from "react";
import { useGame } from "../../modules/GameContext";
import Card from "../Card";
import Cards from "../Cards";
import InPlayCards from "./InPlayCards";
import NameTag from "./NameTag";
import { useTranslation } from 'react-i18next';
import { useDialog } from "../../modules/DialogContext";
import { useIsMobile } from "../../modules/useWindowDimentions";

export default function MyThings() {
    const {t} = useTranslation();
    const {gameState,playCard, fireCard,putCardInPlay,putCardInPlayOnPlayer} = useGame();
    const role = gameState.role;
    const jmeno = gameState.name;
    const karty = gameState.handCards || [];
    const postava = gameState.character || "TESTOVACI";
    const vylozeneKarty = gameState.inPlayCards || [];
    const zdravy = gameState.health || 0;
    const isDead = zdravy !== undefined && zdravy <= 0;
    const { openDialog } = useDialog();

    function CardClick(e: React.MouseEvent<HTMLDivElement>){
        const cardId = parseInt((e.currentTarget as HTMLDivElement).getAttribute("data-id") || "-1");
        if(cardId !== -1){
            openDialog({type:"CONFIRM_ACTION",
                notClosable:false,
                dialogHeader:t("my_things.card_action_header"),
                data:{
                    actions:[
                        {name:t("my_things.action_play"), id:0},
                        {name:t("my_things.action_discard"), id:1},
                        {name:t("my_things.action_play_in_front"), id:2},
                        {name:t("my_things.action_play_on_player"), id:3}
                    ]
                                },
                callback:(actionId:number)=>{
                    switch (actionId) {
                        case 0:
                            playCard(cardId);
                            break;
                        case 1:
                            fireCard(cardId);
                            break;
                        case 2:
                            putCardInPlay(cardId);
                            break;
                        case 3:
                            openDialog({type:"SELECT_PLAYER",
                                notClosable:false,
                                dialogHeader:t("my_things.play_on_player_header"),
                                data:{
                                    max:1,
                                    min:0,
                                    players:gameState.players?.filter(p => p.id !== gameState.playerId).map(p => ({ id: p.id, name: p.name })) ?? []
                                },
                                callback:(playerId:number[])=>{
                                    if(playerId.length > 0){
                                        putCardInPlayOnPlayer(cardId,playerId[0]);
                                    }
                                }
                            });
                            break;
                    }
                }
            });
        }
    }
    const hasVerticalSpace = useMediaQuery("(min-height: 1000px)");
    const isMobile = useIsMobile();
    if (isMobile) {
        const rowScrollStyle: React.CSSProperties = {
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "flex-start", 
            width: "100%",
            overflowX: "auto",
            WebkitOverflowScrolling: "touch",
            touchAction: "pan-x",
            boxSizing: "border-box"
        };

        return (
            <div style={{ width: "100%", display: "flex", flexDirection: "column", flex: "0 0 auto", zIndex: 3 }}>
                    
                <div style={rowScrollStyle}>
                    <NameTag jmeno={jmeno || t("nepojmenovaný hráč")} isDead={isDead} showDeadIndicator={gameState.allowedUIElements.includes("ZIVOTY")} style={{backgroundColor:(gameState.playerId === gameState.turnPlayerId)?"yellow":"white", flex: "0 0 auto"}} />
                    {gameState.allowedUIElements.includes("ROLE") ?    <Card image={`/img/karty/role/${role}.png`} />: null}
                    {gameState.allowedUIElements.includes("POSTAVA") ? <Card image={`/img/karty/postavy/${postava}.png`} />: null}
                    {gameState.allowedUIElements.includes("ZIVOTY") ? <Card image={`/img/velkeZivoty/${zdravy}zivoty.png`} />: null}
                    <div style={{ width: "30px", flex: "0 0 auto" }} />
                    <Cards isDragable={false} onClickCard={CardClick} cards={vylozeneKarty} isInline={false} />
                </div>

                <div style={rowScrollStyle}>
                    <Cards isDragable={false} onClickCard={CardClick} cards={karty} isInline={false} />
                </div>
            </div>
        )
    }
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