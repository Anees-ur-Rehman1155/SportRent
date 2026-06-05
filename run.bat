@echo off
setlocal

cd /d "%~dp0"

echo.
echo  ============================================
echo    PlayRent  --  Backend Launcher
echo  ============================================
echo.

where javac >nul 2>nul
if errorlevel 1 (
    echo  [ERROR] Java JDK not found.
    echo  Download JDK 17+ from: https://adoptium.net
    pause
    exit /b 1
)

cd backend

if not exist lib mkdir lib
if not exist lib\mysql-connector-j-8.3.0.jar (
    if exist "C:\Users\Anees\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" (
        echo  Found MySQL Connector in local Maven repository. Copying...
        copy "C:\Users\Anees\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" lib\ >nul
    ) else (
        echo  [WARNING] MySQL JDBC connector not found in lib\ or local Maven cache.
    )
)

if exist out rmdir /s /q out
mkdir out

echo  Compiling Java sources...

javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d out ^
  src\com\sportrent\Main.java ^
  src\com\sportrent\handler\AuthHandler.java ^
  src\com\sportrent\handler\EquipmentHandler.java ^
  src\com\sportrent\handler\RentalHandler.java ^
  src\com\sportrent\handler\RootHandler.java ^
  src\com\sportrent\service\DatabaseConnection.java ^
  src\com\sportrent\service\Http.java ^
  src\com\sportrent\service\Json.java ^
  src\com\sportrent\service\StorageService.java

if errorlevel 1 (
    echo.
    echo  [ERROR] Compilation failed.
    pause
    exit /b 1
)

echo  Compiled successfully.
echo.
echo  Backend running on http://localhost:8080
echo  Open frontend\index.html in your browser.
echo  Press Ctrl+C to stop.
echo.

java -cp "out;lib\mysql-connector-j-8.3.0.jar" com.sportrent.Main

endlocal
pause
