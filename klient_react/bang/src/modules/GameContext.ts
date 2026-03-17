import { createContext, useContext } from "react";


export type GameValueType = "PLAYER_ACTION" | "DRAW_CARD" | "CONNECT" | "CHANGE_NAME" | "START_GAME" | "END_TURN";

export type CardType = {
    image:string;
    id:number;
}

export type CustomUIButton = {
    id: number;
    text: string;
    disabled: boolean;
}

export type GameStateType = {
    startedConection:boolean;
    inGame:boolean;
    gameStarted:boolean;
    gameEnded:boolean;
    gameTypesAvailable?:Array<{id:number,name:string,description:string,url:string}>

    gameCode: string | null;

    name?:string;
    role?:string;
    health?:number;
    maxHealth?:number;
    character?:string;
    handCards: Array<CardType>;
    inPlayCards: Array<CardType>;
    playerId: number| null;
    isAdmin?: boolean;



    turnPlayerId?: number | null;
    characters?: Array<{jmeno:string,obrazek:string,popis:string}> | null;
    players: Array<{ id: number; name: string; role: string; health: number;cardsInHand:number; character: string; isCurrentTurn: boolean; inPlayCards: Array<CardType> | null; isAdmin?: boolean;}> | null;
    turnOrder: string[];
    deckCount: number;
    discardPile: Array<string>;

    allowedUIElements: string[];
    customUIButtons: Array<CustomUIButton>;

    gameStateMessege?:string;
    gameStateMessegeFull?:string;

    winningPlaces?:number[][]; // [[<id 1. místa>], [<id 2. místa>,<2. id 2. místa>], ...]]

};
export const gameStateDefault: GameStateType = {
    startedConection:false,
    gameStarted: false,
    inGame: false,
    gameEnded: false,

    gameCode: "",
    handCards:[],
    inPlayCards:[],
    players: [],
    playerId: null,
    turnOrder: [],
    deckCount: 0,
    discardPile: [],

    allowedUIElements: ["ZIVOTY","UKONCENI_TAHU","POSTAVA","ROLE","VYLOZENE_KARTY","ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"],
    customUIButtons: [],
};

export const GameContext = createContext<{
    gameState: GameStateType;
    connectToGame: (gameCode: string, name: string) => void;
    changePlayerName: (newName: string) => void;
    startGame: () => void;
    endTurn: () => void;
    chooseCharacter: (characterName: string) => void;
    createGame: (gameTypeId:number,name: string) => void;
    drawCard: () => void;
    playCard: (cardId:number) => void;
    returnToGame (): void;
    fireCard: (cardId:number) => void;
    putCardInPlay: (cardId:number) => void;
    clickUIButton: (buttonId:number) => void;

} | null>(null);


export const useGame = () => {
    const ctx = useContext(GameContext);
    if (!ctx) throw new Error("useGame musí být použito uvnitř GameProvider");
    return ctx;
};