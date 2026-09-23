import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { spawn, type ChildProcess } from 'child_process';
import WebSocket from 'ws';
import path from 'path';
import { handleGameMessage, createGame, connectToGame, startGame } from '../modules/gameActions';
import { gameStateDefault, type GameStateType } from '../modules/GameContext';

const TEST_PORT = 22216;
const WS_URL = `ws://127.0.0.1:${TEST_PORT}/ws`;

describe('E2E Full-Stack Protocol Integration (Java Server + React Logic)', () => {
  let serverProcess: ChildProcess;

  beforeAll(async () => {
    const rootDir = path.resolve(__dirname, '../../../../');
    const jarPath = path.resolve(rootDir, 'server/target/server-1.0-SNAPSHOT.jar');

    serverProcess = spawn('java', ['-jar', jarPath], {
      cwd: rootDir, // Aby server našel složku pluginy/
      env: {
        ...process.env,
        SERVER_PORT: String(TEST_PORT),
      },
      stdio: ['ignore', 'pipe', 'pipe'],
    });

    serverProcess.stdout?.on('data', () => {
      // ignore
    });
    serverProcess.stderr?.on('data', (_data) => {
      // ignore
    });

    // Počkáme na nastartování serveru
    await new Promise<void>((resolve, reject) => {
      const timeout = setTimeout(() => {
        reject(new Error('Java server se nespustil v limitu 10s'));
      }, 10000);

      const checkConnection = () => {
        const testWs = new WebSocket(WS_URL);
        testWs.on('open', () => {
          testWs.close();
          clearTimeout(timeout);
          resolve();
        });
        testWs.on('error', () => {
          setTimeout(checkConnection, 200);
        });
      };

      setTimeout(checkConnection, 500);
    });
  }, 15000);

  afterAll(async () => {
    if (serverProcess) {
      serverProcess.kill('SIGTERM');
    }
  });

  it('Hráč 1 založí hru Bang, Hráč 2 se připojí se stejným kódem a oba synchronizují stav', async () => {
    // Stav a socket pro Klienta 1
    let state1: GameStateType = { ...gameStateDefault };
    const stateRef1 = { current: state1 };
    const setGameState1 = (updater: (prev: GameStateType) => GameStateType) => {
      state1 = updater(state1);
      stateRef1.current = state1;
    };

    // Stav a socket pro Klienta 2
    let state2: GameStateType = { ...gameStateDefault };
    const stateRef2 = { current: state2 };
    const setGameState2 = (updater: (prev: GameStateType) => GameStateType) => {
      state2 = updater(state2);
      stateRef2.current = state2;
    };

    const ws1 = new WebSocket(WS_URL);
    const ws2 = new WebSocket(WS_URL);

    await new Promise<void>((resolve) => {
      let openCount = 0;
      const onOpen = () => {
        openCount++;
        if (openCount === 2) resolve();
      };
      ws1.on('open', onOpen);
      ws2.on('open', onOpen);
    });

    ws1.on('message', (msg) => {
      const text = msg.toString();
      handleGameMessage(
        { data: text } as MessageEvent,
        setGameState1,
        stateRef1,
        () => {},
        ws1 as any,
        () => {}
      );
    });

    ws2.on('message', (msg) => {
      const text = msg.toString();
      handleGameMessage(
        { data: text } as MessageEvent,
        setGameState2,
        stateRef2,
        () => {},
        ws2 as any,
        () => {}
      );
    });

    // 1. Klient 1 vytvoří hru Bang (typ 0) pomocí reálné akce z gameActions
    createGame(ws1 as any, 0, 'HostHrac');

    // Počkáme na vygenerování kódu hry
    let gameCode = '';
    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (state1.gameCode) {
          gameCode = state1.gameCode;
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Klient 1 neobdržel kód hry'));
      }, 5000);
    });

    expect(gameCode).toMatch(/^[0-9]+$/);
    expect(state1.inGame).toBe(true);

    // 2. Klient 2 se připojí ke hře s obdrženým kódem
    connectToGame(ws2 as any, setGameState2, gameCode, 'HostujiciHrac2');

    // Počkáme, až Klient 2 potvrdí připojení a Klient 1 uvidí 2 hráče v seznamu
    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (state2.inGame && state1.players && state1.players.length >= 2) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Hráči se nesynchronizovali v lobby'));
      }, 5000);
    });

    expect(state1.players?.length).toBe(2);
    expect(state2.inGame).toBe(true);

    // Ověříme, že jména hráčů byla správně přenesena do stavu obou klientů
    const playerNames1 = state1.players?.map((p) => p.name);
    expect(playerNames1).toContain('HostHrac');
    expect(playerNames1).toContain('HostujiciHrac2');

    // 3. Klient 1 (admin) spustí hru
    startGame(ws1 as any);

    // Počkáme na zahájení hry (výběr postav nebo herní kolo)
    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (
          state1.gameStarted ||
          (state1.characters && state1.characters.length > 0) ||
          state1.allowedUIElements?.includes('POSTAVA')
        ) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Hra neodpověděla na zahájení hry'));
      }, 5000);
    });

    // Úklid socketů
    ws1.close();
    ws2.close();
  }, 20000);
});
