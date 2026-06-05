@echo off
REM ============================================================
REM   SportRent backend launcher (Windows)
REM   Requires: JDK 17 or newer on PATH (check with `java -version`)
REM ============================================================
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java JDK not found on PATH.
  echo Install JDK 17+ from https://adoptium.net and try again.
  pause
  exit /b 1
)

if not exist out mkdir out
if not exist lib mkdir lib
if not exist lib\mysql-connector-j-8.3.0.jar (
  if exist "C:\Users\Anees\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" (
    copy "C:\Users\Anees\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" lib\ >nul
  )
)

echo Compiling Java sources...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d out @sources.txt
del sources.txt
if errorlevel 1 (
  echo [ERROR] Compilation failed.
  pause
  exit /b 1
)

echo Starting SportRent backend on http://localhost:8080 ...
java -cp "out;lib\mysql-connector-j-8.3.0.jar" com.sportrent.Main

endlocal
