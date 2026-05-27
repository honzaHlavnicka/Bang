import { useEffect, useState } from 'react';
import css from '../styles/loginPage.module.css';
import { useGame } from '../modules/GameContext';
import toast from 'react-hot-toast';
import DarkModeSwitch from '../components/DarkModeSwitch';
import globalCSS from "../styles/global.module.css";
import { useTranslation } from 'react-i18next';
import i18n from '../../i18n';
import { usePostHog } from '@posthog/react';
import config from '../config';

export default function LoginPage() {
    const { t } = useTranslation();
    const posthog = usePostHog();
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState('');
    const [zobrazenaPaticka, setZobrazenaPaticka] = useState(config.showCookies);
    const [idTypuHry, setIdTypuHry] = useState<number>(0);
    const { connectToGame, createGame, gameState, returnToGame } = useGame();
    const [openCard , setOpenCard] = useState<string>("pripojeni");
    const gameToken = localStorage.getItem("gameToken");
    const [worldQuizVisible, setWorldQuizVisible] = useState(true);
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
                setGameCode(params.code);
            }
            setOpenCard("kod");
        }
    }, []);

    useEffect(() => {
        const autoconnect = sessionStorage.getItem("autoconnect");
        if (autoconnect !== "" && autoconnect !== null && autoconnect !== undefined && autoconnect.indexOf(",") == 6) {
            const [code,name] = autoconnect.split(",", 2);
            setJmeno(name);
            setGameCode(code);
            setOpenCard("pripojeni");
            connectToGame(code, name);
            sessionStorage.removeItem("autoconnect");
            return;
        }
    }, [connectToGame]);

    // Klávesové zkratky pro přepínání karet (Z, P, V)
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (document.activeElement?.tagName === "INPUT" || 
                document.activeElement?.tagName === "TEXTAREA" ||
                document.activeElement?.tagName === "SELECT") {
                return;
            }

            const key = e.key.toLowerCase();
            if (key === "z" && gameToken) {
                setOpenCard("pripojeni");
            } else if (key === "p") {
                setOpenCard("kod");
            } else if (key === "v") {
                setOpenCard("vytvoreni");
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [gameToken]);
    
    useEffect(() => {
        const souhlas = localStorage.getItem("souhlas");
        if (souhlas === "true") {
            setZobrazenaPaticka(false);
        }
    }, []);


    function zkontroluj(isToConnect: boolean = false) {
        if (jmeno.trim().length < 3) {
            toast.error(t("Udělej to jméno delší, prosím"));
            return false;
        }
        if (jmeno.trim().length > 15) {
            toast.error(t("Udělej to jméno kratší, prosím"));
            return false;
        }
        if (gameCode.trim().length != 6 && isToConnect) {
            toast.error(t("Kód hry musí mít 6 číslic"));

            if (!window.confirm(t("Kód hry musí mít 6 číslic. Pokud si nejsi jistý, zkontroluj ho prosím ještě jednou. Pokračovat?"))) {
                return false;
            }
        }

        if(!isToConnect && idTypuHry === -1){
            toast.error(t("vyber typ hry, prosím"));
            return false;
        }
        return true;

    }

    return (
        <div className={css.kontent}>
            <DarkModeSwitch  style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>

            <main  className={css.paddingBottom} >
                    <div className={`${css.sectionCard} ${css.sectionHero} ${css.box}`} >
                <h1>{t("NAZEV_STRÁNKY")}</h1>
                <p>
                   {t("LOGIN_POPIS_BEZ_ODKAZU")} <a href='https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md' target='_blank'>{t("plugin")}</a>.
                </p>
                <div className={css.radioButtonsParent} >
                    {gameToken &&
                    <button 
                        className={globalCSS.button + " " + (openCard == "pripojeni" ? globalCSS.buttonActive : "") + " " + css.radioButton}
                        onClick={() => {setOpenCard("pripojeni");setWorldQuizVisible(false)}}
                    >
                        <span>{t("VRÁTIT_SE_DLOUHÉ")}</span><span>{t("VRÁTIT_SE_KRÁTKÉ")}</span>
                    </button>}
                    <button 
                        className={globalCSS.button + " " + (openCard == "kod" ? globalCSS.buttonActive : "")+ " " + css.radioButton}
                        onClick={() => {setOpenCard("kod");setWorldQuizVisible(false)}}
                    >
                        <span>{t("PŘIPOJIT_SE_KE_HŘE_DLOUHÉ")}</span><span>{t("PŘIPOJIT_KRÁTKÉ")}</span>
                    </button>
                    <button 
                        className={globalCSS.button + " " + (openCard == "vytvoreni" ? globalCSS.buttonActive : "")+ " " + css.radioButton}
                        onClick={() => {setOpenCard("vytvoreni");setWorldQuizVisible(false)}}
                    >
                        <span>{t("VYTVOŘIT_HRU_DLOUHÉ")}</span><span>{t("VYTVOŘIT_HRU_KRÁTKÉ")}</span>
                    </button>   
                    </div>
                    </div>
                    {worldQuizVisible ?
                    <div className={css.box} >
                        <div style={{textAlign:"center",fontSize:".8em"}}>
                            {t("Chceš hrát sám zeměpisný kvíz, nebo navštívit největší databázi vlajek?")}<br />
                            <a 
                                className={globalCSS.button + " " + css.btnSecondary} 
                                href={"https://world-quiz.com/" + i18n.language + "?utm_source=bang.honzaa.cz&utm_medium=referral&utm_campaign=bang&utm_campaign=na_titulni_strance"} 
                                target="_blank" 
                                onClick={() => posthog?.capture('external_link_clicked', { destination: 'world-quiz', location: 'login_page' })}
                            >
                                {t("World Quiz")}
                            </a>
                            <a 
                                className={globalCSS.button + " " + css.btnSecondary} 
                                href={"https://jef.world-quiz.com/" + i18n.language + "?utm_source=bang.honzaa.cz&utm_medium=referral&utm_campaign=bang&utm_campaign=na_titulni_strance"} 
                                target="_blank" 
                                onClick={() => posthog?.capture('external_link_clicked', { destination: 'just-enough-flags', location: 'login_page' })}
                            >
                                {t("Just Enough Flags")}
                            </a>
                        </div>
                    </div> : null}
                {gameToken && !gameState.inGame && !gameState.playerId && openCard == "pripojeni" ?  (
                    
                    <div className={css.box + " " + css.sectionCard} >
                        <h2>{t("Vrátit se k rozehrané hře")}</h2>
                        <button
                            className={globalCSS.button + " " + css.btnRight}
                            onClick={() => { posthog?.capture('game_returned_to'); returnToGame(); }}
                        >{t("vrátit se")}</button>
                    </div>
                    
                ): null}
                {openCard == "kod" ?
                <div className={css.box + " " + css.sectionCard} >
                    <h2>{t("Připojit se ke hře")}</h2>
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            if (zkontroluj(true)) {
                                posthog?.capture('game_joined', { game_code: gameCode, player_name: jmeno });
                                connectToGame(gameCode, jmeno);
                            }
                        }}
                    >
                        <h4>{t("Kód hry, kam se chceš přihlásit:")}</h4>
                        <input 
                            type="text" 
                            inputMode="numeric" 
                            pattern="[0-9]*"
                            value={(()=>{return gameCode})()}
                            onChange={e =>  {
                                setGameCode(e.target.value.replace(/[^0-9]/g, ''));
                            }}
                        />
                        <h4>{t("Tvoje jméno:")}</h4>
                        <input value={jmeno} onChange={e => setJmeno(e.target.value)}/><br />
                        <button type="submit" className={globalCSS.button}>
                            {t("Připojit se ke hře")}
                        </button>
                    </form>
                </div>
                : null}


                {openCard == "vytvoreni" ?                 
                <div className={css.box} >
                    <h2>{t("Vytvořit novou hru")}</h2>
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            if (zkontroluj(false)) {
                                const gameTypeName = gameState.gameTypesAvailable?.find(gt => gt.id === idTypuHry)?.name;
                                posthog?.capture('game_created', { game_type_id: idTypuHry, game_type_name: gameTypeName, player_name: jmeno });
                                createGame(idTypuHry, jmeno);
                            }
                        }}
                    >
                        <h4>{t("Typ hry")}</h4>
                        <select 
                            value={idTypuHry === -1 ? "" : String(idTypuHry)}
                            onChange={(e)=>{ setIdTypuHry(parseInt((e.target as HTMLSelectElement).value)); }}
                        >
                            {gameState.gameTypesAvailable ? gameState.gameTypesAvailable.map((val)=>(
                                <option key={val.id} value={val.id} title={val.description}>{val.name}</option>
                            )) : <option disabled>{t("Načítám...")}</option>}
                            <option onClick={() =>location.href = "https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md"} value={-1} >{t("Vytvoř si vlastní! [⇗]")}</option>
                        </select>
                        <p>
                            {gameState.gameTypesAvailable && idTypuHry !== -1
                                ? gameState.gameTypesAvailable.find(gt => gt.id === idTypuHry)?.description ?? ""
                                : ""}
                        </p>
                        <h4>{t("Tvoje jméno:")}</h4>
                        <input
                            value={jmeno}
                            onChange={e => setJmeno(e.target.value)}
                        /><br />
                        <button type="submit" className={globalCSS.button}>
                            {t("Vytvořit hru")}
                        </button>
                    </form>
                    
                </div>
                : null}               
            </main >
            {/*<ContextMenu x={menu.x} y={menu.y} options={[{text:"odhodit"},{text:"spalit"}]} />*/}
            {zobrazenaPaticka &&
            <footer className={css.footer}>
                <div className={css.footerContent}>

                    <p dangerouslySetInnerHTML={{ __html: t("footer.copyright") }} />
                    <nav>
                        <a href="/apidocs" target="_blank" rel="noopener noreferrer">{t("Dokumentace SDK")}</a> | <a href="https://honzaa.cz" target='_blank' >{t("honzaa.cz")}</a> | <a href="https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md" target='_blank' >{t("vytvoření pluginu")}</a> | <a href="https://github.com/honzaHlavnicka/Bang" target="_blank">{t("GitHub")}</a>
                    </nav>
                    <small>
                        <p>
                            <strong>{t("Upozornění:")}</strong> <span dangerouslySetInnerHTML={{ __html: t("footer.legal_text") }} />
                        </p>
                    </small>
                    <div style={{display: "flex", gap: "10px", marginTop: "16px", justifyContent: "center"}}>
                        <button onClick={() => { 
                            localStorage.setItem("souhlas", "true"); 
                            posthog?.set_config({ 
                                persistence: 'localStorage+cookie', 
                                disable_persistence: false,
                                disable_session_recording: false,
                                autocapture: true,
                                capture_performance: true
                            });
                            posthog?.startSessionRecording();
                            posthog?.opt_in_capturing();
                            posthog?.capture('consent_accepted'); 
                            setZobrazenaPaticka(false);
                        }} className={globalCSS.button}>
                            {t("Souhlasím")}
                        </button>
                        <button onClick={() => { 
                            setZobrazenaPaticka(false); 
                            localStorage.setItem("souhlas", "false"); 
                            posthog?.set_config({ 
                                persistence: 'memory', 
                                disable_persistence: true,
                                disable_session_recording: true,
                                autocapture: false,
                                capture_performance: false
                            });
                            posthog?.stopSessionRecording();
                            posthog?.opt_out_capturing();
                            posthog?.capture('consent_declined'); 
                        }} className={globalCSS.button} style={{backgroundColor: "rgba(100, 100, 100, 0.5)"}}>
                            {t("Nesouhlasím")}
                        </button>
                    </div>
                </div>
            </footer>
            }

            {config.showCookies && !zobrazenaPaticka && (
                <button 
                    onClick={() => setZobrazenaPaticka(true)}
                    title={t("Nastavení soukromí")}
                    style={{
                        position: "fixed",
                        bottom: "15px",
                        left: "15px",
                        zIndex: 1000,
                        width: "40px",
                        height: "40px",
                        borderRadius: "50%",
                        background: "rgba(0, 0, 0, 0.5)",
                        border: "1px solid rgba(255, 255, 255, 0.2)",
                        cursor: "pointer",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        padding: "0",
                        transition: "transform 0.2s, background 0.2s",
                        boxShadow: "0 2px 10px rgba(0,0,0,0.3)"
                    }}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.background = "rgba(0, 0, 0, 0.7)";
                        e.currentTarget.style.transform = "scale(1.1)";
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.background = "rgba(0, 0, 0, 0.5)";
                        e.currentTarget.style.transform = "scale(1)";
                    }}
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style={{color: "#fff"}}>
                        <path d="M12 2a10 10 0 1 0 10 10 4 4 0 0 1-5-5 4 4 0 0 1-5-5"/>
                        <path d="M8.5 8.5v.01"/>
                        <path d="M16 15.5v.01"/>
                        <path d="M12 12v.01"/>
                        <path d="M11 17v.01"/>
                        <path d="M7 14v.01"/>
                    </svg>
                </button>
            )}
        </div >

    );
}
