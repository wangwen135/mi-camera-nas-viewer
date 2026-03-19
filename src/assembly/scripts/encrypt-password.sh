#!/bin/bash

echo "==============================================="
echo "       小米摄像头 NAS 视频查看工具"
echo "           密码加密工具"
echo "==============================================="
echo ""

# 查找 JAR 包
JAR_FILE=$(ls mi-camera-nas-viewer-*.jar 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "错误：未找到 mi-camera-nas-viewer-*.jar！"
    echo ""
    echo "请确保在包含主 JAR 包的目录中运行此脚本"
    echo ""
    echo "其他方式："
    echo "  在线加密（推荐）：访问 https://bcrypt-generator.com/"
    exit 1
fi

echo "找到 JAR 包: $JAR_FILE"
echo ""

if [ $# -eq 0 ]; then
    echo "交互模式：请在下方输入要加密的密码"
    echo ""
    java -jar "$JAR_FILE" --encrypt
else
    echo "命令行模式：加密指定的密码"
    echo ""
    java -jar "$JAR_FILE" --encrypt "$@"
fi

echo ""
