import { useGame } from "../../modules/GameContext";
import Card from "../Card";
import Cards from "../Cards";
import NameTag from "./NameTag";

 export default function MyThings() {
    const {gameState,playCard} = useGame();
    const role = gameState.role;
    const jmeno = gameState.name;
    const karty = gameState.handCards || [];
    const postava = gameState.character || "TESTOVACI";
    const vylozeneKarty = gameState.inPlayCards || [];
    const zdravy = gameState.health || 0;

    function CardClick(e: React.MouseEvent<HTMLDivElement>){
        const cardId = parseInt((e.currentTarget as HTMLDivElement).getAttribute("data-id") || "-1");
        if(cardId !== -1){
            playCard(cardId);
        }
    }


    return (
        <div style={{ width: "100%", display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center" }}>
            <div style={{ marginRight: "32px" }}>
                <NameTag jmeno={jmeno || "nepojmenovaný hráč"} style={{backgroundColor:(gameState.playerId === gameState.turnPlayerId)?"yellow":"white"}} />
                <div style={{display:"flex",justifyContent:"center"}}>
                    <Card image={`/img/karty/role/${role}.png`} />
                    <Card image={`/img/karty/postavy/${postava}.png`} />
                    <Card image={`/img/velkeZivoty/${zdravy}zivoty.png`} />
                </div>
            </div>
            <div style={{flex: 1, display: "flex", justifyContent: "center",flexDirection:"column" }}>
                <Cards isAnimated={false} isRotated={false} onClickCard={alert} cards={vylozeneKarty}/>
                <Cards onClickCard={CardClick} cards={karty}/>
            </div>
        </div>
    );
}