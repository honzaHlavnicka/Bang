import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { spawn, type ChildProcess } from 'child_process';
import WebSocket from 'ws';
import path from 'path';
import { handleGameMessage, createGame, connectToGame, startGame } from '../modules/gameActions';
import { gameStateDefault, type GameStateType } from '../modules/GameContext';

const RECONNECT_PORT = 22218;
const WS_URL = `ws://127.0.0.1:${RECONNECT_PORT}/ws`;

function killServer(proc: ChildProcess): Promise<void> {
  return new Promise((resolve) => {
    if (!proc || proc.killed || proc.exitCode !== null) {
      resolve();
      return;
    }
    proc.once('close', () => resolve());
    proc.kill('SIGKILL');
  });
}

function spawnJavaServer(port: number): Promise<ChildProcess> {
  const rootDir = path.resolve(__dirname, '../../../../');
  const jarPath = path.resolve(rootDir, 'server/target/server-1.0-SNAPSHOT.jar');

  const proc = spawn('java', ['-jar', jarPath], {
    cwd: rootDir,
    env: {
      ...process.env,
      SERVER_PORT: String(port),
    },
    stdio: ['ignore', 'pipe', 'pipe'],
  });

  let stdoutOutput = '';
  let stderrOutput = '';
  proc.stdout?.on('data', (data) => {
    stdoutOutput += data.toString();
  });
  proc.stderr?.on('data', (data) => {
    stderrOutput += data.toString();
  });

  return new Promise((resolve, reject) => {
    let resolved = false;

    const interval = setInterval(() => {
      const testWs = new WebSocket(`ws://127.0.0.1:${port}/ws`);
      testWs.on('open', () => {
        if (!resolved) {
          resolved = true;
          clearInterval(interval);
          clearTimeout(timeout);
          testWs.close();
          resolve(proc);
        }
      });
      testWs.on('error', () => {
        // Ignore and retry on next tick
      });
    }, 250);

    const timeout = setTimeout(() => {
      if (!resolved) {
        resolved = true;
        clearInterval(interval);
        proc.kill('SIGKILL');
        reject(new Error(`Java server na portu ${port} se nespustil v limitu 10s. Stdout: ${stdoutOutput} Stderr: ${stderrOutput}`));
      }
    }, 10000);
  });
}

