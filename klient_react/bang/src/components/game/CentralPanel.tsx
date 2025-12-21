import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";
import { useGame } from "../../modules/GameContext";
import globalCSS from "../../styles/global.module.css";

import Fire from "./Fire";
import { useDialog } from "../../modules/DialogContext";
export default function CentralPanel() {
    //const [deckImages, setDeckImages] =  React.useState<string[]>([]);
    const {gameState, drawCard, endTurn} = useGame();
    const {openDialog} = useDialog();

    
    //const imagesForDeck = deckImages.length ? deckImages : gameState.discardPile;
    const imagesForDeck = gameState.discardPile;
    return (<>

        <h2 className={globalCSS.darkmodeReverseColor} style={{textAlign:"center"}}>
              {gameState.gameStateMessege ? gameState.gameStateMessege : ""}
             {gameState.gameStateMessegeFull ?  <button onClick={()=>openDialog({type:"INFO",dialogHeader:"Tvůj stav hry.",data:{message:gameState.gameStateMessegeFull || "", header:gameState.gameStateMessege}})} className={globalCSS.button}>?</button> : null}
             </h2>

        <div style={{flex:1, minHeight:0, display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center", }}>
            {gameState.allowedUIElements.includes("UKONCENI_TAHU") ? 
                <button className={globalCSS.button}  onClick={()=>{endTurn();}} style={{marginRight:20}}>Ukončit tah</button>
            : null}
            {gameState.allowedUIElements.includes("OHEN") ?
            <Fire />
            : null}
            {gameState.allowedUIElements.includes("ODHAZOVACI_BALICEK") ?
                <Deck images={imagesForDeck} />
            : null}
            {gameState.allowedUIElements.includes("DOBIRACI_BALICEK") ?
                <Card image={"/img/karty/zezadu.png"} name="dobírací balíček" onClick={()=>drawCard()} id={-2}/>
            : null}
            <ZoomToggleButton />
            
        </div>
        </>
    );
}