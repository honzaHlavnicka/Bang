import Peer from 'peerjs';
import type { MediaConnection } from 'peerjs';

// Definice typu pro callback, který řekne Reactu: "Něco se změnilo, překresli se"
type UpdateCallback = () => void;

class VoiceManager {
    private peer: Peer | null = null;
    private myUserId: string | null = null;
    public localStream: MediaStream | null = null;
    
    // Mapa userId -> MediaStream (všechny hlasy ostatních hráčů)
    public remoteStreams: Map<string, MediaStream> = new Map();
    
    // Držíme si reference na probíhající hovory, abychom je mohli korektně ukončit
    private activeConnections: Map<string, MediaConnection> = new Map();

    private onUpdate: UpdateCallback = () => {};

    /**
     * Inicializace PeerJS a získání mikrofonu
     * Volá se po kliknutí na tlačítko "Připojit k Voice Chatu"
     */
    public async init(userId: string, onUpdate: UpdateCallback, sendMessage: (msg: string) => void) {
        if (this.peer) return; // Už je inicializováno

        this.myUserId = userId;
        this.onUpdate = onUpdate;

        try {
            // 1. Získání mikrofonu
            this.localStream = await navigator.mediaDevices.getUserMedia({
                audio: {
                    echoCancellation: true,
                    noiseSuppression: true,
                    autoGainControl: true
                }
            });

            // 2. Vytvoření PeerJS instance (ID necháme vygenerovat automaticky)
            this.peer = new Peer();

            // 3. Po otevření spojení s Peer serverem pošleme naše ID na Java server
            this.peer.on('open', (peerId) => {
                console.log('Voice: PeerJS připojen pod ID:', peerId);
                sendMessage(`voicechat:JOIN:${peerId}`);
            });

            // 4. Obsluha příchozích hovorů (když nám někdo volá)
            this.peer.on('call', (call) => {
                const callerUserId = call.metadata.userId;
                console.log('Voice: Přijímám hovor od:', callerUserId);

                call.answer(this.localStream!); // "Zvedneme" hovor a pošleme náš hlas
                this.setupCallHandlers(call, callerUserId);
            });

            this.peer.on('error', (err) => {
                console.error('Voice: PeerJS Error:', err.type, err);
            });

        } catch (err) {
            console.error('Voice: Nepodařilo se získat přístup k mikrofonu:', err);
        }
    }

    /**
     * Volá se, když nám Java server řekne o novém hráči (NEW_PEER)
     */
    public callPlayer(remoteUserId: string, remotePeerId: string) {
        if (!this.peer || !this.localStream || !this.myUserId) return;
        
        // Pokud už s ním mluvíme, nevoláme znovu
        if (this.activeConnections.has(remoteUserId)) return;

        console.log(`Voice: Volám hráči ${remoteUserId} na PeerID ${remotePeerId}`);

        const call = this.peer.call(remotePeerId, this.localStream, {
            metadata: { userId: this.myUserId } // Předáme naše ID, aby věděl, kdo volá
        });

        this.setupCallHandlers(call, remoteUserId);
    }

    /**
     * Nastavení eventů pro konkrétní hovor
     */
    private setupCallHandlers(call: MediaConnection, remoteUserId: string) {
        call.on('stream', (remoteStream) => {
            console.log(`Voice: Přijat audio stream od ${remoteUserId}`);
            this.remoteStreams.set(remoteUserId, remoteStream);
            this.activeConnections.set(remoteUserId, call);
            this.onUpdate(); // Upozorníme React
        });

        call.on('close', () => {
            this.removeUser(remoteUserId);
        });

        call.on('error', () => {
            this.removeUser(remoteUserId);
        });
    }

    /**
     * Odstranění uživatele (např. po zprávě LEFT ze serveru)
     */
    public removeUser(userId: string) {
        const call = this.activeConnections.get(userId);
        if (call) call.close();
        
        this.remoteStreams.delete(userId);
        this.activeConnections.delete(userId);
        this.onUpdate();
    }

    /**
     * Úplné vypnutí (při odchodu ze hry)
     */
    public disconnect() {
        this.activeConnections.forEach(call => call.close());
        this.peer?.destroy();
        this.localStream?.getTracks().forEach(track => track.stop());
        
        this.peer = null;
        this.remoteStreams.clear();
        this.activeConnections.clear();
        this.onUpdate();
    }

    /**
     * Dočasné vypnutí/zapnutí mikrofonu (Mute)
     */
    public toggleMute(isMuted: boolean) {
        this.localStream?.getAudioTracks().forEach(track => {
            track.enabled = !isMuted;
        });
    }


    /**
     * Hlavní vstupní bod pro zprávy ze serveru s prefixem "voicechat:"
     * @param payload - zbytek zprávy bez prefixu (např. "NEW_PEER:userId:peerId")
     */
    public handleMessage(payload: string) {
        // Rozdělíme zprávu na podtyp a data (např. ["NEW_PEER", "u123", "p456"])
        const parts = payload.split(':');
        const subType = parts[0];

        switch (subType) {
            case 'novyHrac': {
                const userId = parts[1];
                const peerId = parts[2];
                this.callPlayer(userId, peerId);
                break;
            }

            case 'odpojeni': {
                const userId = parts[1];
                this.removeUser(userId);
                break;
            }

            case 'seznamPeeru': {
                // Formát: PEER_LIST:userId1,peerId1|userId2,peerId2
                const listData = parts[1];
                this.handlePeerList(listData);
                break;
            }

            case 'ztlumeni': {
                const userId = parts[1];
                const isMuted = parts[2];
                // Tady bys mohl aktualizovat stav ikonky v UI (pokud to máš v paměti)
                console.log(`Hráč ${userId} si ${isMuted === 'true' ? 'vypnul' : 'zapnul'} mikrofon.`);
                break;
            }

            default:
                console.warn('Voice: Neznámý podtyp zprávy:', subType);
        }
    }

    private handlePeerList(data: string) {
        if (!data) return;
        // Rozsekáme seznam podle svislítka a dvojtečky
        // Formát: userId1:peerId1|userId2:peerId2|...
        const peers = data.split('|'); 
        peers.forEach(peerStr => {
            const [uId, pId] = peerStr.split(':');
            if (uId && pId) this.callPlayer(uId, pId);
        });
    }
}

// Exportujeme instanci jako Singleton
export const voiceManager = new VoiceManager();