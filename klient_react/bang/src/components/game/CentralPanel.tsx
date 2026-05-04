import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";
import { useGame } from "../../modules/GameContext";
import globalCSS from "../../styles/global.module.css";

import Fire from "./Fire";
import { useDialog } from "../../modules/DialogContext";
import { useTranslation } from "react-i18next";


export default function CentralPanel() {
    //const [deckImages, setDeckImages] =  React.useState<string[]>([]);
    const {gameState, drawCard, endTurn, clickUIButton, startNewGameAndDeleteThisOne} = useGame();
    const {openDialog} = useDialog();
    const {t} = useTranslation();

    //const imagesForDeck = deckImages.length ? deckImages : gameState.discardPile;
    const imagesForDeck = gameState.discardPile;

    const talonTopCardUrl = gameState.talonTopCard ? "/img/karty/" + gameState.talonTopCard + ".png" : "/img/karty/zezadu.png";
    return (<>

        <h2 className={globalCSS.darkmodeReverseColor} style={{textAlign:"center", zIndex:4}}>
              {gameState.gameStateMessege ? gameState.gameStateMessege : ""}
             {gameState.gameStateMessegeFull ?  <button onClick={()=>openDialog({type:"INFO",dialogHeader:t("central_panel.game_state_header"),data:{message:gameState.gameStateMessegeFull || "", header:gameState.gameStateMessege}})} className={globalCSS.button}>?</button> : null}
             </h2>

        <div style={{flex:1, minHeight:0, display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center", }}>
            <div style={{display:"flex", flexDirection:"column", alignItems:"flex-start", flexWrap:"wrap", marginRight: "2em"}}>
                {gameState.allowedUIElements.includes("UKONCENI_TAHU") ? 
                <button className={globalCSS.button}  onClick={()=>{endTurn();}} style={{marginRight:20}}>{t("Ukončit tah")}</button>
                : null}
                {gameState.isAdmin ?
                    <button className={globalCSS.button} onClick={() => {startNewGameAndDeleteThisOne()}}>{t("Smazat hru a začít jinou")}</button>
                : null}
                <ZoomToggleButton />
                {gameState.customUIButtons.length > 0 && 
                    <div>
                        {gameState.customUIButtons.map(btn => (
                            <button key={btn.id} className={globalCSS.button} onClick={() => clickUIButton(btn.id)}>
                                {btn.text}
                            </button>
                        ))}
                    </div>
                }
            </div>

            {gameState.allowedUIElements.includes("OHEN") ?
            <Fire />
            : null}
            {gameState.allowedUIElements.includes("ODHAZOVACI_BALICEK") ?
                <Deck images={imagesForDeck} />
            : null}
            {gameState.allowedUIElements.includes("DOBIRACI_BALICEK") ?
                <Card image={ talonTopCardUrl} name={t("central_panel.draw_pile")} onClick={()=>drawCard()} id={-2}/>
            : null}


        </div>
        </>
    );
}