import { createContext, useContext } from "react";

export type GameStateType = {
    players: Array<{ id: string; name: string; role: string; health: number }>;
    currentPlayerId: string;
    turnOrder: string[];
    deckCount: number;
    discardPile: Array<string>;
};

export const GameContext = createContext<{
    gameState: GameStateType;
    sendMessage: (msg: any) => void;
} | null>(null);

export const useGame = () => {
    const ctx = useContext(GameContext);
    if (!ctx) throw new Error("useGame musí být použito uvnitř GameProvider");
    return ctx;
};