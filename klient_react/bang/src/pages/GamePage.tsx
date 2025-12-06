
import MyThings from "../components/game/MyThings";
import Players from "../components/game/Players";
import CentralPanel from "../components/game/CentralPanel";
import GlobalNotifications from "../components/GlobalNotifications";
import { notify } from "../modules/notify";
import { DndContext, type DragEndEvent } from "@dnd-kit/core";
import { useGame } from "../modules/GameContext";


export default function GamePage() {
    const {playCard,fireCard,putCardInPlay} = useGame();


    function onDragEnd(event:DragEndEvent) {
        if(event.over?.id === "discardPile"){
            const cardId = parseInt(event.active.id as string);
            playCard(cardId);
        }else if(event.over?.id === "fire"){
            const cardId = parseInt(event.active.id as string);
            fireCard(cardId);
        }else if(event.over?.id === "InPlayCards"){
            const cardId = parseInt(event.active.id as string);
            putCardInPlay(cardId);
            //alert("joooo"+cardId);
        }
        
    }
    return (
            <DndContext onDragEnd={onDragEnd}>
                <div style={{display:"flex",flexDirection:"column",height:"100vh"}}>
                    <GlobalNotifications />
                    <Players />
                    <CentralPanel />
                    <MyThings />
                    <button style={{position:"fixed",bottom:10,right:10,zIndex:1000}} onClick={()=>{notify("Pozor!");}}>Test notifikace</button>
                </div>
            </DndContext>
    );
}
