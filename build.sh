#!/bin/bash

# ! upozornění !
# tento script byl vygenerován AI a není přímou součástí projektu
# --------------------------------------------------------


SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
PLUGINS_DIR="$BUILD_DIR/pluginy"

echo "🔨 Zahajuji build..."
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

# Kontrola zda existují VychoziHry plugin
if [ -f "pluginy/VychoziHry/target/VychoziHry-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/VychoziHry/target/VychoziHry-1.0-SNAPSHOT.jar "$PLUGINS_DIR/VychoziHry.jar"
fi

# Kontrola zda existují Uno plugin
if [ -f "pluginy/Uno/target/Uno-1.0-SNAPSHOT.jar" ]; then
    cp pluginy/Uno/target/Uno-1.0-SNAPSHOT.jar "$PLUGINS_DIR/Uno.jar"
fi

echo ""
echo "✨ Build dokončen!"
echo ""
echo "📍 Umístění:"
echo "   Server: $BUILD_DIR/server.jar"
echo "   Pluginy: $PLUGINS_DIR/"
echo ""
echo "🚀 Spuštění serveru:"
echo "   java -jar $BUILD_DIR/server.jar"
echo ""
