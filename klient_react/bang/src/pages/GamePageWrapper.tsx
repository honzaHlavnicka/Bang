import GamePage from "./GamePage";
//import css from "../styles/global.module.css"

export default function GamePageWrapper() {
    return (
        <div style={{display:"flex",flexDirection:"row"}}>
            <GamePage />
            {/*<div style={{height:"100dvh",backgroundColor:"#f4ece1d0", borderLeft: "4px double #8d6e63",padding:"1rem",marginLeft:"1rem",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
                <button className={css.button}>test</button>
            </div>*/}
        </div>
    );
}