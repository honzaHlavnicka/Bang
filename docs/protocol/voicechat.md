## Voice Chat System - Dokumentace

### Přehled
Voice chat systém umožňuje hráčům slyšet se navzájem během hry. Používá **PeerJS** pro peer-to-peer komunikaci a WebSocket pro signalizaci.

### Komponenty

#### 1. **Backend - Java (Server)**

##### `VoiceChatManager.java`
- Spravuje připojení hráčů k voice chatu
- Udržuje mapu: `hráč -> PeerJS ID`
- Postupy:
  - `pripojiHrace()` - Zaregistruje nového hráče a oznámí to ostatním
  - `odpojHrace()` - Odebere hráče a upozorní ostatní
  - `posliZpravuONovemHraci()` - Posílá `voicechat:novyHrac:userId:peerId` ostatním
  - `posliSeznamOstatnichHracu()` - Posílá nováčkovi seznam všech ostatních: `voicechat:seznamPeeru:userId:peerId|userId:peerId|...`

##### Integrace do `KomunikatorHryImp.java`
- Zpracování zpráv: `voicechat:JOIN:<peerId>`
- Odpojení: `voiceManager.odpojHrace(hrac)` v `hracOdpojen()`
- Debug info v `getGameStateJSON()`

#### 2. **Frontend - React/TypeScript**

##### `voiceManager.ts` (PeerJS wrapper)
- **Singleton**: `export const voiceManager = new VoiceManager()`
- Spravuje PeerJS peer a lokální stream
- Metody:
  - `init()` - Inicializace PeerJS, získání mikrofonu
  - `callPlayer()` - Zavolá vzdálenému hráči (peer-to-peer hovor)
  - `removeUser()` - Odpojení od uživatele
  - `disconnect()` - Úplné vypnutí
  - `toggleMute()` - Ztlumení/zapnutí mikrofonu
  - `handleMessage()` - Zpracování zpráv ze serveru

**Zprávy zpracovávané:**
- `voicechat:novyHrac:userId:peerId` → `callPlayer(userId, peerId)`
- `voicechat:odpojeni:userId` → `removeUser(userId)`
- `voicechat:seznamPeeru:` → `handlePeerList()`
- `voicechat:ztlumeni:userId:isMuted` → Jen log

##### `AudioPlayer.tsx` (Komponenta)
- Přehrává MediaStream z jednoho hráče
- Neviditelný HTML5 `<audio>` element
- Props:
  - `stream: MediaStream` - Audio stream
  - `playerName?: string` - Jméno hráče (pro debugging)

##### `VoiceChatWidget.tsx` (UI komponenta)
- Tlačítka pro ovládání voice chatu
- Zobrazuje počet připojených uživatelů
- Ztlumení/zapnutí mikrofonu
- Renderuje `AudioPlayer` pro každý remote stream

### Tok komunikace

```
1. Hráč klinkne na "🎙️ Voice Chat" v UI

2. VoiceChatWidget.handleVoiceInit()
   ↓
3. voiceManager.init(userId)
   - Získá mikrofon přes getUserMedia
   - Vytvoří PeerJS peer
   - Pošle serveru: voicechat:JOIN:<peerId>

4. Server obdrží zprávu a:
   - Registruje hráče do voiceManager.hracyPeerIds
   - Pošle ostatním: voicechat:novyHrac:userId:peerId
   - Pošle novému: voicechat:seznamPeeru:userId1:peerId1|userId2:peerId2|...

5. Každý klient obdrží zprávu a:
   - gameActions.ts rozpozná typ "voicechat"
   - Předá payload do voiceManager.handleMessage()
   - voiceManager zavolá callPlayer(userId, peerId)

6. PeerJS zavolá vzdálenému hráči (peer-to-peer)
   - Vzdálený peer přijme hovor a odpovídá
   - Oba si vymění MediaStream

7. React komponenta AudioPlayer
   - Dostane stream z voiceManager.remoteStreams
   - Připojí ho k <audio> elementu
   - Přehrává zvuk
```

### WebSocket protokol

**Klient → Server:**
```
voicechat:JOIN:<peerId>
voicechat:ztlumeni:<isMuted>
```

**Server → Klient:**
```
voicechat:novyHrac:<userId>:<peerId>
voicechat:odpojeni:<userId>
voicechat:seznamPeeru:<userId>:<peerId>|<userId>:<peerId>|...
```

### Klíčové vlastnosti

✅ **Peer-to-peer komunikace** - Zvuk jde přímo mezi hráči, ne serverem
✅ **Automatické připojení** - Když se nový hráč připojí, všichni si automaticky zavolají
✅ **Mute funkce** - Hráči si mohou vypnout mikrofon
✅ **Automatické odpojení** - Když se hráč odpojí z hry, jeho stream se zničí
✅ **Neviditelný UI** - Audio playery jsou skryté, jen přehrávají zvuk

### Instalace Dependencies

Frontend potřebuje:
```bash
npm install peerjs
npm install react-hot-toast
```

### Debugging

**Console logs:**
- `Voice: PeerJS připojen pod ID: <peerId>`
- `Voice: Přijímám hovor od: <userId>`
- `Voice: Přijat audio stream od <userId>`
- `Voice: Hráč <userId> si vypnul mikrofon`

**Debug info v GameProvider:**
Pokud je `VITE_DEBUG=true`, WebSocket je dostupný jako `window.ws`

### Poznámky

- PeerJS server používá default veřejný STUN server (peer.herokuapp.com)
- Pro production by měl běžet vlastní PeerJS server
- Audio echo cancellation je zapnuté v `getUserMedia` konfiguraci
- Všechny hovory jsou v proměnné `activeConnections` kvůli správě lifecycle
