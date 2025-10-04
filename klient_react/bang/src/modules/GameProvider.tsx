import React, { useEffect, useState, useRef } from "react";
import { GameContext } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, setGameValue, connectToGame, changePlayerName,chooseCharacter, createGame, startGame, playCard, drawCard } from "./gameActions";
import toast from "react-hot-toast";

const gameStateDefault: GameStateType = {
    gameStarted: false,
    inGame: false,

    gameCode: "",

    
    players: [],
    playerId: null,
    turnOrder: [],
    deckCount: 0,
    discardPile: [],
};

export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    useEffect(() => {
        const socketAdress =  "ws://localhost:9999";
        const socket = new WebSocket("ws://localhost:9999");
        console.log("pokus o připojení k ws serveru na adrese " + socketAdress);
        socket.onopen = () => {
            setWs(socket);
            toast.success("Připojeno k serveru");
            (window as unknown as { ws: WebSocket }).ws = socket; //TODO: odstranit testovací přiřazení
        };
        socket.onmessage = (event) => { handleGameMessage(event, setGameState, stateRef); };
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            setWs(null);
            toast.error("Byl jsi odpojen od serveru");
        };
        return () => socket.close();
    }, []);

    return (
        <GameContext.Provider value={{
            gameState,
            setGameValue: (data, type) => setGameValue(ws, data, type),
            connectToGame: (gameCode, name) => connectToGame(ws, setGameState, gameCode, name),
            changePlayerName: (newName) => changePlayerName(ws, newName),
            chooseCharacter: (characterName) => chooseCharacter(ws, setGameState, characterName),
            startGame: () => { startGame(ws) },
            endTurn: () => { /* implementace nebo prázdná funkce */ },
            createGame: (name) => createGame(ws, name),
            drawCard: () => drawCard(ws),
            playCard: (cardId) => playCard(ws, cardId),
        }}>
            {children}
        </GameContext.Provider>
    );
}