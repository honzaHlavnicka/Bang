import { useEffect } from "react";
import { Wheel } from "react-custom-roulette";
import globalCSS from "../styles/global.module.css";
import { useState } from "react";

const LuckyWheel = ({ options, chosedOptionId, onOk } : { options: {id: number;name: string;color: string;size?: number;}[], chosedOptionId: number, onOk: () => void }) => {
    const [mustSpin, setMustSpin] = useState(false);

    const [hasStopped, setHasStopped] = useState(false);

    useEffect(() => {
        // Tady je ten trik! Počkáme 100ms, než se kolo nakreslí, a pak ho roztočíme.
        // Tím se vyhneme chybě "NaN".
        const timer = setTimeout(() => setMustSpin(true), 100);
        return () => clearTimeout(timer);
    }, []);

    const prizeIndex = options.findIndex(o => o.id === chosedOptionId);
    const safePrizeIndex = prizeIndex >= 0 ? prizeIndex : 0;

    return (
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 10 }}>
            <Wheel 
                mustStartSpinning={mustSpin}
                prizeNumber={safePrizeIndex}
                data={options.map(o => ({ option: o.name }))}
                backgroundColors={options.map(o => o.color)}
                onStopSpinning={() => {
                    setMustSpin(false); // Vypneme točení
                    setHasStopped(true); // Zobrazíme tlačítko
                }}
            />
           
                <button className={globalCSS.button} onClick={onOk}>
                    {!hasStopped ? "zavřít" : "OK"}
                </button>
            
        </div>
    );
};

export default LuckyWheel;