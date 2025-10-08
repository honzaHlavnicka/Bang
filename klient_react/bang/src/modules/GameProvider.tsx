import React, { useEffect, useState, useRef } from "react";
import { GameContext } from "./GameContext";
import type { GameStateType } from "./GameContext";
import { handleGameMessage, setGameValue, connectToGame, changePlayerName,chooseCharacter, createGame, startGame, playCard, drawCard, returnToGame } from "./gameActions";
import toast from "react-hot-toast";
import { useDialog } from "./DialogContext";

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
    const {openDialog} = useDialog();

    // drž aktuální stav ve ref, aby ho onmessage vždy četl aktuální
    const stateRef = useRef<GameStateType>(gameState);
    useEffect(() => {
        stateRef.current = gameState;
    }, [gameState]);

    useEffect(() => {
        //TODO: odstranit testovací dialog
        //openDialog({type:"SELECT_CARD", data:{cards:[{image:"/img/karty/bang.png",id:70},{image:"/img/karty/dostavnik.png",id:71},{image:"img/karty/uno/red4.png",id:1}],min:1,max:2},dialogHeader:"Vyber si jednu kartu co se ti zlíbí!",notCloasable:true});
        //openDialog({type:"SELECT_PLAYER", data:{players:[{id:1,name:"Honza"},{id:2,name:"Pepa"},{id:3,name:"Karel"}]},dialogHeader:"Vyber si hráče!",notCloasable:true,callback:(selectedPlayer:number)=>{alert(selectedPlayer)}});
        //openDialog({type:"CONFIRM_ACTION", data:{actions:[{id:1,name:"Akce 1"},{id:2,name:"Akce 2"},{id:3,name:"Akce 3"},{id:4,name:"asdsd"},{id:5,name:"fkolod"},{id:6,name:"asddfsgsd"},{id:7,name:"dfb"}]},dialogHeader:"Co chceš udělat???!",notCloasable:false,callback:(selectedAction:number)=>{alert(selectedAction)}});
        //openDialog({type:"INFO", data:{header:"Něco se stalo",message:"Toto je informační hláška pro hráče."},dialogHeader:"Info",notCloasable:false});

        const socketAdress =  "ws://localhost:9999";
        const socket = new WebSocket("wss://alexzander-contemnible-tolerantly.ngrok-free.dev/ws");
        console.log("pokus o připojení k ws serveru na adrese " + socketAdress);
        socket.onopen = () => {
            setWs(socket);
            toast.success("Připojeno k serveru");
            (window as unknown as { ws: WebSocket }).ws = socket; //TODO: odstranit testovací přiřazení
        };
        socket.onmessage = (event) => { handleGameMessage(event, setGameState, stateRef,openDialog,socket); };
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
            setGameValue: (data, type) => setGameValue(ws, data, type),
            connectToGame: (gameCode, name) => connectToGame(ws, setGameState, gameCode, name),
            changePlayerName: (newName) => changePlayerName(ws, newName),
            chooseCharacter: (characterName) => chooseCharacter(ws, setGameState, characterName),
            startGame: () => { startGame(ws) },
            endTurn: () => { /* implementace nebo prázdná funkce */ },
            createGame: (name) => createGame(ws, name),
            drawCard: () => drawCard(ws),
            playCard: (cardId) => playCard(ws, cardId),
            returnToGame: () => { returnToGame(ws) },
        }}>
            {children}
        </GameContext.Provider>
    );
}