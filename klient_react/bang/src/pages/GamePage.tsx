import MyThings from "../components/game/MyThings";
import Player from "../components/game/Player";

export default function GamePage() {
    
    return (
        <div style={{display:"flex",flexDirection:"column",height:"100vh"}}>

            <div style={{flex:"1",backgroundColor:"lightblue"}}>
                <Player />
            </div>
            <div style={{flex:"1",backgroundColor:"lightgreen"}}>prostredi</div>
            <MyThings />
        </div>
    );
}