import Player from "./Player";
import { useRef, useEffect, useMemo } from "react";
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

    const ostatniHraci = useMemo(() => {
        if (!gameState.players || gameState.playerId == null) {
            return gameState.players || [];
        }

        const mujIndex = gameState.players.findIndex(p => p.id === gameState.playerId);

        // Pokud náhodou nejsem ve hře (třeba divák), vrátím všechny
        if (mujIndex === -1) {
            return gameState.players;
        }

        // Zrotuje pole tak, že hráči po mně jdou na začátek, a hráči přede mnou na konec.
        // Zároveň mě (mujIndex) z pole rovnou vynechá.
        return [
            ...gameState.players.slice(mujIndex + 1),
            ...gameState.players.slice(0, mujIndex)
        ];
    }, [gameState.players, gameState.playerId]);

    return (
        <div
            ref={containerRef}
            className={css.scrollable}
            style={{
                flexShrink: 0,
                flexDirection: "row",
                display: "flex",
                justifyContent: "space-between",
                padding: "0",
                margin: "0",
                width: "100%",
                overflowX: "auto",
                overflowY: "hidden",
                whiteSpace: "nowrap",
                alignItems: "flex-start",

            }}
            onWheel={handleWheel}
        >
           {ostatniHraci.map((player)=>{
            
                   return <Player 
                            jmeno={player.name}
                            key={player.id} 
                            pocetZivotu={player.health} 
                            pocetKaret={player.cardsInHand} 
                            postava={player.character} 
                            naTahu={gameState.turnPlayerId === player.id}
                            vylozeneKarty={player.inPlayCards}
                            povoleneUI={gameState.allowedUIElements}
                            playerId={player.id}
                        />
            })}
        </div>
    );
}