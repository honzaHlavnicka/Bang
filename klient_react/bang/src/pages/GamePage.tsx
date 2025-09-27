
import MyThings from "../components/game/MyThings";
import Players from "../components/game/Players";
import CentralPanel from "../components/game/CentralPanel";
import { ZoomProvider } from "../modules/ZoomContext";
import ZoomDialog from "../components/zoomDialog";
import { useGame } from "../modules/GameKontext";

export default function GamePage() {
    
    return (
        <ZoomProvider>
            <ZoomDialog />

            <div style={{display:"flex",flexDirection:"column",height:"100vh"}}>
                <Players />
                <CentralPanel />
                <MyThings />
            </div>
        </ZoomProvider>
    );
}
