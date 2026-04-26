import React, { useEffect, useState, useRef } from "react";
import { GameContext, gameStateDefault } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, connectToGame, changePlayerName,chooseCharacter, createGame, startGame, playCard, drawCard, returnToGame, endTurn, fireCard, putCardInPlay, putCardInPlayOnPlayer, clickUIButton } from "./gameActions";
import toast from "react-hot-toast";
import { useDialog } from "./DialogContext";
import { notify } from "./notify";
import { voiceManager } from "../voice/voiceManager";



export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);
    const { openDialog: openDialogHook } = useDialog();

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    const openDialogRef = useRef(openDialogHook);
    useEffect(() => {
        openDialogRef.current = openDialogHook;
    }, [openDialogHook]);

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
        socket.onmessage = (event) => { handleGameMessage(event, setGameState, stateRef,openDialogRef.current,socket,notify); };
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            setWs(null);
            voiceManager.disconnect();
            toast.error("Byl jsi odpojen od serveru");
        };
        return () => {
            socket.close();
            voiceManager.disconnect();
        };
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
            putCardInPlayOnPlayer: (cardId, playerId) => putCardInPlayOnPlayer(ws, cardId, playerId),
            clickUIButton: (buttonId) => clickUIButton(ws, buttonId),
            voiceInit: async (userId, onVoiceUpdate, sendMessage) => {
                await voiceManager.init(userId, onVoiceUpdate, sendMessage);
            },
            voiceToggleMute: (isMuted) => {
                voiceManager.toggleMute(isMuted);
            },
            voiceDisconnect: () => {
                voiceManager.disconnect();
            }
        }}>
            {children}
        </GameContext.Provider>
    );
}