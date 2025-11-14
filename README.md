# Hra bang
Server engine pro podporu karetních her využívajících prvků Bangu nebo méně. K němu klient vyvíjený souběžně.

V současné době moje ročníková práce (IOČ)

# jak spustit  v dev režimu
1) nejprve je potřeba otevřít a spustit java projekt v nějakém IDE (například Netbeans)
   - V netbens například použijeme `f6`.
   - Pokud je problém s nenainstalovanímy knihovnami, tak mi pomohlo najít to místo kde se importují a zmáčknout `ctrl` + `space` a pak enter. (ještě zjistím, proč a jak to opravit lépe)
2) poté je třeba najít soubor `/klient_react/bang/src/modules/GameProvider.tsx` a v něm nastavení režimu adresy serveru. V něm odkomentovat řádek obsahující `const socketAdress = "ws://localhost:9999";` pokud chceme používat lokákální vezi.
3) pak se můžou nainstalovat závislosti klienta a spustit jeho server.

    ```bash
   cd klient_react/bang/
   npm install
   npm run dev
  
 
# jak udělat build
Zatím radši nijak, ale můžeme:
1) buildnout javu
2) ve složce `/klient-react/bang/` spustit `npm run build`
