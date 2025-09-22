import React from "react";
import { Deck } from "./Deck";

export default function CentralPanel() {
    const [deckImages, setDeckImages] =  React.useState<string[]>([]);

    return (
        <div style={{flex:1, minHeight:0, display: "flex", flexDirection: "column"}}>
            <Deck images={deckImages} />
            <button
                style={{marginTop: "10px", alignSelf: "flex-start"}}
                onClick={() => {
                    setDeckImages(prev => [...prev, "/img/karty/bang.png"]);
                }}
            >
                Add Image to Deck
            </button>
        </div>
    );
}