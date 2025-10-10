import { useEffect, useState } from "react";

export default function DarkModeSwitch({style}: {style?: React.CSSProperties}) {
    const btnStyle: React.CSSProperties = {
        borderRadius: "50%",
        height: "2em",
        width: "2em",
        border: "1px solid black",
        backgroundColor: "white",
        padding: "5px",
        cursor: "pointer",
        fontSize: "1.7em",
        transition: "background-color 0.3s, color 0.3s",
    };

    const [isDark, setIsDark] = useState(
        document.documentElement.classList.contains("darkMode")
    );

    // Reaguj na externí změny classListu na <html>
    useEffect(() => {
        const update = () => setIsDark(document.documentElement.classList.contains("darkMode"));
        const observer = new MutationObserver(update);
        observer.observe(document.documentElement, { attributes: true, attributeFilter: ["class"] });
        return () => {
            observer.disconnect();
        };
    }, []);

    const toggle = () => {
        const next = !isDark;
        document.documentElement.classList.toggle("darkMode", next);
        setIsDark(next);
    };

    const menu = (e: React.MouseEvent) => {
        e.preventDefault();
        //Zatím nic
    };

    return (
        <div>
            {!isDark ? (
                <button
                    style={{ ...btnStyle, background: "black", ...style }}
                    onClick={toggle}
                    onContextMenu={e => {menu(e);}}
                >
                    🌙
                </button>
            ) : (
                <button style={{ ...btnStyle, background: "white",...style }} onClick={toggle} onContextMenu={e => {menu(e);}}>
                    ☀️
                </button>
            )}
        </div>
    );
}