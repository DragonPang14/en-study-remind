#!/bin/bash

APP_DIR="/app/english-study-remind"
PID_PATH="$APP_DIR/app.pid"

echo ">>> 正在重启..."

# 停止进程
if [ -f "$PID_PATH" ]; then
    PID=$(cat "$PID_PATH")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo ">>> 停止进程 (PID: $PID)..."
        kill -9 "$PID" 2>/dev/null
        sleep 2
    fi
    rm -f "$PID_PATH"
fi

# 调用启动脚本
echo ">>> 调用启动脚本..."
cd /app/english-study-remind/bin
bash start.sh