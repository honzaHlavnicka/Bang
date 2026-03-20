import toast from "react-hot-toast";
import type { CardType, GameStateType } from "./GameContext";
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
    isAdmin?: boolean;
};

type ServerCard = { obrazek: string; id: number };

type Player = NonNullable<GameStateType["players"]>[number];

export function handleGameMessage(
    event: MessageEvent,
    setGameState: (updater: (prev: GameStateType) => GameStateType) => void,
    stateRef: RefObject<GameStateType>,
    openDialog: (dialog: DialogState) => void,
    socket:WebSocket | null,
    notify: (text: string) => void
) {
    console.log("%c" + event.data, "color: green");

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
                const json = JSON.parse(payload) as { error: string, kod?: number };
                toast.error(json.error);

                if(json.kod === 5 || json.error.includes("Hra neexistuje")){
                    //Hra neexistuje
                    setGameState(prev => ({ ...prev, inGame: false, gameCode: null }) );
                }

            } catch {
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
                    isAdmin: player.isAdmin ?? false,
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
                    isAdmin: json.isAdmin ?? false,
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
            const myId = parseInt(payload);
            setGameState(prev => {
                // Najdi si svoje info v seznamu hráčů a zjisti isAdmin
                const myInfo = prev.players?.find(p => p.id === myId);
                
                return { 
                    ...prev, 
                    playerId: myId,
                    isAdmin: myInfo?.isAdmin ?? false
                };
            });
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
        case "odehrat": 
            zbaveniSeKarty("discard", payload,stateRef,setGameState);
           break;
        case "spalit":
            zbaveniSeKarty("fire", payload,stateRef,setGameState);
            break;        
        case "spalenaVylozena":
            //payload: cardId,playerId
            setGameState(prev=>{
                const parts = payload.split(",");
                const cardId = parseInt(parts[0] ?? "");
                const playerId = parts[1] ?? "";
                const isMe = String(prev.playerId ?? "") === playerId;
                const nextDiscard = [...prev.discardPile, prev.inPlayCards?.find(c=>c.id === cardId)?.image ?? ""];

                if(isMe){
                    return {
                        ...prev,
                        inPlayCards: prev.inPlayCards?.filter(c=>c.id !== cardId) ?? [],
                        discardPile: nextDiscard
                    }
                }else{
                    const updatedPlayers = prev.players
                        ? prev.players.map(p => {
                            if (String(p.id) === playerId) {
                                return { ...p, inPlayCards: (p.inPlayCards ?? []).filter(c => c.id !== cardId) };
                            }
                            return p;
                        })
                        : prev.players;

                    return {
                        ...prev,
                        players: updatedPlayers,
                        discardPile: nextDiscard
                    }
                }
            });

             break;
        case "stavHry": {
            // Payload obsahuje text, který se má zobrazit v centru obrazovky
            setGameState(prev => ({ ...prev, gameStateMessege: payload }));
            break;
        }
        case "tvujTahZacal": {
            setGameState(prev => ({ ...prev, turnPlayerId: prev.playerId ?? null }));
            toast.success('Tvůj tah začal!')
            notify('Tvůj tah začal!');
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
            try {
                const json = JSON.parse(payload) as {id:number,akce:{ id: number; nazev: string }[], notClosable?: boolean}; 
                const actions = json.akce.map(a=>({id:a.id,name:a.nazev}));
                const notClosable = json.notClosable ?? true;
                openDialog({type:"CONFIRM_ACTION", data:{actions},dialogHeader:"Vyber akci kterou chceš provést.",notClosable,callback:(selectedAction:number)=>{
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
                const json = JSON.parse(payload) as {id:number,hraci:number[], nadpis?:string, min?:number, max?:number, notClosable?: boolean}; 
                const heading = json.nadpis ?? "Vyber hráče";
                const min = json.min ?? 1;
                const max = json.max ?? 1;
                const notClosable = json.notClosable ?? true;
                const players = (stateRef.current?.players ?? []).filter(p=>json.hraci.includes(p.id)).map(p=>({id:p.id,name:p.name}));
                openDialog({type:"SELECT_PLAYER", data:{players,min,max},dialogHeader:heading,notClosable,callback:(selectedPlayers:number[])=>{
                    console.log("vybraní hráči:",selectedPlayers);
                    if(stateRef.current?.playerId == null){
                        toast.error("Nelze provést akci, protože není znám tvůj hráčský ID");
                        return;
                    }
                    if(socket == null){
                        toast.error("Nelze provést akci, protože není navázáno spojení se serverem");
                        return;
                    }
                    socket.send(`dialog:${json.id},${selectedPlayers.join(",")}`);
                    
                }});
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "vyberKartu": {
            try {
                const json = JSON.parse(payload) as {id:(string | number),karty:{obrazek:string,id:number,jmeno:string}[], nadpis?:string, min?:number, max?:number, notClosable?: boolean}; 
                const heading = json.nadpis ?? "Vyber kartu";
                const min = json.min ?? 1;
                const max = json.max ?? 1;
                const notClosable = json.notClosable ?? true;
                const cards = json.karty.map(k=>({image:k.obrazek,id:k.id}));
                openDialog({type:"SELECT_CARD", data:{cards,min,max},dialogHeader:heading,notClosable,callback:(selectedCards:number[])=>{ 
                    console.log("vybrané karty:",selectedCards);
                    if(stateRef.current?.playerId == null){
                        toast.error("Nelze provést akci, protože není znám tvůj hráčský ID");
                        return;
                    }
                    if(socket == null){
                        toast.error("Nelze provést akci, protože není navázáno spojení se serverem");
                        return;
                    }
                    socket.send(`dialog:${json.id},${selectedCards.join(",")}`);
                }});
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "vyhral":{  // Jeden ze 2 způsobů jak to oznámit
            const winnerId = payload;
            const state = stateRef.current;
            if(state == null){
                toast.error("Někdo vyhrál, nevím kdo.");
                return;
            }
            const winner = state.players?.find((p)=>String(p.id) === String(winnerId));
            if(winner == null){
                toast.error("Někdo vyhrál, nevím kho.");
                return;
            }
            openDialog({type:"INFO", data:{header:"Konec hry",message:`Hru vyhrál hráč ${winner.name}. Gratuluji!`},dialogHeader:"Konec hry",notClosable:false});
            break;
        }
        case "welcome": {
            //toto by měl přijít jako první po připojení
            if(socket != null){
                stateRef.current.startedConection = true;
                socket.send("infoHer");
            }
            break;
        }
        case "infoHer": {
            try {
                const json = JSON.parse(payload) as {verze:string,hry:{jmeno:string,id:number,popis:string,url:string}[]};
                setGameState(prev=>({...prev, gameTypesAvailable: json.hry.map(h=>({id:h.id,name:h.jmeno,description:h.popis,url:h.url}))}));
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "pocetZivotu": {
            const parts = payload.split(",");
            const playerId = parts[0] ?? "";
            const newHealth = parseInt(parts[1] ?? "0");
            if(parseInt(playerId) === stateRef.current?.playerId){
                setGameState(prev=>({...prev, health:newHealth}));
                toast(`Máš nyní ${newHealth} životů`,{icon:"❤️"});
            }else{
                updatePlayerProperty(setGameState, playerId, "health", newHealth);
            }
            break;
        }
        case "vylozeni":{
            //payload: "predKoho,kym,{json}"
            // 1,1,{"jmeno":"barel","obrazek":"barel","id":38}
            const firstComma = payload.indexOf(",");
            const secondComma = payload.indexOf(",", firstComma + 1);

            if (firstComma === -1 || secondComma === -1) {
                console.error("vylozeni: neplatný formát payloadu", payload);
                toast.error("Chybná odpověď serveru");
                break;
            }

            const predKoho = Number(payload.slice(0, firstComma));
            const kym = Number(payload.slice(firstComma + 1, secondComma));
            const jsonStr = payload.slice(secondComma + 1).trim();

            let co: { id: number; obrazek: string };
            try {
                co = JSON.parse(jsonStr);
            } catch (e) {
                console.error("vylozeni: nelze parsovat JSON části", jsonStr, e);
                toast.error("Chybná odpověď serveru");
                break;
            }

            console.log("zpracovávám vylozeni", payload, { predKoho, kym, co });
            const card: CardType = { id: co.id, image: co.obrazek };

            setGameState((prev) => {
                const statePlayerId = prev.playerId ?? null;
                if (statePlayerId !== null && predKoho === statePlayerId) {
                    return {
                        ...prev,
                        inPlayCards: [...prev.inPlayCards, card],
                        handCards: (prev.handCards ?? []).filter((c) => c.id !== card.id),
                    };
                }

                const updatedPlayers = (prev.players ?? []).map((p) =>
                    p.id === predKoho
                        ? { ...p, inPlayCards: [...(p.inPlayCards ?? []), card] }
                        : p
                );

                return { ...prev, players: updatedPlayers };
            });

            break;
        }
        case "povoleneUI":{
            try {
                const json = JSON.parse(payload) as string[];
                setGameState(prev=>({...prev, allowedUIElements: json}));
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "rychleOznameni":{
            notify(payload);
            break;
        }
        case "vysledkyHry":{
            try{
                const json = JSON.parse(payload) as number[][];
                setGameState(prev=>({...prev, winningPlaces: json}));
            }catch(error){
                console.error("chyba při parsování vysledkyHry", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "konecHry":{
            setGameState(prev=>({...prev, gameEnded:true}));
            break;
        }
        case "noveUI": {
            try {
                const json = JSON.parse(payload) as {id: number, text: string, disabled: boolean};
                setGameState(prev => {
                    const existingIndex = prev.customUIButtons.findIndex(btn => btn.id === json.id);
                    if (existingIndex >= 0) {
                        // Aktualizace existujícího tlačítka
                        const updated = [...prev.customUIButtons];
                        updated[existingIndex] = { id: json.id, text: json.text, disabled: json.disabled };
                        return { ...prev, customUIButtons: updated };
                    } else {
                        // Přidání nového tlačítka
                        return { ...prev, customUIButtons: [...prev.customUIButtons, { id: json.id, text: json.text, disabled: json.disabled }] };
                    }
                });
            } catch (error) {
                console.error("chyba při parsování noveUI", error, payload);
                toast.error('Chybná odpověď serveru');
            }
            break;
        }
        case "odebratUI": {
            try {
                const uiId = parseInt(payload);
                setGameState(prev => ({
                    ...prev,
                    customUIButtons: prev.customUIButtons.filter(btn => btn.id !== uiId)
                }));
            } catch (error) {
                console.error("chyba při parsování odebratUI", error, payload);
                toast.error('Chybná odpověď serveru');
            }
            break;
        }
        case "vyberText": {
            try {
                const json = JSON.parse(payload) as {id:number, title?:string, placeholder?:string, buttonText?:string, notClosable?: boolean};
                const notClosable = json.notClosable ?? false;
                openDialog({type:"TEXT", data:{title:json.title, placeholder:json.placeholder, buttonText:json.buttonText},dialogHeader:json.title ?? "Zadej text",notClosable,callback:(text:string)=>{
                    console.log("zadaný text:",text);
                    if(stateRef.current?.playerId == null){
                        toast.error("Nelze provést akci, protože není znám tvůj hráčský ID");
                        return;
                    }
                    if(socket == null){
                        toast.error("Nelze provést akci, protože není navázáno spojení se serverem");
                        return;
                    }
                    socket.send(`dialog:${json.id},${text}`);
                }});
            }catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru')
            }
            break;
        }
        case "koloStesti": {  // Poznámka: náhoda probíhá na serveru, není třeba posílat oddpověď
            try {
                const json = JSON.parse(payload) as {moznosti:{name:string,barva:string,id:number,velikost:number}[], vybranaMoznost:number, nadpis:string};
                openDialog({type:"LUCKY_WHEEL", data:{options:json.moznosti.map(o => ({name:o.name,color:o.barva,id:o.id,size:o.velikost})), chosedOptionId:json.vybranaMoznost},dialogHeader:json.nadpis,notClosable:false});
               
            } catch (error) {
                console.error("chyba při parsování", error, payload);
                toast.error('Chybná odpověď serveru');
            }
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

export function createGame(ws: WebSocket | null,gameTypeId:number, name: string) {
    if (ws !== null) {
        ws.send("novaHra:"+gameTypeId);
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

export function endTurn(ws: WebSocket | null) {
    if (ws !== null) {
        ws.send("konecTahu");
    }
}

export function fireCard(ws: WebSocket | null, cardId: number) {
    if (ws !== null) {
        ws.send("spaleni:" + cardId);
    }
}

function zbaveniSeKarty(
    how:"fire"|"discard",
    payload: string,
    stateRef: RefObject<GameStateType>,
    setGameState: (updater: (prev: GameStateType) => GameStateType) => void
) {
    const parts = payload.split("|");
    const playerId = parts[0] ?? "";
    try {
        const json = JSON.parse(parts[1] ?? "0") as ServerCard;
        const card = { image: json.obrazek, id: json.id };
        console.log("zpracovávám "+how, json, card);
        console.log("playerId", playerId, "currentPlayerId", stateRef.current?.playerId, stateRef.current);

        // Speciální případ: playerId === "-1" znamená otočení vrchní karty (bez hráče)
        if (playerId === "-1") {
            setGameState(prev => ({
                ...prev,
                discardPile: [...prev.discardPile, card.image]
            }));
            return;
        }

        setGameState(prev => {
            const isCurrent = String(prev.playerId ?? "") === String(playerId);
            const nextDiscard = [...prev.discardPile, card.image];

            if (isCurrent) {
                // Nejprve se pokusíme odstranit z ruky, pokud tam není, odstraníme z vyložených karet
                const wasInHand = (prev.handCards ?? []).some(c => c.id === card.id);
                const nextHand = wasInHand
                    ? (prev.handCards ?? []).filter(c => c.id !== card.id)
                    : (prev.handCards ?? []);

                const nextInPlay = wasInHand
                    ? prev.inPlayCards // karta byla v ruce, inPlayCards zůstává
                    : (prev.inPlayCards ?? []).filter(c => c.id !== card.id); // karta byla ve hře

                return {
                    ...prev,
                    handCards: nextHand,
                    inPlayCards: nextInPlay,
                    discardPile: nextDiscard
                };
            } else {
                // Jiný hráč: přidej do discardu a odeber z jeho vyložených karet v players
                const updatedPlayers = (prev.players ?? []).map(p =>
                    String(p.id) === String(playerId)
                        ? { ...p, inPlayCards: (p.inPlayCards ?? []).filter(c => c.id !== card.id) }
                        : p
                );

                return {
                    ...prev,
                    players: updatedPlayers,
                    discardPile: nextDiscard
                };
            }
        });
    } catch (error) {
        console.error("chyba při parsování", error, payload);
        toast.error('Chybná odpověď serveru');
    }
}

export function putCardInPlay(ws: WebSocket | null, cardId: number) {
    if (ws !== null) {
        console.log("putCardInPlay - vykládám kartu do hry:", cardId);
        ws.send("vylozeni:" + cardId);
    }
}

export function putCardInPlayOnPlayer(ws: WebSocket | null, cardId: number, playerId: number) {
    if (ws !== null) {
        console.log("putCardInPlayOnPlayer - vykládám kartu na hráče:", cardId, "na hráče:", playerId);
        ws.send("vylozeni:" + cardId + "," + playerId);
    }
}

export function clickUIButton(ws: WebSocket | null, buttonId: number) {
    if (ws !== null) {
        ws.send(`uiClick:${buttonId}`);
    }
}