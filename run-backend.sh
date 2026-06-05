#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/backend"
mkdir -p out lib
if [ ! -f "lib/mysql-connector-j-8.3.0.jar" ]; then
  if [ -f "$HOME/.m2/repository/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar" ]; then
    echo "Found MySQL Connector in local Maven repository. Copying..."
    cp "$HOME/.m2/repository/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar" lib/
  fi
fi
echo "Compiling Java sources..."
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.3.0.jar" -d out @sources.txt
rm sources.txt
echo ""
echo "  PlayRent backend running on http://localhost:8080"
echo "  Open frontend/index.html in your browser."
echo "  Press Ctrl+C to stop."
echo ""
java -cp "out:lib/mysql-connector-j-8.3.0.jar" com.sportrent.Main
