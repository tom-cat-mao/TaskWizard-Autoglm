package com.example.autoglm.service

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.example.autoglm.IAutoGLMService
import java.io.File
import kotlin.system.exitProcess

/**
 * 运行在 ADB Shell 进程中的服务。
 * 拥有 UID 2000 权限，可直接执行 input/screencap 命令。
 */
class AutoGLMUserService(context: Context) : IAutoGLMService.Stub() {

    companion object {
        private const val TAG = "AutoGLMUserService"
        // 使用 /data/local/tmp - Shell UID(2000)有完整读写权限的标准临时目录
        // 这是Android系统为shell用户预留的临时存储空间
        private const val SCREENSHOT_DIR = "/data/local/tmp"
    }

    override fun destroy() {
        // Shizuku 推荐在销毁时退出进程
        exitProcess(0)
    }

    override fun executeShellCommand(command: String): String {
        return try {
            // 直接执行命令，无需 "adb shell" 前缀
            val process = Runtime.getRuntime().exec(command)
            process.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }

    override fun takeScreenshotToFile(): String {
        return try {
            // 1. 确保目录存在
            val dir = File(SCREENSHOT_DIR)
            if (!dir.exists()) {
                val mkdirResult = executeShellCommand("mkdir -p $SCREENSHOT_DIR")
                Log.d(TAG, "mkdir result: $mkdirResult")
            }

            // 2. 生成唯一文件名（使用时间戳）
            val timestamp = System.currentTimeMillis()
            val filename = "screen_$timestamp.png"
            val fullPath = "$SCREENSHOT_DIR/$filename"

            // 3. 执行截图命令，直接输出到文件
            val process = Runtime.getRuntime().exec(arrayOf("screencap", "-p", fullPath))
            val exitCode = process.waitFor()

            Log.d(TAG, "screencap exit code: $exitCode, path: $fullPath")

            // 4. 验证文件是否成功创建
            val file = File(fullPath)
            if (file.exists() && file.length() > 1000) {
                // 文件存在且大小合理（大于1KB，避免空文件或错误信息）
                Log.d(TAG, "Screenshot saved successfully, size: ${file.length()} bytes")
                fullPath
            } else {
                val errorMsg = if (!file.exists()) {
                    "File not created"
                } else {
                    "File too small (${file.length()} bytes)"
                }
                Log.e(TAG, "Screenshot failed: $errorMsg")
                "ERROR: $errorMsg"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Screenshot exception", e)
            "ERROR: ${e.message}"
        }
    }

    override fun injectInputBase64(base64Text: String) {
        // 复刻 Open-AutoGLM 的 input.py 逻辑
        val cmd = "am broadcast -a ADB_INPUT_B64 --es msg $base64Text"
        executeShellCommand(cmd)
    }

    override fun getCurrentPackage(): String {
        return try {
            // 使用 dumpsys window 获取当前焦点窗口
            // 对齐 Python 原版：不使用正则，直接搜索包名
            val output = executeShellCommand("dumpsys window")
            
            if (output.isEmpty()) {
                Log.w(TAG, "dumpsys window returned empty output")
                return ""
            }
            
            Log.d(TAG, "getCurrentPackage: searching in dumpsys output (${output.length} chars)")
            
            // 遍历每一行，查找包含 mCurrentFocus 或 mFocusedApp 的行
            for (line in output.split("\n")) {
                if ("mCurrentFocus" in line || "mFocusedApp" in line) {
                    Log.d(TAG, "Found focus line: $line")
                    
                    // 在这一行中搜索所有已知的包名
                    for ((appName, packageName) in com.example.autoglm.config.AppMap.PACKAGES) {
                        if (packageName in line) {
                            Log.d(TAG, "Matched package: $packageName -> app: $appName")
                            return packageName
                        }
                    }
                }
            }
            
            Log.d(TAG, "No matching package found, returning empty")
            return ""
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current package", e)
            ""
        }
    }
}
