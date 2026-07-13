import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";
import { useGame } from "../../modules/GameContext";
import globalCSS from "../../styles/global.module.css";

import Fire from "./Fire";
import { useDialog } from "../../modules/DialogContext";
import { useTranslation } from "react-i18next";
import { useIsMobile } from "../../modules/useWindowDimentions";


export default function CentralPanel() {
    //const [deckImages, setDeckImages] =  React.useState<string[]>([]);
    const {gameState, drawCard, endTurn, clickUIButton, startNewGameAndDeleteThisOne} = useGame();
    const {openDialog} = useDialog();
    const {t} = useTranslation();
    const isMobile = useIsMobile();

    const mobileButtonStyle: React.CSSProperties = isMobile ? {
        padding: "8px 16px",
        fontSize: "13px",
        borderRadius: "8px",
        margin: "2px 1px"
    } : {};

    //const imagesForDeck = deckImages.length ? deckImages : gameState.discardPile;
    const imagesForDeck = gameState.discardPile;

    const talonTopCardUrl = gameState.talonTopCard ? "/img/karty/" + gameState.talonTopCard + ".png" : "/img/karty/zezadu.png";
    return (<>
        {gameState.gameStateMessege || gameState.gameStateMessegeFull ? (
            <h2 
                className={globalCSS.darkmodeReverseColor} 
                style={{
                    textAlign: "center", 
                    zIndex: 4,
                    fontSize: isMobile ? "1.1rem" : "1.5rem",
                    margin: isMobile ? "2px 0" : "20px 0",
                    padding: isMobile ? "0 10px" : "0"
                }}
            >
                {gameState.gameStateMessege ? gameState.gameStateMessege : ""}
                {gameState.gameStateMessegeFull ?  (
                    <button 
                        onClick={() => openDialog({
                            type: "INFO",
                            dialogHeader: t("central_panel.game_state_header"),
                            data: {
                                message: gameState.gameStateMessegeFull || "", 
                                header: gameState.gameStateMessege
                            }
                        })} 
                        className={globalCSS.button}
                        style={{
                            padding: isMobile ? "3px 8px" : "13px 30px",
                            fontSize: isMobile ? "12px" : "14px",
                            borderRadius: isMobile ? "6px" : "12px",
                            marginLeft: "8px"
                        }}
                    >
                        ?
                    </button>
                ) : null}
            </h2>) : null}

        <div style={{
            flex: 1, 
            minHeight: 0, 
            display: "flex", 
            flexDirection: isMobile ? "column" : "row", 
            alignItems: "center", 
            justifyContent: "center",
            gap: isMobile ? "4px" : "0",
            padding: isMobile ? "2px" : "0"
        }}>
            <div style={{
                display: "flex", 
                flexDirection: isMobile ? "row" : "column", 
                alignItems: "center", 
                justifyContent: "center",
                flexWrap: "wrap", 
                marginRight: isMobile ? "0" : "2em",
                gap: "6px"
            }}>
                {gameState.allowedUIElements.includes("UKONCENI_TAHU") ? 
                    <button className={globalCSS.button} onClick={() => { endTurn(); }} style={{ marginRight: isMobile ? 0 : 20, ...mobileButtonStyle }}>
                        {t("Ukončit tah")}
                    </button>
                : null}
                {gameState.isAdmin ?
                    <button className={globalCSS.button} onClick={() => { startNewGameAndDeleteThisOne(); }} style={mobileButtonStyle}>
                        {t("Smazat hru a začít jinou")}
                    </button>
                : null}
                <ZoomToggleButton style={mobileButtonStyle} />
                {gameState.customUIButtons.length > 0 && 
                    gameState.customUIButtons.map(btn => (
                        <button key={btn.id} className={globalCSS.button} onClick={() => clickUIButton(btn.id)} style={mobileButtonStyle}>
                            {btn.text}
                        </button>
                    ))
                }
            </div>

            <div style={{
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "center",
                gap: isMobile ? "6px" : "20px"
            }}>
                {gameState.allowedUIElements.includes("OHEN") && !isMobile ?
                    <Fire />
                : null}
                {gameState.allowedUIElements.includes("ODHAZOVACI_BALICEK") ?
                    <Deck images={imagesForDeck} />
                : null}
                {gameState.allowedUIElements.includes("DOBIRACI_BALICEK") ?
                    <Card image={talonTopCardUrl} name={t("central_panel.draw_pile")} onClick={() => drawCard()} id={-2} />
                : null}
            </div>
        </div>
        </>
    );
}