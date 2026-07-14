import toast from "react-hot-toast";
import type { GameStateType } from "./GameContext";
import { gameStateDefault } from "./GameContext";

// --- MOCK DATA PRO VÝVOJ BEZ SERVERU ---
export const mockCardsPool = [
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

export const getInitialMockGameState = (name: string): GameStateType => {
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

export const getMockActions = (
    setGameState: React.Dispatch<React.SetStateAction<GameStateType>>
) => {
    const actions = {
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
            actions.connectToGame("MOCK123", name);
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
            setGameState(prev => {
                const initial = getInitialMockGameState(prev.name || "Honza");
                return initial;
            });
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
            actions.playCard(cardId);
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
                ...gameStateDefault,
                allowedUIElements: ["ZIVOTY","UKONCENI_TAHU","POSTAVA","ROLE","VYLOZENE_KARTY","ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"],
                customUIButtons: []
            });
            toast.success("Mock: Hra smazána");
        }
    };

    return actions;
};