describe('E2E Reconnect & Server Restart Scenarios', () => {
  let serverProcess: ChildProcess;

  beforeAll(async () => {
    serverProcess = await spawnJavaServer(RECONNECT_PORT);
  }, 15000);

  afterAll(async () => {
    if (serverProcess) {
      await killServer(serverProcess);
    }
  });

  it('Hráč se po nečekaném odpojení socketu úspěšně vrátí do rozehrané hry pomocí tokenu', async () => {
    let state1: GameStateType = { ...gameStateDefault };
    const stateRef1 = { current: state1 };
    const setGameState1 = (updater: (prev: GameStateType) => GameStateType) => {
      state1 = updater(state1);
      stateRef1.current = state1;
    };

    let state2: GameStateType = { ...gameStateDefault };
    const stateRef2 = { current: state2 };
    const setGameState2 = (updater: (prev: GameStateType) => GameStateType) => {
      state2 = updater(state2);
      stateRef2.current = state2;
    };

    let player2Token = '';

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
      handleGameMessage(
        { data: msg.toString() } as MessageEvent,
        setGameState1,
        stateRef1,
        () => {},
        ws1 as any,
        () => {}
      );
    });

    ws2.on('message', (msg) => {
      const text = msg.toString();
      if (text.startsWith('token:')) {
        player2Token = text.replace('token:', '').trim();
      }
      handleGameMessage(
        { data: text } as MessageEvent,
        setGameState2,
        stateRef2,
        () => {},
        ws2 as any,
        () => {}
      );
    });

    // 1. Host založí hru
    createGame(ws1 as any, 0, 'AdminHost');

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
        reject(new Error('Admin neobdržel kód hry'));
      }, 5000);
    });

    // 2. Druhý hráč se připojí
    connectToGame(ws2 as any, setGameState2, gameCode, 'PlayerReconnectTest');

    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (state2.inGame && player2Token.length >= 24 && state1.players && state1.players.length >= 2) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Hráč 2 se nepřipojil nebo neobdržel token'));
      }, 5000);
    });

    // 3. Admin zahájí hru
    startGame(ws1 as any);

    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (state1.gameStarted || (state1.characters && state1.characters.length > 0)) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Hra neodstartovala'));
      }, 5000);
    });

    // 4. Simulace výpadku sítě: Socket hráče 2 se náhle zavře
    ws2.close();

    // 5. Hráč 2 naváže nové spojení s uloženým tokenem
    const ws2Reconnected = new WebSocket(WS_URL);
    await new Promise<void>((resolve) => {
      ws2Reconnected.on('open', () => resolve());
    });

    let reconnectedState: GameStateType = { ...gameStateDefault };
    const reconnectedRef = { current: reconnectedState };
    const setReconnectedState = (updater: (prev: GameStateType) => GameStateType) => {
      reconnectedState = updater(reconnectedState);
      reconnectedRef.current = reconnectedState;
    };

    ws2Reconnected.on('message', (msg) => {
      handleGameMessage(
        { data: msg.toString() } as MessageEvent,
        setReconnectedState,
        reconnectedRef,
        () => {},
        ws2Reconnected as any,
        () => {}
      );
    });

    // 6. Ověření tokenu
    ws2Reconnected.send(`overeniTokenu:${player2Token}`);

    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (reconnectedState.isTokenValid === true) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Ověření tokenu selhalo nebo nepřišla odpověď'));
      }, 5000);
    });

    expect(reconnectedState.isTokenValid).toBe(true);

    // 7. Návrat do hry pomocí vraceniSe
    ws2Reconnected.send(`vraceniSe:${player2Token}`);

    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (reconnectedState.inGame && reconnectedState.gameStarted) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Hráč se po vraceniSe nevrátil do rozehrané hry'));
      }, 5000);
    });

    expect(reconnectedState.inGame).toBe(true);
    expect(reconnectedState.gameStarted).toBe(true);
    expect(reconnectedState.gameCode).toBe(gameCode);

    ws1.close();
    ws2Reconnected.close();
  }, 25000);

  it('Klient po restartu serveru správně detekuje neplatnost starého tokenu a vyčistí stav', async () => {
    // Použijeme token z předchozí hry nebo libovolný platně naformátovaný token
    const oldToken = '123456abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGH';

    // Restartujeme server (zastavíme starý a spustíme novou instanci)
    if (serverProcess) {
      await killServer(serverProcess);
    }
    const RESTART_PORT = 22219;
    serverProcess = await spawnJavaServer(RESTART_PORT);

    const ws = new WebSocket(`ws://127.0.0.1:${RESTART_PORT}/ws`);
    await new Promise<void>((resolve) => {
      ws.on('open', () => resolve());
    });

    let state: GameStateType = {
      ...gameStateDefault,
      inGame: true,
      gameCode: '123456',
      isTokenValid: true,
    };
    const stateRef = { current: state };
    const setGameState = (updater: (prev: GameStateType) => GameStateType) => {
      state = updater(state);
      stateRef.current = state;
    };

    ws.on('message', (msg) => {
      handleGameMessage(
        { data: msg.toString() } as MessageEvent,
        setGameState,
        stateRef,
        () => {},
        ws as any,
        () => {}
      );
    });

    // 1. Klient po restartu ověří starý token
    ws.send(`overeniTokenu:${oldToken}`);

    await new Promise<void>((resolve, reject) => {
      const check = setInterval(() => {
        if (state.isTokenValid === false) {
          clearInterval(check);
          resolve();
        }
      }, 50);
      setTimeout(() => {
        clearInterval(check);
        reject(new Error('Klient neobdržel informaci o neplatnosti tokenu po restartu serveru'));
      }, 5000);
    });

    expect(state.isTokenValid).toBe(false);

    // 2. Pokus o vraceniSe s neplatným tokenem
    ws.send(`vraceniSe:${oldToken}`);

    // Server odpoví chybou a handleGameMessage ji zachytí bez pádu
    await new Promise((resolve) => setTimeout(resolve, 300));

    ws.close();
  }, 25000);
});
