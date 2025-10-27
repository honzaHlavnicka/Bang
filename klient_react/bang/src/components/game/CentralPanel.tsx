import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";
import { useGame } from "../../modules/GameContext";
import globalCSS from "../../styles/global.module.css";
export default function CentralPanel() {
    //const [deckImages, setDeckImages] =  React.useState<string[]>([]);
    const {gameState, drawCard, endTurn} = useGame();

    //const imagesForDeck = deckImages.length ? deckImages : gameState.discardPile;
    const imagesForDeck = gameState.discardPile;
    return (
        <div style={{flex:1, minHeight:0, display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center", }}>
            <button className={globalCSS.button}  onClick={()=>{endTurn();}} style={{marginRight:20}}>Ukončit tah</button>
            <Deck images={imagesForDeck} />
            
            <Card image={"/img/karty/zezadu.png"} name="dobírací balíček" onClick={()=>drawCard()}/>
            <ZoomToggleButton />
        </div>
    );
}