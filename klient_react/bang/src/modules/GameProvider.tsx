import React, { useEffect, useState, useRef } from "react";
import { GameContext, gameStateDefault } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, connectToGame, changePlayerName, chooseCharacter, createGame, startGame, playCard, drawCard, returnToGame, endTurn, fireCard, putCardInPlay, putCardInPlayOnPlayer, clickUIButton, startNewGameAndDeleteThisOne, kickPlayer } from "./gameActions";
import toast from "react-hot-toast";
import { useDialog } from "./DialogContext";
import { notify } from "./notify";
import { useTranslation } from "react-i18next";
import posthog from "./posthog";
import { getMockActions } from "./mockData";

export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);
    const { openDialog, closeDialog } = useDialog();
    const { t } = useTranslation();

    const isMock = import.meta.env.DEV && new URLSearchParams(window.location.search).has("mock");

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    useEffect(() => {
        let isUnmounted = false;
        let reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
        let reconnectAttempt = 0;
        const maxConnectAttempts = 3;

        if (isMock) {
            console.log("Mock Mode is ACTIVE! No server connection will be made.");
            setGameState({
                ...gameStateDefault,
                startedConection: true,
                gameTypesAvailable: [
                    { id: 1, name: "Bang!", description: "Klasická kovbojská střílečka (Mock)", url: "/img/karty/bang.png" },
                    { id: 2, name: "Prší", description: "Klasická česká karetní hra (Mock)", url: "/img/karty/marias/sedma.png" }
                ]
            });
            return;
        }

        let activeSocket: WebSocket | null = null;

        const connectSocket = () => {
            if (isUnmounted) return;

            // Zrušíme předchozí běžící časovač reconnectu, aby nevznikaly paralelní pokusy
            if (reconnectTimeout) {
                clearTimeout(reconnectTimeout);
                reconnectTimeout = null;
            }

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

            console.log(`Connecting to WebSocket (attempt ${reconnectAttempt + 1}/${maxConnectAttempts})...`);
            const socket = new WebSocket(socketUrl);
            activeSocket = socket;

            socket.onopen = () => {
                if (isUnmounted) {
                    socket.close();
                    return;
                }
                setWs(socket);
                reconnectAttempt = 0;
                toast.dismiss("reconnect-toast");
                toast.success(t("Připojeno k serveru"));
                posthog.capture('socket_connected', { url: socketUrl });

                // Pokud máme uložený token, ověříme jeho platnost na serveru
                const token = sessionStorage.getItem("gameToken") || localStorage.getItem("gameToken");
                if (token) {
                    socket.send("overeniTokenu:" + token);
                }

                // Pokud se nám podařilo znovupřipojit, zavřeme chybový dialog odpojení, pokud byl otevřený
                closeDialog();

                if (import.meta.env.VITE_DEBUG) {
                    console.log("Debug mode is ON. Socket address: " + socketUrl);
                    (window as unknown as { ws: WebSocket }).ws = socket;
                }

                // Pokud jsme už byli ve hře, automaticky se znovupřipojíme pomocí tokenu
                const current = stateRef.current;
                if (current.inGame && token) {
                    console.log(`Auto-rejoining game using token: ${token}`);
                    setGameState(prev => ({
                        ...gameStateDefault,
                        name: prev.name,
                        gameCode: prev.gameCode,
                        gameTypesAvailable: prev.gameTypesAvailable,
                        inGame: true,
                        startedConection: true
                    }));
                    socket.send("vraceniSe:" + token);
                    toast.success(t("Znovupřipojeno ke hře"));
                }
            };

            socket.onmessage = (event) => {
                if (isUnmounted) return;
                handleGameMessage(event, setGameState, stateRef, openDialog, socket, notify);
            };

            socket.onclose = () => {
                if (isUnmounted) return;
                setWs(null);
                console.log("WebSocket disconnected");

                // Pokus o automatické znovupřipojení
                if (reconnectAttempt < maxConnectAttempts) {
                    reconnectAttempt += 1;
                    const delay = 1500;
                    toast.loading(
                        `${t("Připojení ztraceno. Pokouším se o znovupřipojení...")} (${reconnectAttempt}/${maxConnectAttempts})`,
                        { id: "reconnect-toast" }
                    );
                    
                    reconnectTimeout = setTimeout(() => {
                        connectSocket();
                    }, delay);
                } else {
                    // Automatické připojení selhalo, ukážeme chybový dialog
                    posthog.capture('socket_disconnected');
                    toast.dismiss("reconnect-toast");
                    toast.error(t("Byl jsi odpojen od serveru"));

                    const current = stateRef.current;
                    const token = sessionStorage.getItem("gameToken") || localStorage.getItem("gameToken");
                    if (current.inGame && token) {
                        openDialog({
                            type: "CONFIRM",
                            data: {
                                title: t("Odpojení"),
                                message: t("Byl jsi odpojen od serveru. Chceš se zkusit znovu připojit?")
                            },
                            dialogHeader: t("Odpojení"),
                            callback: (confirmed) => {
                                if (confirmed) {
                                    sessionStorage.setItem("autoreturn", "true");
                                    window.location.reload();
                                }
                            }
                        });
                    } else {
                        openDialog({
                            type: "INFO",
                            data: {
                                header: t("Odpojení"),
                                message: t("Zkus znovu načíst stránku a kliknout na znovu se připojit ke hře.")
                            },
                            dialogHeader: t("Odpojení")
                        });
                    }
                }
            };
        };

        const handleVisibilityChange = () => {
            if (document.visibilityState === "visible") {
                console.log("Tab became visible. Checking connection status...");
                // Pokud nejsme připojeni k serveru, vynutíme okamžitý pokus o připojení
                if (!activeSocket || activeSocket.readyState === WebSocket.CLOSED || activeSocket.readyState === WebSocket.CLOSING) {
                    console.log("Active socket is closed. Auto-reconnecting immediately...");
                    reconnectAttempt = 0;
                    connectSocket();
                }
            }
        };

        connectSocket();
        document.addEventListener("visibilitychange", handleVisibilityChange);

        return () => {
            isUnmounted = true;
            document.removeEventListener("visibilitychange", handleVisibilityChange);
            if (activeSocket) {
                activeSocket.close();
            }
            if (reconnectTimeout) {
                clearTimeout(reconnectTimeout);
            }
            toast.dismiss("reconnect-toast");
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Mockovací akce
    const mockActions = getMockActions(setGameState);

    return (
        <GameContext.Provider value={{
            gameState,
            isConnected: ws !== null,
            connectToGame: (gameCode, name) => isMock ? mockActions.connectToGame(gameCode, name) : connectToGame(ws, setGameState, gameCode, name),
            changePlayerName: (newName) => isMock ? mockActions.changePlayerName(newName) : changePlayerName(ws, newName),
            chooseCharacter: (characterName) => isMock ? mockActions.chooseCharacter(characterName) : chooseCharacter(ws, setGameState, characterName),
            startGame: () => { if (isMock) mockActions.startGame(); else startGame(ws); },
            endTurn: () => { if (isMock) mockActions.endTurn(); else endTurn(ws); },
            createGame: (gameTypeId, name) => isMock ? mockActions.createGame(gameTypeId, name) : createGame(ws, gameTypeId, name),
            drawCard: () => isMock ? mockActions.drawCard() : drawCard(ws),
            playCard: (cardId) => isMock ? mockActions.playCard(cardId) : playCard(ws, cardId),
            returnToGame: () => { if (isMock) mockActions.returnToGame(); else returnToGame(ws, setGameState); },
            fireCard: (cardId) => isMock ? mockActions.fireCard(cardId) : fireCard(ws, cardId),
            putCardInPlay: (cardId) => isMock ? mockActions.putCardInPlay(cardId) : putCardInPlay(ws, cardId),
            putCardInPlayOnPlayer: (cardId, playerId) => isMock ? mockActions.putCardInPlayOnPlayer(cardId, playerId) : putCardInPlayOnPlayer(ws, cardId, playerId),
            clickUIButton: (buttonId) => isMock ? mockActions.clickUIButton(buttonId) : clickUIButton(ws, buttonId),
            startNewGameAndDeleteThisOne: () => isMock ? mockActions.startNewGameAndDeleteThisOne() : startNewGameAndDeleteThisOne(ws, openDialog, gameState),
            kickPlayer: (playerId) => { if (!isMock) kickPlayer(ws, playerId); }
        }}>
            {children}
        </GameContext.Provider>
    );
}