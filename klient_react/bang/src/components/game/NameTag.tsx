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



export default function NameTag({jmeno,style,}: {jmeno: string;style?: React.CSSProperties;}) {
    const displayName = jmeno.length > 13 ? jmeno.slice(0, 12) + "…" : jmeno;
    return (
        <span
            style={{ ...nameCss, ...style }}
            title={jmeno}
        >
            {displayName}
        </span>
    );
}