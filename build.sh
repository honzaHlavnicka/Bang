#!/bin/bash

# ! upozornění !
# tento script byl vygenerován AI a není přímou součástí projektu
# --------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
PLUGINS_DIR="$BUILD_DIR/pluginy"

# Pokud je zadán parametr 's', pouze spusť existující build bez kompilace
if [ "$1" = "s" ]; then
    if [ -f "$BUILD_DIR/start.sh" ]; then
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "🎮 Spouštím existující build serveru..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        cd "$BUILD_DIR"
        bash start.sh
        exit $?
    else
        echo "❌ Server ještě nebyl sestaven ($BUILD_DIR/start.sh neexistuje)!"
        echo "   Spusťte nejprve './build.sh' nebo './build.sh r'."
        exit 1
    fi
fi

# Zjisti, zda chceme spustit server po buildu
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

# Kopírování Java (JAR) pluginů
echo "📋 Kopíruji Java pluginy..."
for jar in pluginy/*/target/*.jar; do
    if [ -f "$jar" ]; then
        filename=$(basename "$jar")
        if [[ ! "$filename" =~ ^original- && ! "$filename" =~ -sources\.jar$ && ! "$filename" =~ -javadoc\.jar$ ]]; then
            plugin_name=$(echo "$filename" | sed -E 's/-[0-9].*\.jar$//')
            cp "$jar" "$PLUGINS_DIR/${plugin_name}.jar"
            echo "   -> Nalezen a zkopírován Java plugin: ${plugin_name}.jar"
        fi
    fi
done

# --- NOVÉ: Kopírování skriptovaných (JS/Python) pluginů ---
echo "📋 Kopíruji skriptované pluginy..."
# Projde všechny podsložky ve složce pluginy/
for d in pluginy/*/; do
    # Pokud složka obsahuje plugin.json, považujeme ji za platný skriptovaný plugin
    if [ -f "${d}plugin.json" ]; then
        PLUGIN_NAME=$(basename "$d")
        echo "   -> Nalezen skriptovaný plugin: $PLUGIN_NAME"
        # Zkopíruje celou složku i se všemi .js soubory
        cp -r "$d" "$PLUGINS_DIR/$PLUGIN_NAME"
    fi
done

# Vytvoření startovacího scriptu
echo "📋 Vytvářím startovací script..."
cat > "$BUILD_DIR/start.sh" << 'EOF'
#!/bin/bash
# Startovací script pro server
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

# --- OPRAVENO: Přidán flag --enable-native-access pro funkčnost GraalVM ---
java --enable-native-access=ALL-UNNAMED -jar server.jar
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
echo "🚀 Spuštění serveru:
   ./build.sh s         (okamžité spuštění existujícího buildu)
   ./build.sh r         (nový build + automatické spuštění)
   nebo
   cd $BUILD_DIR && bash start.sh"
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