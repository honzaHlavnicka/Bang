import Card from "../Card";
import SmallCards from "./SmallCards";
import NameTag from "./NameTag";
import Cards from "../Cards";
import {type CardType } from "../../modules/GameContext";
import { useDroppable } from "@dnd-kit/core";


export default function Player({jmeno,postava = "TESTOVACI2",pocetKaret = 8,pocetZivotu = 0,vylozeneKarty=[],naTahu=false,povoleneUI,playerId}:{jmeno:string,postava?:string,pocetKaret?:number,pocetZivotu?:number,vylozeneKarty?:Array<CardType>|null,naTahu?:boolean,povoleneUI:string[],playerId:number}) {
    vylozeneKarty = vylozeneKarty ? vylozeneKarty : [];
    const isDead = pocetZivotu !== undefined && pocetZivotu <= 0;
    
    // Uděláme hráče droppable
    const {setNodeRef, isOver} = useDroppable({
        id: `player-${playerId}`,
    });
    
    const playerStyle: React.CSSProperties = {
        display: "flex",
        flexDirection: "column",
        borderRadius: "10px",
        backgroundColor: isOver ? "rgba(255, 255, 0, 0.3)" : "transparent",
        transition: "background-color 0.2s",
    };
    return (
            <div ref={setNodeRef} style={playerStyle}>
                <div style={{ display: "flex" , justifyContent:"center"}}>
                    <div>
                    <NameTag jmeno={jmeno} isDead={isDead} showDeadIndicator={povoleneUI.includes("ZIVOTY")} style={{backgroundColor:(naTahu?"yellow":"white")}}/>
                    <div style={{display:"flex",flexDirection:"row"}}>
                        {povoleneUI.includes("POSTAVA") ? <Card name={"postava:" + postava.toLowerCase()} image={`/img/karty/postavy/${postava}.png`} />: null}
                        {povoleneUI.includes("ZIVOTY") ? <Card name={`${pocetZivotu} životů.`} image={`/img/velkeZivoty/${pocetZivotu}zivoty.png`} />:null}
                    </div>
                    </div>
                    
                    <SmallCards count={pocetKaret} />
                </div>
                <div
                    style={{ transform: "scale(0.7)", transition: "transform 0.2s" }}
                    onMouseEnter={e => (e.currentTarget.style.transform = "scale(1.1)")}
                    onMouseLeave={e => (e.currentTarget.style.transform = "scale(0.7)")}
                >
                    <Cards cards={vylozeneKarty}  isRotated={false} />
                </div>
            </div>
    );
}