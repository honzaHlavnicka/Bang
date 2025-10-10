import toast from "react-hot-toast";
import type { GameStateType } from "./GameContext";
import { type DialogState } from "./DialogContext";
import type { RefObject } from "react";


// Helper types for server payloads
type ServerPlayer = {
    jmeno: string;
    zivoty: number;
    maximumZivotu: number;
    role: string;
    id: number;
    pocetKaret?: number;
    postava?: string;
};

type ServerCard = { obrazek: string; id: number };

type Player = NonNullable<GameStateType["players"]>[number];

export function handleGameMessage(
    event: MessageEvent,
    setGameState: (updater: (prev: GameStateType) => GameStateType) => void,
    stateRef: RefObject<GameStateType>,
    openDialog: (dialog: DialogState) => void,
    socket:WebSocket | null
) {
    console.log("%c" + event.data, "color: green");

    if(event.data.startsWith("error")) {
        //TODO: UPRAVIT aby server posílal i dvotečku.
        event.data.replace("error:",":");
    }//TODO: co to je za blbost?

    let type = "";
    let payload = "";
    if (event.data.includes(":")) {
        const i = event.data.indexOf(":");
        type = event.data.slice(0, i);
        payload = event.data.slice(i + 1);
    } else {
        type = event.data;
    }

    switch (type) {
        case "popoup": {
            alert(payload);
            break;
        }
        case "error": {
            console.error("Chyba ze serveru: " + payload);
            try {
                const json = JSON.parse(payload) as { error: string };
                toast.error(json.error);
            } catch (err) {
                toast.error("Chyba ze serveru: " + payload);
            }
            break;
        }
        case "novaHra": {
            setGameState(prev => ({ ...prev, inGame: true, gameCode: payload }));
            break;
        }
        case "pripojenKeHre": {
            setGameState(prev => ({ ...prev, inGame: true }));
            break;
        }
        case "vyberPostavu": {
            try {
                const json = JSON.parse(payload) as GameStateType["characters"];
                setGameState(prev => ({ ...prev, characters: json }));
                console.log(json);
            } catch (error) {
                console.error("chyba při parsování", error, payload);
            }
            break;
        }
        case "hraci": {
            try {
                const json = JSON.parse(payload) as ServerPlayer[];
                const mappedPlayers: Player[] = json.map((player) => ({
                    id: player.id,
                    name: player.jmeno,
                    role: player.role,
                    health: player.zivoty,
                    cardsInHand: player.pocetKaret ?? 0,
                    character: player.postava ?? "",
                    isCurrentTurn: false,
                    inPlayCards: [],
                }));
                setGameState(prev => ({ ...prev, players: mappedPlayers }));
                console.log(json);
            } catch (error) {
                console.error("chyba při parsování", error, payload);
            }
            break;
        }
        case "novyHrac": {
            try {
                const json = JSON.parse(payload) as ServerPlayer;
                const newPlayer: Player = {
                    id: json.id,
                    name: json.jmeno,
                    role: json.role,
                    health: json.zivoty,
                    cardsInHand: json.pocetKaret ?? 0,
                    character: json.postava ?? "",
                    isCurrentTurn: false,
                    inPlayCards: [],
                };
                setGameState(prev => ({
                    ...prev,
                    players: prev.players ? [...prev.players, newPlayer] : [newPlayer],
                }));
                console.log("nový hráč", json);
                toast.success(`Připojil se hráč ${json.jmeno}!`);
            } catch (error) {
                console.error("chyba při parsování", error, payload);
            }
            break;
        }
        case "hraSpustena": {
            setGameState(prev => ({ ...prev, gameStarted: true }));
            break;
        }
        case "noveJmeno": {
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newName = parts[1] ?? "";
            console.log("zpracovávám noveJmeno", payload, playerId, newName);

            setGameState(prev => {
                const isMe = String(prev.playerId ?? "") === playerId;
                const base = isMe ? { ...prev, name: newName } : { ...prev };
                return {
                    ...base,
                    players: base.players
                        ? base.players.map((p) => (String(p.id) === playerId ? { ...p, name: newName } : p))
                        : base.players,
                };
            });
            break;
        }
        case "setPostava": {
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const character = parts[1] ?? "";
            updatePlayerProperty(setGameState, playerId, "character", character);
            break;
        }
        case "noveIdHrace": {
            setGameState(prev => ({ ...prev, playerId: parseInt(payload) }));
            break;
        }
        case "novaKarta": {
            try {
                const json = JSON.parse(payload) as ServerCard;
                const card = { image: json.obrazek, id: json.id };
                setGameState(prev => ({
                    ...prev,
                    handCards: prev.handCards ? [...prev.handCards, card] : [card]
                }));
            } catch (error) {
                console.error("chyba při parsování", error, payload);
            }
            break;
        }
        case "role": {
            setGameState(prev => ({ ...prev, role: payload }));
            break;
        }
        case "zmenaPoctuKaret": {
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newCardCount = parseInt(parts[1] ?? "0");
            updatePlayerProperty(setGameState, playerId, "cardsInHand", newCardCount);
            break;
        }
        case "hraZacala": {
            setGameState(prev => ({ ...prev, gameStarted: true }));
            toast.success('Hra zahájena!')
            break;
        }
        case "novyPocetKaret": {
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newCardCount = parseInt(parts[1] ?? "0");
            updatePlayerProperty(setGameState, playerId, "cardsInHand", newCardCount);
            break;
        }
        case "odehrat": {
            const parts = payload.split("|");
            const playerId = parts[0] ?? "";
            try {
                const json = JSON.parse(parts[1] ?? "0") as ServerCard;
                const card = { image: json.obrazek, id: json.id };
                console.log("zpracovávám odehrat", json, card);
                console.log("pleyerId", playerId, "currentPlayerId", stateRef.current?.playerId, stateRef.current);

                setGameState(prev => {
                    const isCurrent = String(prev.playerId ?? "") === String(playerId);
                    const nextDiscard = [...prev.discardPile, card.image];
                    if (!isCurrent) {
                        return { ...prev, discardPile: nextDiscard };
                    }
                    return {
                        ...prev,
                        handCards: (prev.handCards ?? []).filter((c) => c.id !== card.id),
                        inPlayCards: [...(prev.inPlayCards ?? []), card],
                        discardPile: nextDiscard,
                    };
                });
            } catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')

            }
            break;
        }
        case "tvujTahZacal": {
            setGameState(prev => ({ ...prev, turnPlayerId: prev.playerId ?? null }));
            toast.success('Tvůj tah začal!')
            break;
        }
        case "tahZacal": {
            const playerId = payload;
            setGameState(prev => ({
                ...prev,
                turnPlayerId: parseInt(playerId),
                players: prev.players
                    ? prev.players.map((p) => ({
                        ...p,
                        isCurrentTurn: String(p.id) === String(playerId),
                    }))
                    : prev.players,
            }));
            break;
        }
        case "Echo": {
            break;
        }
        case "token": {
            localStorage.setItem("gameToken", payload);
            console.log("uložen token", payload);
            break;
        }
        case "vyberAkci": {
            //TODO: neni dodelane a otestovane
            try {
                const json = JSON.parse(payload) as {id:number,akce:{ id: number; nazev: string }[]}; 
                const actions = json.akce.map(a=>({id:a.id,name:a.nazev}));
                openDialog({type:"CONFIRM_ACTION", data:{actions},dialogHeader:"Vyber akci kterou chceš provést.",notCloasable:false,callback:(selectedAction:number)=>{
                    console.log("vybraná akce",selectedAction);
                    if(stateRef.current?.playerId == null){
                        toast.error("Nelze provést akci, protože není znám tvůj hráčský ID");
                        return;
                    }
                    if(socket == null){
                        toast.error("Nelze provést akci, protože není navázáno spojení se serverem");
                        return;
                    }
                    socket.send(`dialog:${json.id},${selectedAction}`);
                    
                }});
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "vyberHrace": {
            try {
                const json = JSON.parse(payload) as {id:number,hraci:number[], nadpis?:string}; 
                const heading = json.nadpis ?? "Vyber hráče";
                const players = (stateRef.current?.players ?? []).filter(p=>json.hraci.includes(p.id)).map(p=>({id:p.id,name:p.name}));
                openDialog({type:"SELECT_PLAYER", data:{players},dialogHeader:heading,notCloasable:false,callback:(selectedAction:number)=>{
                    console.log("vybraný hráč:",selectedAction);
                    if(stateRef.current?.playerId == null){
                        toast.error("Nelze provést akci, protože není znám tvůj hráčský ID");
                        return;
                    }
                    if(socket == null){
                        toast.error("Nelze provést akci, protože není navázáno spojení se serverem");
                        return;
                    }
                    socket.send(`dialog:${json.id},${selectedAction}`);
                    
                }});
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "vyhral":{
            const winnerId = payload;
            const state = stateRef.current;
            if(state == null){
                toast.error("Někdo vyhrál, nevím kdo.");
                return;
            }
            const winner = state.players?.find((p)=>String(p.id) === String(winnerId));
            if(winner == null){
                toast.error("Někdo vyhrál, nevím kdo.");
                return;
            }
            openDialog({type:"INFO", data:{header:"Konec hry",message:`Hru vyhrál hráč ${winner.name}. Gratuluji!`},dialogHeader:"Konec hry",notCloasable:false});
            //TODO: vylepšit dialog
            break;
        }
        default: {
            console.log("=> klient nezná");
            break;
        }
    }
}

export function updatePlayerProperty(
    setGameState: (updater: (prev: GameStateType) => GameStateType) => void,
    playerId: string,
    property: keyof Player,
    value: Player[keyof Player]
) {
    setGameState((prevState) => ({
        ...prevState,
        players: prevState.players
            ? prevState.players.map((player) =>
                String(player.id) == String(playerId)
                    ? { ...player, [property]: value } as Player
                    : player
            )
            : prevState.players
    }));
}

export function setGameValue(ws: WebSocket | null, data: unknown, type: string) {
    if (ws !== null) {
        if (type === "DRAW_CARD") {
            ws.send("linuti");
        } else if (type === "CONNECT") {
            const d = data as { kod: string; jmeno: string };
            ws.send("pripojeni:" + d.kod);
            ws.send("noveJmeno:" + d.jmeno);
        }
    }
}

export function connectToGame(
    ws: WebSocket | null,
    setGameState: (updater: (prev: GameStateType) => GameStateType) => void,
    gameCode: string,
    name: string
) {
    if (ws !== null) {
        setGameState(prevState => ({ ...prevState, gameCode: gameCode, inGame: true }));
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

export function chooseCharacter(
    ws: WebSocket | null,
    changeGameState: (updater: (prev: GameStateType) => GameStateType) => void,
    character: string
) {
    changeGameState((prev) => ({ ...prev, character: character, characters: [] }));
    if (ws !== null) {
        ws.send("setPostava:" + character);
    }
}
export function startGame(ws: WebSocket | null) {
    if (ws !== null) {
        ws.send("zahajeniHry");
    }
}
export function playCard(ws: WebSocket | null, cardId: number) {
    if (ws !== null) {
        ws.send("odehrani:" + cardId);
    }
}

export function drawCard(ws: WebSocket | null) {
    if (ws !== null) {
        ws.send("linuti");
    }
}

export function returnToGame(ws: WebSocket | null) {
    console.log("pokouším se vrátit do hry");
    if (ws !== null) {
        const token = localStorage.getItem("gameToken");
        if (!token) {
            console.error("Nelze se vrátit do hry, protože není uložen token");
            toast.error("Nelze se vrátit do hry, protože není uložen token");
            return;
        }
        
        console.log("posílám vraceniSe s tokenem", token);
        ws.send("vraceniSe:" + token);
    }
}
