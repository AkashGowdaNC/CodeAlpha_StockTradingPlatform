@echo off
REM Compiles and runs the project. Usage: double-click, or type run.bat
echo Compiling...
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt
echo Starting...
echo.
java -cp out com.codealpha.trading.Main
pause
