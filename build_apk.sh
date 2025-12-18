#!/bin/bash
export ANDROID_HOME="/opt/homebrew/Caskroom/android-platform-tools/36.0.0"

# 检查 SDK 目录
if [ ! -d "$ANDROID_HOME" ]; then
    echo "ERROR: ANDROID_HOME not found at $ANDROID_HOME"
    exit 1
fi

# 检查 licenses 目录
if [ ! -d "$ANDROID_HOME/licenses" ]; then
    echo "WARNING: licenses directory is missing inside ANDROID_HOME"
    # 尝试指向外部的 licenses 目录 (如果存在于 homebrew share 中)
    EXTERNAL_LICENSES="/opt/homebrew/share/android-commandlinetools/licenses"
    if [ -d "$EXTERNAL_LICENSES" ]; then
        echo "Found licenses at $EXTERNAL_LICENSES, symlinking..."
        # 注意：如果目录只读，这步可能会失败，但我们可以尝试
        # ln -s "$EXTERNAL_LICENSES" "$ANDROID_HOME/licenses" 2>/dev/null
    else
        echo "ERROR: Could not locate licenses directory."
    fi
fi

# 清理并构建
echo "Using ANDROID_HOME: $ANDROID_HOME"
chmod +x gradlew
./gradlew clean assembleDebug --stacktrace
