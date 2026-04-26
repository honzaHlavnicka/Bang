import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

export default function DarkModeSwitch({ style }: { style?: React.CSSProperties }) {
    const { i18n } = useTranslation();

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
        // Zatím nic
    };

    const zmenJazyk = (novyJazyk: string) => {
        // 1. Změníme jazyk v i18n
        i18n.changeLanguage(novyJazyk);

        // 2. Přepíšeme URL adresu v prohlížeči (bez obnovení stránky!)
        const aktualniCesta = window.location.pathname; // např. "/cs/hra/123456"
        const cesty = aktualniCesta.split('/').filter(Boolean); // rozseká na ["cs", "hra", "123456"]

        // Pokud už tam nějaký jazyk je (cs/en), nahradíme ho. Pokud není, přidáme ho.
        if (cesty.length > 0 && (cesty[0] === 'cs' || cesty[0] === 'en')) {
            cesty[0] = novyJazyk;
        } else {
            cesty.unshift(novyJazyk);
        }

        // Poskládáme novou URL a podstrčíme ji prohlížeči
        const novaUrl = '/' + cesty.join('/') + window.location.search;
        window.history.pushState({}, '', novaUrl);
    };

    // 1. Styl celého "pilulkového" obalu
    const pillStyle: React.CSSProperties = {
        display: "flex",
        alignItems: "center",
        backgroundColor: isDark ? "rgba(40, 40, 40, 0.9)" : "rgba(255, 255, 255, 0.9)",
        border: isDark ? "1px solid #555" : "1px solid #ccc",
        borderRadius: "50px", // Udělá z obdélníku pilulku
        padding: "4px", // Vnitřní mezera, aby kolečko nebylo úplně nalepené na hraně
        gap: "12px", // Mezera mezi kolečkem a vlajkami
        backdropFilter: "blur(5px)", // Pěkný efekt, když okno plave nad hrou
        boxShadow: "0 2px 5px rgba(0,0,0,0.2)",
        ...style, // Zde se aplikuje tvé position: fixed z Dialog.tsx
    };

    // 2. Styl samotného kolečka (vyplňuje výšku podle velikosti písma)
    const btnStyle: React.CSSProperties = {
        borderRadius: "50%",
        height: "1.5em", // Velikost se odvíjí od font-size v props
        width: "1.5em",
        border: isDark ? "1px solid #fff" : "1px solid #000",
        backgroundColor: isDark ? "white" : "black",
        color: isDark ? "black" : "white",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        cursor: "pointer",
        fontSize: "1em",
        transition: "background-color 0.3s, color 0.3s",
        padding: 0, // Důležité, aby z toho nebyla šiška
    };

    // 3. Dynamický styl pro vlaječky (neaktivní jsou šedé a poloprůhledné)
    const flagStyle = (isActive: boolean): React.CSSProperties => ({
        background: "none",
        border: "none",
        cursor: "pointer",
        fontSize: "1.1em",
        padding: "0 2px",
        filter: isActive ? "none" : "grayscale(100%) opacity(40%)",
        transition: "all 0.3s",
        userSelect: "none"
    });

    return (
        <div style={pillStyle}>
            {/* Tlačítko pro tmavý/světlý režim */}
            <button
                style={btnStyle}
                onClick={toggle}
                onContextMenu={menu}
                title={isDark ? "Přepnout na světlý režim" : "Přepnout na tmavý režim"}
            >
                {isDark ? "☀️" : "🌙"}
            </button>

            {/* Přepínač jazyků */}
            <div style={{ display: "flex", gap: "6px", paddingRight: "6px" }}>
                <button 
                    style={flagStyle(i18n.resolvedLanguage === "cs")} 
                    onClick={() => zmenJazyk("cs")}
                    title="Čeština"
                >
                    🇨🇿
                </button>
                <button 
                    style={flagStyle(i18n.resolvedLanguage === "en")} 
                    onClick={() => zmenJazyk("en")}
                    title="English"
                >
                    🇬🇧
                </button>
            </div>
        </div>
    );
}