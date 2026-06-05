#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out
echo "Compiling..."
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
rm sources.txt
echo "Starting on http://localhost:8080 ..."
java -cp out com.sportrent.Main
