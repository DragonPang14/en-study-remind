#!/bin/bash

# ================= 配置区域（必须修改JAVA_PATH） =================
# 【关键】把这里改成你第一步找到的Java绝对路径！
JAVA_PATH="/usr/bin/java"

# 其他配置（按你的实际情况）
JAR_NAME="english-study-remind.jar"
APP_DIR="/app/english-study-remind"
JAR_PATH="$APP_DIR/lib/$JAR_NAME"
LOG_PATH="$APP_DIR/logs/app.log"
PID_PATH="$APP_DIR/app.pid"
JAVA_OPTS="-Xms256m -Xmx512m -Dfile.encoding=UTF-8"

# ================= 启动逻辑 =================
echo ">>> 正在启动英语学习提醒项目..."

# 1. 直接验证Java绝对路径是否有效
if [ ! -f "$JAVA_PATH" ]; then
    echo "❌ 错误：Java路径无效，请检查 JAVA_PATH 配置"
    echo "   当前配置的Java路径：$JAVA_PATH"
    exit 1
fi
echo "✅ Java路径：$JAVA_PATH"
echo "✅ Java版本：$($JAVA_PATH -version 2>&1 | head -n 1)"

# 2. 检查JAR包
if [ ! -f "$JAR_PATH" ]; then
    echo "❌ 错误：JAR包不存在，请上传到 $JAR_PATH"
    exit 1
fi
echo "✅ JAR包：$JAR_PATH"

# 3. 停止旧进程
if [ -f "$PID_PATH" ]; then
    PID=$(cat "$PID_PATH")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo ">>> 停止旧进程 (PID: $PID)..."
        kill -9 "$PID" 2>/dev/null
        sleep 2
    fi
    rm -f "$PID_PATH"
fi

# 4. 后台启动（直接用Java绝对路径）
echo ">>> 后台启动..."
nohup "$JAVA_PATH" $JAVA_OPTS -jar "$JAR_PATH" > "$LOG_PATH" 2>&1 &
echo $! > "$PID_PATH"

# 5. 验证启动
sleep 5
if [ -f "$PID_PATH" ] && ps -p $(cat "$PID_PATH") > /dev/null 2>&1; then
    echo "========================================"
    echo "✅ 启动成功！"
    echo "📍 进程PID：$(cat "$PID_PATH")"
    echo "📝 日志文件：$LOG_PATH"
    echo "🔍 查看日志：tail -f $LOG_PATH"
    echo "========================================"
else
    echo "❌ 启动失败，请查看日志：$LOG_PATH"
    rm -f "$PID_PATH"
    exit 1
fi