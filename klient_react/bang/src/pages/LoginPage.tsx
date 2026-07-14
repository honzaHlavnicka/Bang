import { useEffect, useState } from 'react';
import css from '../styles/loginPage.module.css';
import { useGame } from '../modules/GameContext';
import toast from 'react-hot-toast';
import DarkModeSwitch from '../components/DarkModeSwitch';
import globalCSS from "../styles/global.module.css";
import { useTranslation } from 'react-i18next';
import { usePostHog } from '@posthog/react';
import config from '../config';
import CookieBar from '../components/CookieBar';
import DonateModal from '../components/DonateModal';

export default function LoginPage() {
    const { t , i18n} = useTranslation();
    const posthog = usePostHog();
    const [gameCode, setGameCode] = useState('');
    const [jmeno, setJmeno] = useState(() => localStorage.getItem("savedName") || '');
    const [zobrazenaPaticka, setZobrazenaPaticka] = useState(config.showCookies);
    const [idTypuHry, setIdTypuHry] = useState<number>(0);
    const { connectToGame, createGame, gameState, returnToGame, isConnected } = useGame();
    const [openCard , setOpenCard] = useState<string>("pripojeni");
    const gameToken = sessionStorage.getItem("gameToken") || localStorage.getItem("gameToken");
    const [worldQuizVisible, setWorldQuizVisible] = useState(true);
    const [donateOpen, setDonateOpen] = useState(false);
    const [h1Title, setH1Title] = useState("");
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
        if (!isConnected) return;

        const autoreturn = sessionStorage.getItem("autoreturn");
        if (autoreturn === "true") {
            sessionStorage.removeItem("autoreturn");
            returnToGame();
            return;
        }

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
    }, [connectToGame, returnToGame, isConnected]);

    // Předvyplnění výběru hry podle parametru ?game=
    useEffect(() => {
        if (!gameState.gameTypesAvailable || gameState.gameTypesAvailable.length === 0) return;

        const params = new URLSearchParams(location.search);
        const gameParam = params.get("game");
        if (gameParam) {
            const searchTerm = gameParam.toLowerCase().trim();
            const normalize = (str: string) => str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
            const normalizedSearch = normalize(searchTerm);

            const matchedGame = gameState.gameTypesAvailable.find(gt => {
                const name = gt.name.toLowerCase();
                const normalizedName = normalize(gt.name);
                return name === searchTerm || 
                       normalizedName === normalizedSearch || 
                       name.includes(searchTerm) || 
                       normalizedName.includes(normalizedSearch);
            });

            if (matchedGame) {
                setIdTypuHry(matchedGame.id);
                setOpenCard("vytvoreni");
                setH1Title(matchedGame.name);
            }
        }
    }, [gameState.gameTypesAvailable]);

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
        if (souhlas !== null) {
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
                <h1>{h1Title || t("NAZEV_STRÁNKY")}</h1>
                <p>
                   {t("LOGIN_POPIS_BEZ_ODKAZU")} <a href='https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md' target='_blank'>{t("plugin")}</a>.
                </p>
                <p>
                    {t("Na")} <a href={i18n.language.startsWith("cs") ? "https://bang.honzaa.cz/o-hrach" : "https://bang.honzaa.cz/en/about-games"} target="_blank">{t("link_této_stránce")}</a> {t("si můžete přečíst více.")}
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
                                localStorage.setItem("savedName", jmeno);
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
                                localStorage.setItem("savedName", jmeno);
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
                
                <footer className={css.footer}>
                    <div className={css.footerContent}>
                        <p dangerouslySetInnerHTML={{ __html: t("footer.copyright") }} />
                        <nav className={css.footerNav}>
                            <a href="/apidocs" target="_blank" rel="noopener noreferrer">{t("Dokumentace SDK")}</a>
                            <span className={css.separator}>•</span>
                            <a href="https://honzaa.cz" target="_blank" rel="noopener noreferrer">{t("honzaa.cz")}</a>
                            <span className={css.separator}>•</span>
                            <a href="https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md" target="_blank" rel="noopener noreferrer">{t("vytvoření pluginu")}</a>
                            <span className={css.separator}>•</span>
                            <a href="https://github.com/honzaHlavnicka/Bang" target="_blank" rel="noopener noreferrer">{t("GitHub")}</a>
                            <span className={css.separator}>•</span>
                            <a href="https://honzaa.itch.io/card-games" target="_blank" rel="noopener noreferrer">{t("itch.io")}</a>
                            <span className={css.separator}>•</span>
                            <button onClick={() => setDonateOpen(true)} className={css.linkButton}>{t("Podpořit hru")}</button>
                        </nav>
                    </div>
                </footer>
            </main >
            {/*<ContextMenu x={menu.x} y={menu.y} options={[{text:"odhodit"},{text:"spalit"}]} />*/}
            <CookieBar
                open={zobrazenaPaticka}
                onClose={() => setZobrazenaPaticka(false)}
                onOpen={() => setZobrazenaPaticka(true)}
            />
            <DonateModal isOpen={donateOpen} onClose={() => setDonateOpen(false)} />
        </div >

    );
}
