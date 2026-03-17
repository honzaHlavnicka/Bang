import React, { useEffect, useState, useRef } from "react";
import { GameContext, gameStateDefault } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, connectToGame, changePlayerName,chooseCharacter, createGame, startGame, playCard, drawCard, returnToGame, endTurn, fireCard, putCardInPlay, clickUIButton } from "./gameActions";
import toast from "react-hot-toast";
import { useDialog } from "./DialogContext";
import { notify } from "./notify";



export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);
    const { openDialog } = useDialog();

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    useEffect(() => {
        const socketAddress = `${import.meta.env.VITE_SERVER_PROTOCOL || "ws"}://${import.meta.env.VITE_SERVER_HOST || "localhost"}:${import.meta.env.VITE_SERVER_PORT || "22207"}/ws`;
        const params = new URLSearchParams(window.location.search);
        const addrParam = params.get("adress");
        let socketUrl = socketAddress;
        if (addrParam && addrParam.trim().length > 0) {
            socketUrl = addrParam.trim();
            if (!/^wss?:\/\//i.test(socketUrl)) {
            socketUrl = `ws://${socketUrl}`;
            }
            console.log("Using socket address from ?adress param: " + socketUrl);
        }

        const socket = new WebSocket(socketUrl);
        console.log("pokus o připojení k ws serveru na adrese " + socketAddress);
        socket.onopen = () => {
            setWs(socket);
            toast.success("Připojeno k serveru");
            if (import.meta.env.VITE_DEBUG) {
                console.log("Debug mode is ON. Socket address: " + socketUrl);
                (window as unknown as { ws: WebSocket }).ws = socket; //Přiřadí se pouze v debug módu, v produkci ochráněn před self-XSS
            }
            
        };
        socket.onmessage = (event) => { handleGameMessage(event, setGameState, stateRef,openDialog,socket,notify); };
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            setWs(null);
            toast.error("Byl jsi odpojen od serveru");
            openDialog({type:"INFO", data:{header:"Byl jsi odpojen od serveru",message:"Zkus znovu načíst stránku a kliknout na znovu se připojit ke hře."},dialogHeader:"Odpojení"});
        };
        return () => socket.close();
    }, []);

    return (
        <GameContext.Provider value={{
            gameState,
            connectToGame: (gameCode, name) => connectToGame(ws, setGameState, gameCode, name),
            changePlayerName: (newName) => changePlayerName(ws, newName),
            chooseCharacter: (characterName) => chooseCharacter(ws, setGameState, characterName),
            startGame: () => { startGame(ws) },
            endTurn: () => { endTurn(ws) },
            createGame: (gameTypeId,name) => createGame(ws,gameTypeId, name),
            drawCard: () => drawCard(ws),
            playCard: (cardId) => playCard(ws, cardId),
            returnToGame: () => { returnToGame(ws) },
            fireCard: (cardId) => fireCard(ws, cardId),
            putCardInPlay: (cardId) => putCardInPlay(ws, cardId),
            clickUIButton: (buttonId) => clickUIButton(ws, buttonId),
        }}>
            {children}
        </GameContext.Provider>
    );
}