@echo off
setlocal

echo ===============================================
echo     Mi Camera NAS Viewer
echo       Password Encryption Tool
echo ===============================================
echo.

REM Find main JAR file
set "JAR_FILE="
for %%f in (mi-camera-nas-viewer-*.jar) do (
    set "JAR_FILE=%%f"
    goto :found_jar
)

echo ERROR: mi-camera-nas-viewer-*.jar not found!
echo.
echo Please ensure this script is run in the directory containing the JAR file
echo.
echo Alternative:
echo   Online tool (recommended): Visit https://bcrypt-generator.com/
pause
exit /b 1

:found_jar
echo JAR file found: %JAR_FILE%
echo.

if "%~2"=="" (
    if "%~1"=="" (
        echo Interactive mode: Enter password below to encrypt
        echo.
        java -jar "%JAR_FILE%" --encrypt
    ) else (
        echo Command line mode: Encrypt specified password
        echo.
        java -jar "%JAR_FILE%" --encrypt %~1
    )
) else (
    echo Command line mode: Encrypt specified password
    echo.
    java -jar "%JAR_FILE%" --encrypt %*
)

echo.
pause
