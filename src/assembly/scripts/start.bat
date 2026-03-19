@echo off
cd /d %~dp0
echo Starting...
for %%f in (mi-camera-nas-viewer-*.jar) do (
    set "JAR_FILE=%%f"
    goto :found_jar
)
echo ERROR: JAR file not found!
pause
exit /b 1

:found_jar
echo Starting: %JAR_FILE%
java -jar "%JAR_FILE%"
pause
