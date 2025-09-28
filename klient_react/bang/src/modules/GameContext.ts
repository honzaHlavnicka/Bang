import { createContext, useContext } from "react";


export type GameValueType = "PLAYER_ACTION" | "DRAW_CARD" | "CONNECT" | "CHANGE_NAME" | "START_GAME" | "END_TURN";


export type GameStateType = {
    inGame:boolean;
    gameStarted:boolean;

    gameCode: string;


    character?:string;
    characters?: Array<{jmeno:string,obrazek:string,popis:string}> | null;
    players: Array<{ id: string; name: string; role: string; health: number }>;
    currentPlayerId: string;
    turnOrder: string[];
    deckCount: number;
    discardPile: Array<string>;

};

export const GameContext = createContext<{
    gameState: GameStateType;
    setGameValue: (data:any,type:GameValueType) => void;
    connectToGame: (gameCode: string, name: string) => void;
    changePlayerName: (newName: string) => void;
    startGame: () => void;
    endTurn: () => void;
    chooseCharacter: (characterName: string) => void;
} | null>(null);

export const useGame = () => {
    const ctx = useContext(GameContext);
    if (!ctx) throw new Error("useGame musí být použito uvnitř GameProvider");
    return ctx;
};