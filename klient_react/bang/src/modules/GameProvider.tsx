import React, { useEffect, useState } from "react";
import { GameContext } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, setGameValue, connectToGame, changePlayerName,chooseCharacter } from "./gameActions";

const gameStateDefault: GameStateType = {
    gameStarted: false,
    inGame: false,

    gameCode: "",

    
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
        socket.onmessage = (event) => handleGameMessage(event, setGameState);
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            setWs(null);
        };
        return () => socket.close();
    }, []);

    return (
        <GameContext.Provider value={{
            gameState,
            setGameValue: (data, type) => setGameValue(ws, data, type),
            connectToGame: (gameCode, name) => connectToGame(ws, gameCode, name),
            changePlayerName: (newName) => changePlayerName(ws, newName),
            chooseCharacter: (characterName) => chooseCharacter(ws, setGameState, characterName),
            startGame: () => {/* implementace nebo prázdná funkce */},
            endTurn: () => {/* implementace nebo prázdná funkce */} 
        }}>
            {children}
        </GameContext.Provider>
    );
}