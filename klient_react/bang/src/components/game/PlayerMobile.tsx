import { useState } from "react";
import { useGame, type Player } from "../../modules/GameContext";
import NameTag from "./NameTag";
import Card from "../Card";
import globalCss from "../../styles/global.module.css";
import SmallCards from "./SmallCards";
import ZoomToggleButton from "../ZoomButton";
import { useTranslation } from "react-i18next";

export default function PlayerMobile({ player }: { player: Player }) {
    const [detailsOpen, setDetailsOpen] = useState(false);
    const { gameState } = useGame();
    const { t } = useTranslation();

    return (
        <>
        <div style={{ backgroundColor: "#f0f0f07", height: "100%",padding:"10px" }} onClick={() => {setDetailsOpen(true);}}>
            <NameTag jmeno={player.name} />
            { Array.from({length: player.health }).map((_, i) => (
                <span style={{ color: "red" }} key={i}>❤︎</span>
            ))}
            {/* Karty v ruce */}
            <div style={{ display: "flex", justifyContent: "center", flexWrap: "wrap" }}>
                {player.cardsInHand 
                    ? Array.from({ length: player.cardsInHand }).map((_, i) => (
                        <div 
                            style={{ width: "10px", height: "20px", background: "brown", margin: "2px", borderRadius: "3px" }} 
                            key={i}
                        />
                    )) 
                    : null
                }
            </div>
            
            {/* Karty ve hře */}
            <div style={{ display: "flex", justifyContent: "center", flexWrap: "wrap" ,color:"white"}}>
                {player.inPlayCards 
                    ? player.inPlayCards.map((card, index) => (
                        <div 
                            style={{ minWidth: "10px", height: "20px", background: "blue", margin: "2px", borderRadius: "3px" }} 
                            key={index}
                        >{card.image.slice(0,1)}</div>
                    )) 
                    : null
                }
            </div>
        </div>

        {detailsOpen && (
            <div style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                backgroundColor: 'rgba(0, 0, 0, 0.5)',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                zIndex: 999,
                backdropFilter: 'blur(3px)'

            }}
                onClick={() => setDetailsOpen(false)}
            >
                <div style={{
                    backgroundColor: '#fff',
                    background: "url('/img/pozadi2-desktop.webp')",

                    padding: '30px',
                    borderRadius: '10px',
                    textAlign: 'center',
                    boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                    position: 'relative',
                    maxWidth: '90%',
                    width: '320px',
                    overflow: "auto",
                    maxHeight:"99dvh",
                    margin: "10px"

                    
                }}
                    onClick={(e) => e.stopPropagation()}
                > 
                    <h3 style={{ marginTop: 0, marginBottom: '15px' }}>
                        <NameTag jmeno={player.name + (player.id === gameState.turnPlayerId ? ` (${t("player.on_turn")})` : "")} />

                    </h3>
                    <hr/>
                    <SmallCards count={player.cardsInHand} />

                    <div style={{ display: "flex", justifyContent: "center", marginBottom: "10px" }}>
                        {gameState.allowedUIElements.includes("ROLE") && <Card image={"/img/karty/role/" + player.role + ".png"} />}
                        {gameState.allowedUIElements.includes("POSTAVA") && <Card image={"/img/karty/postavy/" + player.character + ".png"} />}
                        {gameState.allowedUIElements.includes("ZIVOTY") && <Card image={"/img/velkeZivoty/" + player.health + "zivoty.png"} />}
                    </div>
                    {player.inPlayCards && player.inPlayCards.length > 0 && (
                        <div style={{ display: "flex", justifyContent: "center", flexWrap: "wrap" }}>
                            {player.inPlayCards.map((card, index) => (
                                <Card key={index} image={"/img/karty/" + card.image + ".png"}  animationOnStart={false} biggerOnHover={false} />
                            ))}
                        </div>
                    )}

                    

                </div>
                <div style={{ position: 'absolute', bottom: '10px', right: '10px' }} onClick={(e) => e.stopPropagation()}>
                    
                    <ZoomToggleButton />
                    <button 
                        onClick={() => setDetailsOpen(false)}
                        className={globalCss.button}
                    > {t("Zavřít")}</button>
                </div>
            </div>
            
    )}
        </>
    );


}