#!/bin/bash
cd "$(dirname "$0")"
nohup java -jar mi-camera-nas-viewer-1.0.0.jar > out.log 2>&1 &
echo "Started, PID: $!"
echo "Console log: out.log"
echo "Application log: ./log"
