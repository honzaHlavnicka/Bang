
import MyThings from "../components/game/MyThings";
import Players from "../components/game/Players";
import CentralPanel from "../components/game/CentralPanel";

export default function GamePage() {
    return (
        <div style={{display:"flex",flexDirection:"column",height:"100vh"}}>
            <Players />
            <CentralPanel />
            <MyThings />
        </div>
    );
}
