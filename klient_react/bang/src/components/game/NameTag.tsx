import React from "react";
import { useTranslation} from "react-i18next";

const nameCss = {
    backgroundColor: "white",
    borderRadius: "0.2em",
    fontSize: "large",
    padding: "10px",
    display: "block",
    width: "fit-content",
    marginTop: "5px",
    marginBottom: "5px",
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