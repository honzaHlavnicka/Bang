import Player from "./Player";
import { useRef, useEffect } from "react";
import css from "../../styles/scrolable.module.css"
import { useGame } from "../../modules/GameContext";

export default function Players() {
    const containerRef = useRef<HTMLDivElement>(null);
    const {gameState} = useGame();

    // Přesměrování vertikálního scrollu na horizontální
    function handleWheel(e: React.WheelEvent<HTMLDivElement>) {
        if (containerRef.current) {
            containerRef.current.scrollTo({
                left: containerRef.current.scrollLeft + e.deltaY * 5,
                behavior: "smooth"
            });
            e.preventDefault();
        }
    }

    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.scrollLeft = 500;
        }
    }, []);

    return (
        <div
            ref={containerRef}
            className={css.scrollable}
            style={{
                flexShrink: 0,
                flexDirection: "row",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 10px",
                width: "100%",
                overflowX: "auto",
                overflowY: "hidden",
                whiteSpace: "nowrap",
            }}
            onWheel={handleWheel}
        >
           {gameState.players!.map((player)=>{
            if(player.id != gameState.playerId){
                   return <Player 
                            jmeno={player.name}
                            key={player.id} 
                            pocetZivotu={player.health} 
                            pocetKaret={player.cardsInHand} 
                            postava={player.character} 
                            naTahu={gameState.turnPlayerId === player.id}
                            vylozeneKarty={player.inPlayCards}
                            povoleneUI={gameState.allowedUIElements}
                        />
            }
           })}

        </div>
    );
}