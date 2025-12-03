import { useEffect, useState } from 'react';
import css from '../styles/loginPage.module.css';
import { useGame } from '../modules/GameContext';
import toast from 'react-hot-toast';
import DarkModeSwitch from '../components/DarkModeSwitch';
import globalCSS from "../styles/global.module.css";

export default function LoginPage() {
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState('');
    const [idTypuHry, setIdTypuHry] = useState<number>(0);
    const { connectToGame, createGame, gameState, returnToGame } = useGame();
    const [openCard , setOpenCard] = useState<string>("pripojeni");
    //const [menu, setMenu] = useState({x:0,y:0,visible:false})
    

    useEffect(() => {
        

        const params = location.search
        .substring(1)
        .split("&")
        .map(param => param.split("="))
        .reduce((values, [key, value]) => {
            values[key] = decodeURIComponent(value);
            return values;
        }, {} as Record<string, string>);
        if (params.code) {
            // pokud je v URL kód hry, předvyplní ho do formuláře
            if (params.code.match(/^[0-9]{6}$/)) {
                if (gameCode !== params.code) {
                    setGameCode(params.code);
                }
            }
            setOpenCard("kod");
        }
    }, [setOpenCard, gameCode]);
    

    const gameToken = localStorage.getItem("gameToken");


    function zkontroluj(isToConnect: boolean = false) {
        if (jmeno.trim().length < 3) {
            toast.error("Udělej to jméno delší, prosím");
            return false;
        }
        if (jmeno.trim().length > 15) {
            toast.error("Udělej to jméno kratší, prosím");
            return false;
        }
        if (gameCode.trim().length != 6 && isToConnect) {
            toast.error("Kód hry musí mít 6 číslic");
        
            if (!window.confirm("Kód hry musí mít 6 číslic. Pokud si nejsi jistý, zkontroluj ho prosím ještě jednou. Pokračovat?")) {
                return false;
            }
        }

        if(!isToConnect && idTypuHry === -1){
            toast.error("Vyber typ hry, prosím");
            return false;
        }
        return true;

    }

    return (
        <div className={css.kontent}>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>

            <main  >
                    <div className={`${css.sectionCard} ${css.sectionHero} ${css.box}`} >
                <h1>Bang!</h1>
                <p>
                    tadyto bude text, který bude něco říkat. Teď sice něco říká, 
                    ale doopravdy o hře neřekne nic. jenom zabírá místo.
                </p>
                <div style={{display:"flex",flexDirection:"row",justifyContent:"center",gap:"10px",marginTop:"10px"}}>
                    {gameToken &&
                    <button 
                        className={globalCSS.button + " " + (openCard == "pripojeni" ? globalCSS.buttonActive : "")}
                        onClick={() => setOpenCard("pripojeni")}
                    >
                        Vrácení se
                    </button>}
                    <button 
                        className={globalCSS.button + " " + (openCard == "kod" ? globalCSS.buttonActive : "")}
                        onClick={() => setOpenCard("kod")}
                    >
                        Připojit se ke hře
                    </button>
                    <button 
                        className={globalCSS.button + " " + (openCard == "vytvoreni" ? globalCSS.buttonActive : "")}
                        onClick={() => setOpenCard("vytvoreni")}
                    >
                        Vytvořit novou hru
                    </button>   
                    </div>
                </div>
                {gameToken && !gameState.inGame && !gameState.playerId && openCard == "pripojeni" ?  (
                    
                    <div className={css.box + " " + css.sectionCard} >
                        <h2>Vrátit se k rozehrané hře</h2>
                        <button 
                            className={globalCSS.button + " " + css.btnRight} 
                            onClick={() => returnToGame()}
                        >Vrátit se</button>
                    </div>
                    
                ): null}
                {openCard == "kod" ?
                <div className={css.box + " " + css.sectionCard} >
                    <h2>Připojit se ke hře</h2>
                    <h4>Kód hry, kam se chceš přihlásit:</h4>
                    <input 
                        type="text" 
                        inputMode="numeric" 
                        pattern="[0-9]*"
                        value={(()=>{return gameCode})()}
                        onChange={e =>  {
                            setGameCode(e.target.value.replace(/[^0-9]/g, ''));
                        }}
                    />
                    <h4>Tvoje jméno:</h4>
                    <input value={jmeno} onChange={e => setJmeno(e.target.value)}/><br />
                    <button className={globalCSS.button} onClick={() => { if(zkontroluj(true)) connectToGame(gameCode,jmeno); }} >
                        Připojit se ke hře
                    </button>
                </div>
                : null}


                {openCard == "vytvoreni" ?                 
                <div className={css.box} >
                    <h2>Vytvořit novou hru</h2>
                    <h4>Typ hry</h4>
                    <select 
                        value={idTypuHry === -1 ? "" : String(idTypuHry)}
                        onChange={(e)=>{ setIdTypuHry(parseInt((e.target as HTMLSelectElement).value)); }}
                    >
                        {gameState.gameTypesAvailable ? gameState.gameTypesAvailable.map((val)=>(
                            <option key={val.id} value={val.id} title={val.description}>{val.name}</option>
                        )) : <option disabled>Načítám...</option>}
                        <option onClick={() =>location.href = "https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md"} value={-1} >Vytvoř si vlastní! [⇗]</option>
                    </select>
                    <p>
                        {gameState.gameTypesAvailable && idTypuHry !== -1
                            ? gameState.gameTypesAvailable.find(gt => gt.id === idTypuHry)?.description ?? ""
                            : ""}
                    </p>
                    <h4>Tvoje jméno:</h4>
                    <input
                        value={jmeno}
                        onChange={e => setJmeno(e.target.value)}
                    /><br />
                    <button 
                        onClick={() => { if(zkontroluj(false)) createGame(idTypuHry,jmeno); }} 
                        className={globalCSS.button}
                    >
                        vytvořit hru
                    </button>
                    <a href="https://www.flaticon.com/free-animated-icons/fire" title="fire animated icons">Fire animated icons created by Freepik - Flaticon</a>
                </div>
                : null}               
            </main >
            {/*<ContextMenu x={menu.x} y={menu.y} options={[{text:"odhodit"},{text:"spalit"}]} />*/}
        </div >
    );
}
