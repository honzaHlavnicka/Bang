import React, { useEffect, useState, useRef } from "react";
import { GameContext, gameStateDefault } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, connectToGame, changePlayerName, chooseCharacter, createGame, startGame, playCard, drawCard, returnToGame, endTurn, fireCard, putCardInPlay, putCardInPlayOnPlayer, clickUIButton, startNewGameAndDeleteThisOne } from "./gameActions";
import toast from "react-hot-toast";
import { useDialog } from "./DialogContext";
import { notify } from "./notify";
import { useTranslation } from "react-i18next";
import posthog from "./posthog";

// --- MOCK DATA PRO VÝVOJ BEZ SERVERU ---
const mockCardsPool = [
    { name: "Bang!", file: "bang.png" },
    { name: "Vedle!", file: "vedle.png" },
    { name: "Pivo", file: "pivo.png" },
    { name: "Dostavník", file: "dostavnik.png" },
    { name: "Wells Fargo", file: "wellsfargo.png" },
    { name: "Barel", file: "barel.png" },
    { name: "Mustang", file: "mustang.png" },
    { name: "Schofield", file: "schofield.png" },
    { name: "Volcanic", file: "volcanic.png" },
    { name: "Remington", file: "remington.png" },
    { name: "Dynamit", file: "dynamit.png" },
    { name: "Vězení", file: "vezeni.png" },
    { name: "Cat Balou", file: "catBalou.png" },
    { name: "Panika", file: "panika.png" },
    { name: "Hokynářství", file: "hokynarstvi.png" },
    { name: "Salón", file: "salon.png" },
    { name: "Duel", file: "duel.png" },
    { name: "Indiáni", file: "indiani.png" },
    { name: "Kulomet", file: "kulomet.png" }
];

const getInitialMockGameState = (name: string): GameStateType => {
    let nextCardId = 100;
    const generateCard = (file: string) => ({
        id: nextCardId++,
        image: file.replace(/\.(png|jpg|webp)$/i, "")
    });

    const handCards = [
        generateCard("bang.png"),
        generateCard("vedle.png"),
        generateCard("pivo.png"),
        generateCard("dostavnik.png"),
        generateCard("mustang.png"),
    ];

    const players = [
        {
            id: 1,
            name: name || "Honza (Ty)",
            role: "SERIF",
            health: 4,
            cardsInHand: handCards.length,
            character: "WILLY_THE_KID",
            isCurrentTurn: true,
            inPlayCards: [
                generateCard("barel.png"),
                generateCard("schofield.png"),
            ],
            isAdmin: true
        },
        {
            id: 2,
            name: "Pepa",
            role: "BANDITA",
            health: 3,
            cardsInHand: 3,
            character: "SUZY_LAFAYTTE",
            isCurrentTurn: false,
            inPlayCards: [
                generateCard("mustang.png"),
                generateCard("schofield.png"),
                generateCard("barel.png"),
                generateCard("vezeni.png"),
            ]
        },
        {
            id: 3,
            name: "Alena",
            role: "POMOCNIK",
            health: 4,
            cardsInHand: 4,
            character: "VULTURE_SAM",
            isCurrentTurn: false,
            inPlayCards: []
        },
        {
            id: 4,
            name: "Karel",
            role: "BANDITA",
            health: 1,
            cardsInHand: 2,
            character: "bartCassidy",
            isCurrentTurn: false,
            inPlayCards: [
                generateCard("vezeni.png")
            ]
        }
    ];

    return {
        startedConection: true,
        inGame: true,
        gameStarted: true,
        gameEnded: false,
        gameCode: "MOCK123",
        name: name || "Honza (Ty)",
        role: "SERIF",
        health: 4,
        maxHealth: 5,
        character: "WILLY_THE_KID",
        playerId: 1,
        isAdmin: true,
        handCards: handCards,
        inPlayCards: [
            generateCard("barel.png"),
            generateCard("schofield.png"),
        ],
        players: players,
        turnPlayerId: 1,
        turnOrder: ["1", "2", "3", "4"],
        deckCount: 42,
        discardPile: ["bang", "vedle"],
        allowedUIElements: ["ZIVOTY", "UKONCENI_TAHU", "POSTAVA", "ROLE", "VYLOZENE_KARTY", "ODHAZOVACI_BALICEK", "DOBIRACI_BALICEK"],
        customUIButtons: []
    };
};

