#!/bin/bash
cd "$(dirname "$0")"
# 自动查找 JAR 包，支持任意版本号
JAR_FILE=$(ls mi-camera-nas-viewer-*.jar 2>/dev/null | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "错误：未找到 JAR 包！"
    exit 1
fi
echo "启动: $JAR_FILE"
nohup java -jar "$JAR_FILE" > out.log 2>&1 &
echo "Started, PID: $!"
echo "Console log: out.log"
echo "Application log: ./log"
