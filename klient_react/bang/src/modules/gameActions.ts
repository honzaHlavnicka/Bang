import type { GameStateType } from "./GameContext";

export function handleGameMessage(event: MessageEvent,setGameState: (state: any) => void, gameState: GameStateType) {
    console.log("%c" + event.data, "color: green");

    let type = "";
    let payload = "";
    if (event.data.includes(":")) {
        const i = event.data.indexOf(':');
        type = event.data.slice(0,i);
        payload = event.data.slice(i+1);  
    } else {
        type = event.data;
    }

    switch (type) {
        case "popoup":
            //server si nadiktoval popup
            alert(payload)
            break;
        case "error":
            console.error("Chyba ze serveru: " + payload);
            break;
        case "novaHra":
            setGameState((prevState: any) => ({ ...prevState, inGame: true, gameCode: payload }));
            break;
        case "pripojenKeHre":
            setGameState((prevState: any) => ({ ...prevState, inGame: true }));
            break;
        case "vyberPostavu":
            try {
                const json = JSON.parse(payload);
                setGameState((prevState: any) => ({ ...prevState, characters:json  }));
                console.log(json);
            } catch (error) {
                console.error("chyba při parsování",error,payload)
            }
            break;
        case "hraci":
            try {
                const json = JSON.parse(payload);
                const mappedPlayers = json.map((player: any) => ({
                    name: player.jmeno,
                    lives: player.zivoty,
                    maxLives: player.maximumZivotu,
                    role: player.role,
                    id: player.id,
                }));
                setGameState((prevState: any) => ({ ...prevState, players: mappedPlayers }));
                console.log(json);
            } catch (error) {
                console.error("chyba při parsování",error,payload)
            }
            break;
        case "novyHrac":
            try {
                const json = JSON.parse(payload);
                const newPlayer = {
                    name: json.jmeno,
                    lives: json.zivoty,
                    maxLives: json.maximumZivotu,
                    id: json.id,
                    cardsIndHand: json.pocetKaret,
                    character: json.postava,
                    isCurrentTurn: false,
                    inPlayCards: [],
                };
                setGameState((prevState: any) => ({
                    ...prevState,
                    players: prevState.players ? [...prevState.players, newPlayer] : [newPlayer],
                }));
                console.log("nový hráč",json);
            } catch (error) {
                console.error("chyba při parsování",error,payload)
            }
            break;
        case "hraSpustena":
            setGameState((prevState: any) => ({ ...prevState, gameStarted:true }));
            break;

        case "noveJmeno":{
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newName = parts[1] ?? "";
            
            console.log("zpracovávám noveJmeno", payload, playerId, newName);

            if(playerId === gameState.currentPlayerId?.toString()){
                setGameState((prev:GameStateType) => ({...prev, name:newName}))
            }
            updatePlayerProperty(setGameState, playerId, "name", newName);
            break;
        }
        case "setPostava":{
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const character = parts[1] ?? "";
            updatePlayerProperty(setGameState, playerId, "character", character);

            break;
        }  
        case "noveIdHrace":
            alert("server ti přidělil id " + payload);
            setGameState((prev:GameStateType) => ({...prev, currentPlayerId:parseInt(payload)}))
            break;
        case "novaKarta":{
            try {
                const json = JSON.parse(payload);
                const card = {image:json.obrazek,id:json.id};
                setGameState((prev:GameStateType) => ({...prev, handCards: prev.handCards ? [...prev.handCards, card] : [card]}))
            } catch (error) {
                console.error("chyba při parsování",error,payload)
            }
            break;
        }
        case "role":
            setGameState((prev:GameStateType) => ({...prev, role:payload}))
            break;
        case "zmenaPoctuKaret":{
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newCardCount = parseInt(parts[1] ?? "0");
            updatePlayerProperty(setGameState, playerId, "cardsInHand", newCardCount);
            break;
        }
        case "hraZacala":
            setGameState((prev:GameStateType) => ({...prev, gameStarted:true}))
            break;
        case "novyPocetKaret":{
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newCardCount = parseInt(parts[1] ?? "0");
            updatePlayerProperty(setGameState, playerId, "cardsInHand", newCardCount);
            break;
        }
        case "odehrat":{
            const parts = payload.split("|");
            const playerId = parts[0] ?? "";
            try {
                const json = JSON.parse(parts[1] ?? "0");
                const card = {image:json.obrazek,id:json.id};
                alert("server říká, že hráč " + playerId + " odehrál kartu " + card.image)
                console.log("zpracovávám odehrat",json,card)
                console.log("pleyerId",playerId,"currentPlayerId",gameState.currentPlayerId,gameState)
                if(playerId == gameState.currentPlayerId?.toString()){
                    setGameState((prev:GameStateType) => ({...prev, handCards: prev.handCards ? prev.handCards.filter(c => c.id !== card.id) : [] , inPlayCards: prev.inPlayCards ? [...prev.inPlayCards, card] : [card]}))
                    alert("odehrál jsi kartu " + card.image)
                }
                
                setGameState((prev:GameStateType) => ({ ...prev, discardPile:card.image} ));
            } catch (error) {
                console.error("chyba při parsování",error,payload)
            }
            break;
        }
            
        case "Echo":
            break;
        default:
            console.log("=> klient nezná")
            break;
    }
    

}

export function updatePlayerProperty(
    setGameState: (state: any) => void,
    playerId: string,
    property: string,
    value: any
) {
    setGameState((prevState: any) => ({
        ...prevState,
        players: prevState.players
            ? prevState.players.map((player: any) =>
                player.id == playerId
                    ? { ...player, [property]: value }
                    : player
            )
            : prevState.players
    }));
}


export function setGameValue(ws: WebSocket | null, data: any, type: string) {
    if (ws !== null) {
        if(type === "DRAW_CARD") {
            ws.send("linuti");
        } else if(type === "CONNECT") {
            ws.send("pripojeni:" + data.kod);
            ws.send("noveJmeno:" + data.jmeno);
        }
    }
}

export function connectToGame(ws: WebSocket | null, setGameState: (state: any) => void, gameCode: string, name: string) {
    if (ws !== null) {
        setGameState((prevState: any) => ({ ...prevState, gameCode: gameCode, inGame:true }));
        ws.send("pripojeniKeHre:" + gameCode);
        ws.send("noveJmeno:" + name);
    }
}

export function createGame(ws: WebSocket | null, name: string) {
    if (ws !== null) {
        ws.send("novaHra");
        ws.send("noveJmeno:" + name);
    }
}

export function changePlayerName(ws: WebSocket | null, newName: string) {
    if (ws !== null) {
        ws.send("noveJmeno:" + newName);
    }
}

export function chooseCharacter(ws: WebSocket | null, changeGameState: (state: any) => void, character: string) {
    changeGameState((prev) => ({ ...prev, character:character, characters:[] }));
    if (ws !== null) {
        ws.send("setPostava:" + character);
    }
}
export function startGame(ws: WebSocket | null, setGameState: (state: any) => void) {
    if (ws !== null) {
        ws.send("zahajeniHry");
    }
    //setGameState((prevState:GameStateType) => ({ ...prevState,gameStarted: true}));
}
export function playCard(ws: WebSocket | null, setGameState: (state: any) => void, cardId: number) {
    if (ws !== null) {
        ws.send("odehrani:" + cardId);
    }
}
