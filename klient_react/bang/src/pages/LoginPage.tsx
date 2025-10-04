import { useState } from 'react';
import css from './loginPage.module.css';
import { useGame } from '../modules/GameContext';

export default function LoginPage() {
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState('');
    const { connectToGame, createGame } = useGame();

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

    function zkontroluj(kodTaky: boolean = false) {
        if(jmeno.trim().length < 3){
            alert("Udělej to jméno delší, prosím");
            return false;
        }
        if(jmeno.trim().length > 15){
            alert("Udělej to jméno kratší, prosím");
            return false;
        }
        if(gameCode.trim().length != 6 && kodTaky){
            if (!window.confirm("Kód hry musí mít 6 číslic. Pokud si nejsi jistý, zkontroluj ho prosím ještě jednou. Pokračovat?")) {
                return false;
            }
        }
        return true;
    }

    return (
        <div className={css.kontent}>
            <main>
                <h1>Bang!</h1>
                <p>
                    tadyto bude text, který bude něco říkat. Teď sice něco říká, 
                    ale doopravdy o hře neřekne nic. jenom zabírá místo.
                </p>
                <hr />
                <div>
                    <h4>Kód hry, kam se chceš přihlásit:</h4>
                    <input 
                        type="text" 
                        inputMode="numeric" 
                        pattern="[0-9]*"
                        value={gameCode}
                        onChange={e => setGameCode(e.target.value.replace(/[^0-9]/g, ''))}
                    />
                    <h4>Tvoje jméno:</h4>
                    <input value={jmeno} onChange={e => setJmeno(e.target.value)}/><br />
                    <button className="btn-primary" onClick={() => { if(zkontroluj(true)) connectToGame(gameCode,jmeno) }} >
                        Připojit se ke hře
                    </button>
                </div>
                <hr/>
                <h4>Tvoje jméno:</h4>
                <input
                    value={jmeno}
                    onChange={e => setJmeno(e.target.value)}
                /><br />
                <button 
                    onClick={() => { if(zkontroluj(false)) createGame(jmeno); }} 
                    className="btn-primary"
                >
                    vytvořit hru
                </button>
            </main>
        </div>
    );
}
