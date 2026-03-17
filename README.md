# Hra bang
Server engine pro podporu karetních her využívajících prvků Bangu nebo méně. K němu klient vyvíjený souběžně.

V současné době moje ročníková práce (IOČ)

# Konfigurace

Aplikace používá `.env` soubory pro konfiguraci. 

**Rychlý start:**

1. Serverový `.env` (kořen projektu):
   ```bash
   SERVER_PORT=8080
   ADMIN_PASSWORD=heslo123
   ```

2. Klientský `.env` (`klient_react/bang/`):
   ```bash
   VITE_SERVER_HOST=localhost
   VITE_SERVER_PORT=8080
   VITE_SERVER_PROTOCOL=ws
   ```

# vytvoř si svojí hru v enginu
Máš nějaký nápad na karetní hru, co by jsi chtěl mít online? Můžeš ji naprogramovat poměrně jednoduše bez nutnosti řešit věci kolem, kromě pravidel a logiky hry.
[Tady máš návod jak](https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md)

# jak spustit  v dev režimu
1) nejprve je potřeba otevřít a spustit java projekt v nějakém IDE (například Netbeans)
   - V netbens například použijeme `f6`.
   - Pokud je problém s nenainstalovanímy knihovnami, tak mi pomohlo najít to místo kde se importují a zmáčknout `ctrl` + `space` a pak enter. (ještě zjistím, proč a jak to opravit lépe)
2) klientská konfigurace - upravte `klient_react/bang/.env` soubor:
   ```bash
   VITE_SERVER_HOST=localhost
   VITE_SERVER_PORT=8080  # Musí odpovídat serveru
   VITE_SERVER_PROTOCOL=ws
   ```
3) pak se můžou nainstalovat závislosti klienta a spustit jeho server.

    ```bash
   cd klient_react/bang/
   npm install
   npm run dev
   ```

# jak udělat build

## Automatizovaně (doporučeno)

```bash
# Nastavte konfiguraci
cat > .env << EOF
SERVER_PORT=8080
ADMIN_PASSWORD=heslo123
EOF

# Spusťte build
./build.sh

# Spusťte server
cd build && bash start.sh
```

## Ručně

1) Buildnout Javu:
   ```bash
   mvn clean package
   ```
2) Ve složce `klient_react/bang/` spusťte build:
   ```bash
   npm run build
   ```

# obsažené hry
- **Bang!**: Hra Bangu. Nejdůležitější a nejsložitější - rozpracovaná.
- **UNO**: Zjednodušená verze Una. Neobsahuje `+2`, `+4` a eso lze dát na každou kartu.
- **prší**: Prší se vším všudy (Esa se nepřebíjejí, na červenou sedmičku se lížou čtyři).
- **volná hra**: Hra pro testování, která nám nenastavuje moc hranice

# dokumentace
- **[Protokol síťové komunikace](docs/protocol/README.md)**: Kompletní dokumentace WebSocket protokolu mezi klientem a serverem
