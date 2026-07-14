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



export default function NameTag({jmeno,style,isDead=false,showDeadIndicator=true,isOnline=true,className}: {jmeno: string;style?: React.CSSProperties;isDead?: boolean;showDeadIndicator?: boolean;isOnline?: boolean;className?: string;}) {
    const {t} = useTranslation();
    jmeno = jmeno ? jmeno : t("nepojmenovaný hráč");
    const displayName = jmeno.length > 13 ? jmeno.slice(0, 12) + "…" : jmeno;
    const deadIndicator = isDead && showDeadIndicator ? " ☠️" : "";
    const onlineIndicator = !isOnline ? " (Off)" : "";
    const onlineColor = !isOnline ? "rgba(220, 220, 220, 0.6)" : (style?.backgroundColor || "white");
    const textDecoration = !isOnline ? "line-through" : "none";
    return (
        <span
            className={className}
            style={{ 
                ...nameCss, 
                ...style, 
                backgroundColor: onlineColor, 
                color: !isOnline ? "#888" : "black",
                textDecoration,
                display: "inline-flex",
                alignItems: "center",
                gap: "5px"
            }}
            title={jmeno + (!isOnline ? " (" + t("Odpojen") + ")" : "")}
        >
            {!isOnline && (
                <span 
                    style={{
                        width: "6px",
                        height: "6px",
                        borderRadius: "50%",
                        backgroundColor: "#ef4444"
                    }}
                />
            )}
            {deadIndicator}{displayName}{onlineIndicator}
        </span>
    );
}