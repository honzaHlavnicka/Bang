import { useEffect, useState } from 'react';
import css from '../styles/loginPage.module.css';
import { useGame } from '../modules/GameContext';
import toast from 'react-hot-toast';
import DarkModeSwitch from '../components/DarkModeSwitch';

export default function LoginPage() {
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState('');
    const { connectToGame, createGame, gameState, returnToGame } = useGame();

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
        }
    }, []);
    

    const gameToken = localStorage.getItem("gameToken");


    function zkontroluj(kodTaky: boolean = false) {
        if (jmeno.trim().length < 3) {
            toast.error("Udělej to jméno delší, prosím");
            return false;
        }
        if (jmeno.trim().length > 15) {
            toast.error("Udělej to jméno kratší, prosím");
            return false;
        }
        if (gameCode.trim().length != 6 && kodTaky) {
            toast.error("Kód hry musí mít 6 číslic");
        
            if (!window.confirm("Kód hry musí mít 6 číslic. Pokud si nejsi jistý, zkontroluj ho prosím ještě jednou. Pokračovat?")) {
                return false;
            }
        }
        return true;
    }

    return (
        <div className={css.kontent}>
            <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>

            <main>
                <h1>Bang!</h1>
                <p>
                    tadyto bude text, který bude něco říkat. Teď sice něco říká, 
                    ale doopravdy o hře neřekne nic. jenom zabírá místo.
                </p>
                <hr />
                {gameToken && !gameState.inGame && !gameState.playerId && (
                    <div>
                        <h4>Vrátit se k rozehrané hře</h4>
                        <button 
                            className={css.btnPrimary + " " + css.btnRight} 
                            onClick={() => returnToGame()}
                        >Připojit</button>
                    </div>
                )}
                <div>
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
                    <button className={css.btnPrimary} onClick={() => { if(zkontroluj(true)) connectToGame(gameCode,jmeno); }} >
                        Připojit se ke hře
                    </button>
                </div>
                <hr/>
                <div>
                    <h4>Tvoje jméno:</h4>
                    <input
                        value={jmeno}
                        onChange={e => setJmeno(e.target.value)}
                    /><br />
                    <button 
                        onClick={() => { if(zkontroluj(false)) createGame(jmeno); }} 
                        className={css.btnPrimary}
                    >
                        vytvořit hru
                    </button>
                </div>                
            </main >
        </div >
    );
}
