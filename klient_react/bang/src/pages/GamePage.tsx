
import MyThings from "../components/game/MyThings";
import Players from "../components/game/Players";
import CentralPanel from "../components/game/CentralPanel";
import GlobalNotifications from "../components/GlobalNotifications";
import { notify } from "../modules/notify";


export default function GamePage() {
    
    return (
            <div style={{display:"flex",flexDirection:"column",height:"100vh"}}>
                <GlobalNotifications />
                <Players />
                <CentralPanel />
                <MyThings />
                <button style={{position:"fixed",bottom:10,right:10,zIndex:1000}} onClick={()=>{notify("Pozor!");}}>Test notifikace</button>
            </div>
    );
}
