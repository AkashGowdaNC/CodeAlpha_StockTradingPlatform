#!/bin/bash
# Compiles and runs the project. Usage:  bash run.sh
echo "Compiling..."
mkdir -p out
javac -d out $(find src -name "*.java") || { echo "Compilation failed."; exit 1; }
echo "Starting..."
echo
java -cp out com.codealpha.trading.Main
