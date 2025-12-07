import { createContext, useContext } from "react";


export type GameValueType = "PLAYER_ACTION" | "DRAW_CARD" | "CONNECT" | "CHANGE_NAME" | "START_GAME" | "END_TURN";

export type CardType = {
    image:string;
    id:number;
}

export type GameStateType = {
    startedConection:boolean;
    inGame:boolean;
    gameStarted:boolean;
    gameTypesAvailable?:Array<{id:number,name:string,description:string}>

    gameCode: string | null;

    name?:string;
    role?:string;
    health?:number;
    maxHealth?:number;
    character?:string;
    handCards: Array<CardType>;
    inPlayCards: Array<CardType>;
    playerId: number| null;



    turnPlayerId?: number | null;
    characters?: Array<{jmeno:string,obrazek:string,popis:string}> | null;
    players: Array<{ id: number; name: string; role: string; health: number;cardsInHand:number; character: string; isCurrentTurn: boolean; inPlayCards: Array<CardType> | null;}> | null;
    turnOrder: string[];
    deckCount: number;
    discardPile: Array<string>;

    allowedUIElements: string[];

};
export const gameStateDefault: GameStateType = {
    startedConection:false,
    gameStarted: false,
    inGame: false,

    gameCode: "",
    handCards:[],
    inPlayCards:[],
    players: [],
    playerId: null,
    turnOrder: [],
    deckCount: 0,
    discardPile: [],

    allowedUIElements: ["ZIVOTY","UKONCENI_TAHU","POSTAVA","ROLE","VYLOZENE_KARTY","ODHAZOVACI_BALICEK","DOBIRACI_BALICEK"],
};

export const GameContext = createContext<{
    gameState: GameStateType;
    setGameValue: (data:any,type:GameValueType) => void;
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


} | null>(null);


export const useGame = () => {
    const ctx = useContext(GameContext);
    if (!ctx) throw new Error("useGame musí být použito uvnitř GameProvider");
    return ctx;
};