import React from "react";
import { useTranslation} from "react-i18next";

const nameCss = {
    backgroundColor: "white",
    borderRadius: "0.2em",
    fontSize: "clamp(0.85rem, 1.5vw, 1.2rem)",
    padding: "clamp(4px, 1vw, 10px)",
    display: "block",
    width: "fit-content",
    marginTop: "clamp(2px, 0.5vw, 5px)",
    marginBottom: "clamp(2px, 0.5vw, 5px)",
    marginLeft: "5px",
};



export default function NameTag({jmeno,style,isDead=false,showDeadIndicator=true}: {jmeno: string;style?: React.CSSProperties;isDead?: boolean;showDeadIndicator?: boolean;}) {
    const {t} = useTranslation();
    jmeno = jmeno ? jmeno : t("nepojmenovaný hráč");
    const displayName = jmeno.length > 13 ? jmeno.slice(0, 12) + "…" : jmeno;
    const deadIndicator = isDead && showDeadIndicator ? " ☠️" : "";
    return (
        <span
            style={{ ...nameCss, ...style }}
            title={jmeno}
        >
            {deadIndicator}{displayName}
        </span>
    );
}