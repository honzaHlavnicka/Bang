import { useState } from 'react';
import css from './loginPage.module.css';

type LoginPageProps = {
    spustitHru: (vytvorit: boolean) => void;
};

export default function LoginPage({ spustitHru }: LoginPageProps) {
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState('');
    function zkontroluj(kodTaky: boolean = false) {
        if(jmeno.trim().length < 3){
            alert("Udělej to jméno delší, prosím");
            return;
        }
        if(jmeno.trim().length > 15){
            alert("Udělej to jméno kratší, prosím");
            return;
        }
        if(gameCode.trim().length != 6 && kodTaky){
            if (!window.confirm("Kód hry musí mít 6 číslic. Pokud si nejsi jistý, zkontroluj ho prosím ještě jednou. Pokračovat?")) {
                return;
            }
        }
        spustitHru(true);
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
                    Kód hry, kam se chceš přihlásit:<br />
                    <input 
                        type="text" 
                        inputMode="numeric" 
                        pattern="[0-9]*"
                        value={gameCode}
                        onChange={e => setGameCode(e.target.value.replace(/[^0-9]/g, ''))}
                    /><br />
                    
                    Tvoje jméno:<br />
                    <input
                        value={jmeno}
                        onChange={e => setJmeno(e.target.value)}
                    /><br />
                    
                    <button className="btn-primary" onClick={() => { zkontroluj(true) }} 
                    
                    >Připojit se ke hře</button>
                </div>
                <hr />
                Tvoje jméno:
                <input
                    value={jmeno}
                    onChange={e => setJmeno(e.target.value)}
                /><br />
                <button 
                    onClick={() => { zkontroluj(false) }} 
                    className="btn-primary"
                >
                    vytvořit hru
                </button>
            </main>
        </div>
    );
}
