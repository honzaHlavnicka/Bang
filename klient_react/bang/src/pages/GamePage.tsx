
import MyThings from "../components/game/MyThings";
import Players from "../components/game/Players";
import CentralPanel from "../components/game/CentralPanel";
import GlobalNotifications from "../components/GlobalNotifications";
import { DndContext, type DragEndEvent } from "@dnd-kit/core";
import { useGame } from "../modules/GameContext";


export default function GamePage() {
    const {playCard,fireCard,putCardInPlay,putCardInPlayOnPlayer} = useGame();


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
        }else if(event.over?.id?.toString().startsWith("player-")){
            // Drop na hráče
            const cardId = parseInt(event.active.id as string);
            const playerId = parseInt((event.over.id as string).replace("player-", ""));
            putCardInPlayOnPlayer(cardId, playerId);
        }
        
    }
    return (
            <DndContext onDragEnd={onDragEnd}>
                <div style={{display:"flex",flexDirection:"column",height:"100dvh"}}>
                    <GlobalNotifications />
                    <Players />
                    <CentralPanel />
                    <MyThings />
                </div>
            </DndContext>
    );
}
