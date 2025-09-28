import type { GameStateType } from "./GameContext";

export function handleGameMessage(event: MessageEvent,setGameState: (state: any) => void) {
    console.log("%cZpráva ze serveru: " + event.data, "color: green");

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
        default:
            break;
    }
}


export function setGameValue(ws: WebSocket | null, data: any, type: string) {
    if (ws !== null) {
        if(type === "DRAW_CARD") {
            ws.send("linuti");
        } else if(type === "CONNECT") {
            ws.send("pripojeni:" + data.kod);
            ws.send("zmenaJmena:" + data.jmeno);
        }
    }
}

export function connectToGame(ws: WebSocket | null, gameCode: string, name: string) {
    if (ws !== null) {
        ws.send("pripojeniKeHre:" + gameCode);
        ws.send("zmenaJmena:" + name);
    }
}

export function changePlayerName(ws: WebSocket | null, newName: string) {
    if (ws !== null) {
        ws.send("zmenaJmena:" + newName);
    }
}

export function chooseCharacter(ws: WebSocket | null, changeGameState: (state: any) => void, character: string) {
    changeGameState((prev) => ({ ...prev, character:character, characters:[] }));
    if (ws !== null) {
        ws.send("setPostava:" + character);
    }
}

