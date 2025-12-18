package com.example.autoglm.service

import android.content.Context
import android.os.RemoteException
import com.example.autoglm.IAutoGLMService
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess

/**
 * 运行在 ADB Shell 进程中的服务。
 * 拥有 UID 2000 权限，可直接执行 input/screencap 命令。
 */
class AutoGLMUserService(context: Context) : IAutoGLMService.Stub() {

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

    override fun takeScreenshot(): ByteArray {
        return try {
            // 使用 screencap -p 输出 PNG 流到 stdout
            val process = Runtime.getRuntime().exec("screencap -p")
            val outputStream = ByteArrayOutputStream()
            
            process.inputStream.use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            
            // 简单校验：如果返回数据极小，可能是报错信息（如 FLAG_SECURE 限制）
            val bytes = outputStream.toByteArray()
            if (bytes.size < 100) {
                // 可以在这里处理黑屏逻辑，暂时原样返回
            }
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    override fun injectInputBase64(base64Text: String) {
        // 复刻 Open-AutoGLM 的 input.py 逻辑
        val cmd = "am broadcast -a ADB_INPUT_B64 --es msg $base64Text"
        executeShellCommand(cmd)
    }
}
