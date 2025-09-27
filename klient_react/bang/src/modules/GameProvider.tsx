import React, { useEffect, useState } from "react";
import { GameContext } from "./GameContext";
import type { GameStateType } from "./GameContext";

const gameStateDefault: GameStateType = {
    players: [],
    currentPlayerId: "",
    turnOrder: [],
    deckCount: 0,
    discardPile: [],
};

export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);

    useEffect(() => {
        const socket = new WebSocket("ws://localhost:9999");
        console.log("pokus o připojení k ws serveru");
        socket.onopen = () => setWs(socket);
        socket.onmessage = (event) => {
            console.log("%cZpráva ze serveru: " + event.data, "color: green");
        };
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            setWs(null);
        };
        return () => socket.close();
    }, []);

    const sendMessage = (msg: any) => {
        if (ws !== null) {
            ws.send(JSON.stringify(msg));
        } else {
            console.log("WebSocket není připojen");
        }
    };

    return (
        <GameContext.Provider value={{ gameState, sendMessage }}>
            {children}
        </GameContext.Provider>
    );
}