export function GameProvider({ children }: { children: React.ReactNode }) {
    const [gameState, setGameState] = useState<GameStateType>(gameStateDefault);
    const [ws, setWs] = useState<WebSocket | null>(null);
    const { openDialog } = useDialog();
    const { t } = useTranslation();

    const isMock = import.meta.env.DEV && new URLSearchParams(window.location.search).has("mock");

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    useEffect(() => {
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
            posthog.capture('socket_connected', { url: socketUrl });
            toast.success(t("Připojeno k serveru"));
            if (import.meta.env.VITE_DEBUG) {
                console.log("Debug mode is ON. Socket address: " + socketUrl);
                (window as unknown as { ws: WebSocket }).ws = socket;
            }
        };
        socket.onmessage = (event) => { handleGameMessage(event, setGameState, stateRef, openDialog, socket, notify); };
        socket.onclose = () => {
            console.log("WebSocket disconnected");
            posthog.capture('socket_disconnected');
            setWs(null);
            toast.error(t("Byl jsi odpojen od serveru"));
            openDialog({
                type: "INFO",
                data: {
                    header: t("Byl jsi odpojen od serveru"),
                    message: t("Zkus znovu načíst stránku a kliknout na znovu se připojit ke hře.")
                },
                dialogHeader: t("Odpojení")
            });
        };
        return () => socket.close();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Mockovací akce
    const mockActions = {
        connectToGame: (gameCode: string, name: string) => {
            setGameState(prev => ({
                ...prev,
                startedConection: true,
                inGame: true,
                gameStarted: false,
                gameEnded: false,
                gameCode: gameCode || "MOCK123",
                name: name || "Honza",
                playerId: 1,
                isAdmin: true,
                handCards: [],
                inPlayCards: [],
                players: [
                    { id: 1, name: name || "Honza", role: "", health: 0, cardsInHand: 0, character: "", isCurrentTurn: false, inPlayCards: [], isAdmin: true },
                    { id: 2, name: "Pepa", role: "", health: 0, cardsInHand: 0, character: "", isCurrentTurn: false, inPlayCards: [] },
                    { id: 3, name: "Alena", role: "", health: 0, cardsInHand: 0, character: "", isCurrentTurn: false, inPlayCards: [] },
                    { id: 4, name: "Karel", role: "", health: 0, cardsInHand: 0, character: "", isCurrentTurn: false, inPlayCards: [] },
                ],
                turnOrder: ["1", "2", "3", "4"],
                deckCount: 0,
                discardPile: [],
                allowedUIElements: ["POSTAVA"],
                customUIButtons: [],
                characters: [
                    { jmeno: "Willy the Kid", obrazek: "WILLY_THE_KID", popis: "Během svého tahu může zahrát libovolný počet karet BANG!." },
                    { jmeno: "Suzy Lafayette", obrazek: "SUZY_LAFAYTTE", popis: "Jakmile nemá v ruce žádné karty, lízne si novou kartu." }
                ]
            }));
            toast.success("Mock: Připojeno do lobby");
        },
        createGame: (_gameTypeId: number, name: string) => {
            mockActions.connectToGame("MOCK123", name);
        },
        changePlayerName: (newName: string) => {
            setGameState(prev => ({
                ...prev,
                name: newName,
                players: prev.players ? prev.players.map(p => p.id === 1 ? { ...p, name: newName } : p) : null
            }));
            toast.success(`Mock: Jméno změněno na ${newName}`);
        },
        chooseCharacter: (characterName: string) => {
            setGameState(prev => ({
                ...prev,
                character: characterName,
                players: prev.players ? prev.players.map(p => p.id === 1 ? { ...p, character: characterName } : p) : null
            }));
            toast.success(`Mock: Vybrána postava ${characterName}`);
        },
        startGame: () => {
            const initial = getInitialMockGameState(gameState.name || "Honza");
            setGameState(initial);
            toast.success("Mock: Hra odstartována");
        },
        drawCard: () => {
            const randomCardInfo = mockCardsPool[Math.floor(Math.random() * mockCardsPool.length)];
            const newCard = { id: Date.now() + Math.floor(Math.random() * 1000), image: randomCardInfo.file.replace(/\.(png|jpg|webp)$/i, "") };
            setGameState(prev => {
                const nextHand = [...prev.handCards, newCard];
                const nextPlayers = prev.players ? prev.players.map(p => p.id === 1 ? { ...p, cardsInHand: nextHand.length } : p) : null;
                return {
                    ...prev,
                    handCards: nextHand,
                    players: nextPlayers,
                    deckCount: Math.max(0, prev.deckCount - 1)
                };
            });
            toast.success(`Mock: Dobral jsi kartu ${randomCardInfo.name}`);
        },
        playCard: (cardId: number) => {
            setGameState(prev => {
                const card = prev.handCards.find(c => c.id === cardId);
                if (!card) return prev;
                const nextHand = prev.handCards.filter(c => c.id !== cardId);
                const nextPlayers = prev.players ? prev.players.map(p => p.id === 1 ? { ...p, cardsInHand: nextHand.length } : p) : null;
                return {
                    ...prev,
                    handCards: nextHand,
                    players: nextPlayers,
                    discardPile: [card.image, ...prev.discardPile]
                };
            });
            toast.success("Mock: Karta zahozena");
        },
        putCardInPlay: (cardId: number) => {
            setGameState(prev => {
                const card = prev.handCards.find(c => c.id === cardId);
                if (!card) return prev;
                const nextHand = prev.handCards.filter(c => c.id !== cardId);
                const nextInPlay = [...prev.inPlayCards, card];
                const nextPlayers = prev.players ? prev.players.map(p => p.id === 1 ? { ...p, cardsInHand: nextHand.length, inPlayCards: nextInPlay } : p) : null;
                return {
                    ...prev,
                    handCards: nextHand,
                    inPlayCards: nextInPlay,
                    players: nextPlayers
                };
            });
            toast.success("Mock: Karta vyložena");
        },
        putCardInPlayOnPlayer: (cardId: number, playerId: number) => {
            setGameState(prev => {
                const card = prev.handCards.find(c => c.id === cardId);
                if (!card) return prev;
                const nextHand = prev.handCards.filter(c => c.id !== cardId);
                const nextPlayers = prev.players ? prev.players.map(p => {
                    if (p.id === 1) {
                        return { ...p, cardsInHand: nextHand.length };
                    } else if (p.id === playerId) {
                        return { ...p, inPlayCards: [...(p.inPlayCards || []), card] };
                    }
                    return p;
                }) : null;
                return {
                    ...prev,
                    handCards: nextHand,
                    players: nextPlayers
                };
            });
            toast.success("Mock: Karta vyložena na soupeře");
        },
        fireCard: (cardId: number) => {
            mockActions.playCard(cardId);
        },
        endTurn: () => {
            setGameState(prev => {
                const nextTurnPlayerId = 2; // Pepa's turn
                const nextPlayers = prev.players ? prev.players.map(p => ({
                    ...p,
                    isCurrentTurn: p.id === nextTurnPlayerId
                })) : null;
                return {
                    ...prev,
                    turnPlayerId: nextTurnPlayerId,
                    players: nextPlayers
                };
            });
            toast.success("Mock: Konec tvého tahu. Hraje Pepa...");
            
            // Pepa plays
            setTimeout(() => {
                setGameState(prev => {
                    const nextTurnPlayerId = 3; // Alena's turn
                    const nextPlayers = prev.players ? prev.players.map(p => ({
                        ...p,
                        isCurrentTurn: p.id === nextTurnPlayerId
                    })) : null;
                    return {
                        ...prev,
                        turnPlayerId: nextTurnPlayerId,
                        players: nextPlayers
                    };
                });
                toast.success("Mock: Pepa ukončil tah. Hraje Alena...");
                
                // Alena plays
                setTimeout(() => {
                    setGameState(prev => {
                        const nextTurnPlayerId = 4; // Karel's turn
                        const nextPlayers = prev.players ? prev.players.map(p => ({
                            ...p,
                            isCurrentTurn: p.id === nextTurnPlayerId
                        })) : null;
                        return {
                            ...prev,
                            turnPlayerId: nextTurnPlayerId,
                            players: nextPlayers
                        };
                    });
                    toast.success("Mock: Alena ukončila tah. Hraje Karel...");
                    
                    // Karel plays
                    setTimeout(() => {
                        setGameState(prev => {
                            const nextTurnPlayerId = 1; // User's turn
                            const nextPlayers = prev.players ? prev.players.map(p => ({
                                ...p,
                                isCurrentTurn: p.id === nextTurnPlayerId
                            })) : null;
                            return {
                                ...prev,
                                turnPlayerId: nextTurnPlayerId,
                                players: nextPlayers
                            };
                        });
                        toast.success("Mock: Karel ukončil tah. Jsi na řadě!");
                    }, 1500);
                }, 1500);
            }, 1500);
        },
        returnToGame: () => {
            setGameState(prev => ({ ...prev, inGame: true }));
        },
        clickUIButton: (buttonId: number) => {
            toast.success(`Mock: Kliknuto na tlačítko ${buttonId}`);
        },
        startNewGameAndDeleteThisOne: () => {
            setGameState({
                startedConection: true,
                inGame: false,
                gameStarted: false,
                gameEnded: false,
                gameCode: "",
                handCards: [],
                inPlayCards: [],
                players: [],
                playerId: null,
                turnOrder: [],
                deckCount: 0,
                discardPile: [],
                allowedUIElements: ["ZIVOTY","UKONCENI_TAHU","POSTAVA","ROLE","VYLOZENE_KARTY","ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"],
                customUIButtons: []
            });
            toast.success("Mock: Hra smazána");
        }
    };

    return (
        <GameContext.Provider value={{
            gameState,
            connectToGame: (gameCode, name) => isMock ? mockActions.connectToGame(gameCode, name) : connectToGame(ws, setGameState, gameCode, name),
            changePlayerName: (newName) => isMock ? mockActions.changePlayerName(newName) : changePlayerName(ws, newName),
            chooseCharacter: (characterName) => isMock ? mockActions.chooseCharacter(characterName) : chooseCharacter(ws, setGameState, characterName),
            startGame: () => { if (isMock) mockActions.startGame(); else startGame(ws); },
            endTurn: () => { if (isMock) mockActions.endTurn(); else endTurn(ws); },
            createGame: (gameTypeId, name) => isMock ? mockActions.createGame(gameTypeId, name) : createGame(ws, gameTypeId, name),
            drawCard: () => isMock ? mockActions.drawCard() : drawCard(ws),
            playCard: (cardId) => isMock ? mockActions.playCard(cardId) : playCard(ws, cardId),
            returnToGame: () => { if (isMock) mockActions.returnToGame(); else returnToGame(ws); },
            fireCard: (cardId) => isMock ? mockActions.fireCard(cardId) : fireCard(ws, cardId),
            putCardInPlay: (cardId) => isMock ? mockActions.putCardInPlay(cardId) : putCardInPlay(ws, cardId),
            putCardInPlayOnPlayer: (cardId, playerId) => isMock ? mockActions.putCardInPlayOnPlayer(cardId, playerId) : putCardInPlayOnPlayer(ws, cardId, playerId),
            clickUIButton: (buttonId) => isMock ? mockActions.clickUIButton(buttonId) : clickUIButton(ws, buttonId),
            startNewGameAndDeleteThisOne: () => isMock ? mockActions.startNewGameAndDeleteThisOne() : startNewGameAndDeleteThisOne(ws, openDialog, gameState),
        }}>
            {children}
        </GameContext.Provider>
    );
}