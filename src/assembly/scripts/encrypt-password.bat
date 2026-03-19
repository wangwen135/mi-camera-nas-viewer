@echo off
chcp 65001 >nul
setlocal

echo ===============================================
echo        小米摄像头 NAS 视频查看工具
echo            密码加密工具
echo ===============================================
echo.

REM 查找 JAR 包
set JAR_FILE=
for %%f in (mi-camera-nas-viewer-*.jar) do (
    set "JAR_FILE=%%f"
    goto :found_jar
)

echo 错误：未找到 JAR 包！
echo 请确保在包含 mi-camera-nas-viewer-x.x.x.jar 的目录中运行此脚本
pause
exit /b 1

:found_jar
echo 找到 JAR 包: %JAR_FILE%
echo.

if "%~1"=="" (
    echo 交互模式：请在下方输入要加密的密码
    echo.
    java -cp "%JAR_FILE%" com.wwh.camera.util.PasswordEncoderUtil
) else (
    echo 命令行模式：加密指定的密码
    java -cp "%JAR_FILE%" com.wwh.camera.util.PasswordEncoderUtil %*
)

echo.
echo.
pause
