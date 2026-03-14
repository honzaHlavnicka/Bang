// PŘÍKLAD: Vylepšená verze CentralPanel s lepšími stavovými zprávami

import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";
import { useGame } from "../../modules/GameContext";
import globalCSS from "../../styles/global.module.css";
import gameStateCSS from "../../styles/gameStateMessage.module.css";

import Fire from "./Fire";
import { useDialog } from "../../modules/DialogContext";

export default function CentralPanelEnhanced() {
    const { gameState, drawCard, endTurn } = useGame();
    const { openDialog } = useDialog();
    
    const imagesForDeck = gameState.discardPile;

    return (
        <>
            {/* Stavová zpráva s vylepšeným stylingem */}
            <div className={gameStateCSS.gameStateMessageContainer}>
                <span className={gameStateCSS.gameStateMessageText}>
                    {gameState.gameStateMessege ? (
                        <>
                            {gameState.gameStateMessege}
                            {/* Indikátor čekání - volitelný */}
                            {gameState.gameStateMessege.includes("vybírá") && (
                                <span className={gameStateCSS.loadingDot}>.</span>
                            )}
                        </>
                    ) : (
                        "Hra běží..."
                    )}
                </span>

                {/* Infomační tlačítko pro podrobné zprávy */}
                {gameState.gameStateMessegeFull && (
                    <button
                        onClick={() =>
                            openDialog({
                                type: "INFO",
                                dialogHeader: "Tvůj stav hry.",
                                data: {
                                    message: gameState.gameStateMessegeFull || "",
                                    header: gameState.gameStateMessege,
                                },
                            })
                        }
                        className={gameStateCSS.infoButton}
                    >
                        <span className={gameStateCSS.infoIcon}>?</span>
                    </button>
                )}
            </div>

            {/* Centralní panel s prvky */}
            <div
                style={{
                    flex: 1,
                    minHeight: 0,
                    display: "flex",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent: "center",
                }}
            >
                {gameState.allowedUIElements.includes("UKONCENI_TAHU") ? (
                    <button
                        className={globalCSS.button}
                        onClick={() => {
                            endTurn();
                        }}
                        style={{ marginRight: 20 }}
                    >
                        Ukončit tah
                    </button>
                ) : null}

                {gameState.allowedUIElements.includes("OHEN") ? (
                    <Fire />
                ) : null}

                {gameState.allowedUIElements.includes("ODHAZOVACI_BALICEK") ? (
                    <Deck images={imagesForDeck} />
                ) : null}

                {gameState.allowedUIElements.includes("DOBIRACI_BALICEK") ? (
                    <Card
                        image={"/img/karty/zezadu.png"}
                        name="dobírací balíček"
                        onClick={() => drawCard()}
                        id={-2}
                    />
                ) : null}

                <ZoomToggleButton />
            </div>
        </>
    );
}
