import React from "react";
import { Deck } from "./Deck";
import Card from "../Card";
import ZoomToggleButton from "../ZoomButton";

export default function CentralPanel() {
    const [deckImages, setDeckImages] =  React.useState<string[]>([]);

    return (
        <div style={{flex:1, minHeight:0, display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center", }}>
            <Deck images={deckImages} />
            <Card image={"/img/karty/zezadu.png"} name="dobírací balíček" />
            <button
                style={{}}
                onClick={() => {
                    setDeckImages(prev => [...prev, "/img/karty/bang.png"]);
                }}
            >
                Add Image to Deck
            </button>
            <ZoomToggleButton />
        </div>
    );
}