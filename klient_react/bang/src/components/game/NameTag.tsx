import React from "react";

const nameCss = {
    backgroundColor: "white",
    borderRadius: "0.2em",
    fontSize: "large",
    padding: "10px",
    display: "block",
    width: "fit-content",
    marginTop: "5px",
    marginBottom: "1em"
};



export default function NameTag({jmeno,style,isDead=false,showDeadIndicator=true}: {jmeno: string;style?: React.CSSProperties;isDead?: boolean;showDeadIndicator?: boolean;}) {
    jmeno = jmeno ? jmeno : "bezejmený hráč";
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