# Hra bang
Server engine pro podporu karetních her využívajících prvků Bangu nebo méně. K němu klient vyvíjený souběžně.

V současné době moje ročníková práce (IOČ)

# jak spustit
1) nejprve je potřeba otevřít a spustit java projekt v nějakém IDE (například Netbeans)
2) poté je třeba najít soubor `/klient_react/bang/src/modules/GameProvider.tsx` a v něm nastavení režimu adresy serveru. V něm odkomentovat řádek obsahující `const socketAdress = "ws://localhost:9999";`.
3) pak se můžou nainstalovat závislosti klienta a spustit jeho server.

    ```bash
   cd klient_react/bang/
   nmp install
   npm run dev
  ```
 
