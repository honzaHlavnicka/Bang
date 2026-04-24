#!/bin/bash

# ! upozornění !
# tento script byl částečně vygenerován AI
# --------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
PLUGINS_DIR="$BUILD_DIR/pluginy"

# Zjisti, zda chceme spustit server po buildu
if [ "$1" = "s" ]; then
    # spustí server bez buildu
    echo "⚠️  Spouštím server bez buildu (použije se poslední zkompilovaná verze)"
    cd "$BUILD_DIR"
    bash start.sh

else


RUN_SERVER=false
if [ "$1" = "r" ]; then
    RUN_SERVER=true
fi



echo "🔨 Zahajuji build..."
if [ "$RUN_SERVER" = true ]; then
    echo "   (server se spustí po úspěšném buildu)"
fi
echo ""

cd "$SCRIPT_DIR"

# Čištění a build projektu
echo "📦 Kompiluju projekt..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "❌ Chyba při kompilaci!"
    exit 1
fi

echo "✅ Projekt zkompilován"
echo ""

# Vyčištění staré build složky
if [ -d "$BUILD_DIR" ]; then
    echo "🗑️  Odstraňuji starou build složku..."
    rm -rf "$BUILD_DIR"
fi

# Vytvoření build struktury
echo "📁 Vytvářím build strukturu..."
mkdir -p "$PLUGINS_DIR"

# Kopírování serveru
echo "📋 Kopíruji server..."
cp server/target/server-1.0-SNAPSHOT.jar "$BUILD_DIR/server.jar"

# Kopírování pluginů
echo "📋 Kopíruji pluginy..."
if [ -f "pluginy/bang/target/bang-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/bang/target/bang-1.0-SNAPSHOT.jar "$PLUGINS_DIR/bang.jar"
fi

if [ -f "pluginy/prsi/target/prsi-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/prsi/target/prsi-1.0-SNAPSHOT.jar "$PLUGINS_DIR/prsi.jar"
fi

if [ -f "pluginy/VychoziHry/target/VychoziHry-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/VychoziHry/target/VychoziHry-1.0-SNAPSHOT.jar "$PLUGINS_DIR/VychoziHry.jar"
fi

if [ -f "pluginy/Uno/target/Uno-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/Uno/target/Uno-1.0-SNAPSHOT.jar "$PLUGINS_DIR/Uno.jar"
fi

if [ -f "pluginy/milostny-dopis/target/milostny-dopis-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/milostny-dopis/target/milostny-dopis-1.0-SNAPSHOT.jar "$PLUGINS_DIR/milostny-dopis.jar"
fi

# Vytvoření startovacího scriptu
echo "📋 Vytvářím startovací script..."
cat > "$BUILD_DIR/start.sh" << 'EOF'
#!/bin/bash
# Startovací script pro server Bang
# Načte konfiguraci z .env souboru pokud existuje

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Načti .env soubor pokud existuje
if [ -f "$SCRIPT_DIR/.env" ]; then
    echo "📝 Načítám konfiguraci z .env..."
    export $(cat "$SCRIPT_DIR/.env" | grep -v '^#' | xargs)
elif [ -f "$SCRIPT_DIR/../.env" ]; then
    echo "📝 Načítám konfiguraci z ../.env..."
    export $(cat "$SCRIPT_DIR/../.env" | grep -v '^#' | xargs)
else
    echo "⚠️  .env soubor nenalezen, používám výchozí nastavení"
fi

# Nastav výchozí hodnoty pokud nejsou nastaveny
SERVER_PORT=${SERVER_PORT:-8080}
ADMIN_PASSWORD=${ADMIN_PASSWORD:-heslo123}

echo "🎮 Spouštím server:"
echo "   Port: $SERVER_PORT"
echo "   Heslo: (nastaveno)"
echo ""

cd "$SCRIPT_DIR"
java -jar server.jar
EOF
chmod +x "$BUILD_DIR/start.sh"

echo ""
echo "✨ Build dokončen!"
echo ""
echo "📍 Umístění:"
echo "   Server: $BUILD_DIR/server.jar"
echo "   Pluginy: $PLUGINS_DIR/"
echo ""
echo "📝 Konfigurace:"
echo "   Vytvořte soubor .env v kořeni projektu nebo v $BUILD_DIR/"
echo "   Příklad:"
echo "     SERVER_PORT=8080"
echo "     ADMIN_PASSWORD=heslo123"
echo ""
echo "🚀 Spuštění serveru:"
echo "   cd $BUILD_DIR && bash start.sh"
echo "   nebo"
echo "   cd $BUILD_DIR && java -jar server.jar"
echo ""

# Pokud je zadán parametr 'r', spusť server
if [ "$RUN_SERVER" = true ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🎮 Spouštím server..."
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    cd "$BUILD_DIR"
    bash start.sh
fi
fi