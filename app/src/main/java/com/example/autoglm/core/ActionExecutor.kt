package com.example.autoglm.core

import android.content.Context
import android.util.Log
import com.example.autoglm.IAutoGLMService
import com.example.autoglm.data.Action
import com.example.autoglm.config.AppMap

/**
 * ActionExecutor - 执行 Agent 的各种操作
 * Phase 2: 完整支持所有动作类型
 * Phase 3: IME 自动化管理
 * 
 * 坐标转换修复：使用截图实际尺寸而非物理屏幕尺寸
 */
class ActionExecutor(
    private val context: Context,
    private val service: IAutoGLMService,
    private var screenWidth: Int,   // 改为 var，支持动态更新
    private var screenHeight: Int,  // 改为 var，支持动态更新
    private val onTakeOver: ((String) -> Unit)? = null,      // Take_over 回调
    private val onInteract: ((String) -> String?)? = null,   // Interact 回调
    private val onNote: ((String) -> Unit)? = null           // Note 回调
) {
    
    companion object {
        private const val TAG = "ActionExecutor"
        private const val ADB_KEYBOARD_IME = "com.android.adbkeyboard/.AdbIME"
    }
    
    // Phase 3: IME Management - 记录原始输入法
    private var originalIME: String? = null
    private var imeHasBeenSwitched = false

    /**
     * 更新屏幕尺寸（基于截图实际尺寸）
     * 必须在每次截图后调用，确保坐标转换使用正确的基准
     */
    fun updateScreenSize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        Log.d("ActionExecutor", "Screen size updated: ${width}x${height}")
    }

    fun execute(action: Action) {
        val type = action.action ?: return
        
        Log.d("ActionExecutor", "Executing: $type with params: ${action.location} / ${action.content}")
        Log.d("ActionExecutor", "Current screen size: ${screenWidth}x${screenHeight}")

        // Normalize action type (case insensitive)
        when (type.lowercase()) {
            "launch" -> {
                val appName = action.content ?: return
                val packageName = findPackageName(appName)
                
                if (packageName != null) {
                    Log.d("ActionExecutor", "Resolved app '$appName' to package '$packageName'")
                    runShell("monkey -p $packageName -c android.intent.category.LAUNCHER 1")
                } else {
                    Log.w("ActionExecutor", "Could not find package for app: $appName")
                }
            }
            
            "tap" -> {
                val coords = action.location ?: return
                if (coords.size >= 2) {
                    val x = (coords[0] / 1000.0 * screenWidth).toInt()
                    val y = (coords[1] / 1000.0 * screenHeight).toInt()
                    
                    Log.d("ActionExecutor", "Tap: model coords=[${coords[0]}, ${coords[1]}] -> screen coords=($x, $y)")
                    
                    // 检查是否有敏感操作消息
                    if (action.message != null) {
                        Log.w("ActionExecutor", "Sensitive tap: ${action.message}")
                        // TODO: Phase 4 - 添加确认对话框
                    }
                    
                    runShell("input tap $x $y")
                }
            }
            
            // Phase 2: Double Tap 实现
            "double tap" -> {
                val coords = action.location ?: return
                if (coords.size >= 2) {
                    val x = (coords[0] / 1000.0 * screenWidth).toInt()
                    val y = (coords[1] / 1000.0 * screenHeight).toInt()
                    
                    Log.d("ActionExecutor", "Double Tap: model coords=[${coords[0]}, ${coords[1]}] -> screen coords=($x, $y)")
                    
                    // 第一次点击
                    runShell("input tap $x $y")
                    // 短暂延迟（150ms）
                    try { Thread.sleep(150) } catch (e: Exception) {}
                    // 第二次点击
                    runShell("input tap $x $y")
                    
                    Log.d("ActionExecutor", "Double tap executed at ($x, $y)")
                }
            }
            
            // Phase 2: Long Press 支持动态 duration
            "long press" -> {
                val coords = action.location ?: return
                if (coords.size >= 2) {
                    val x = (coords[0] / 1000.0 * screenWidth).toInt()
                    val y = (coords[1] / 1000.0 * screenHeight).toInt()
                    
                    Log.d("ActionExecutor", "Long Press: model coords=[${coords[0]}, ${coords[1]}] -> screen coords=($x, $y)")
                    
                    // 使用 action.duration 或默认 1000ms
                    val duration = action.duration ?: 1000
                    
                    runShell("input swipe $x $y $x $y $duration")
                    Log.d("ActionExecutor", "Long press executed at ($x, $y) for ${duration}ms")
                }
            }
            
            "type", "type_name" -> {
                val text = action.content ?: return
                
                // Phase 3: 自动切换到 ADB Keyboard
                if (!imeHasBeenSwitched) {
                    try {
                        // 记录当前输入法
                        originalIME = service.getCurrentIME()
                        Log.i(TAG, "Original IME: $originalIME")
                        
                        // 切换到 ADB Keyboard
                        if (originalIME != ADB_KEYBOARD_IME) {
                            val success = service.setIME(ADB_KEYBOARD_IME)
                            if (success) {
                                imeHasBeenSwitched = true
                                Log.i(TAG, "Successfully switched to ADB Keyboard")
                            } else {
                                Log.w(TAG, "Failed to switch to ADB Keyboard, will try to input anyway")
                            }
                        } else {
                            Log.d(TAG, "Already using ADB Keyboard")
                            imeHasBeenSwitched = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to switch IME", e)
                    }
                }
                
                // 执行输入
                val base64 = android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.NO_WRAP)
                try {
                    service.injectInputBase64(base64)
                    Log.d(TAG, "Text input executed: ${text.take(20)}...")
                } catch (e: Exception) {
                    Log.e(TAG, "Input failed", e)
                }
            }
            
            "swipe" -> {
                 val coords = action.location ?: return
                 if (coords.size >= 4) {
                     val x1 = (coords[0] / 1000.0 * screenWidth).toInt()
                     val y1 = (coords[1] / 1000.0 * screenHeight).toInt()
                     val x2 = (coords[2] / 1000.0 * screenWidth).toInt()
                     val y2 = (coords[3] / 1000.0 * screenHeight).toInt()
                     
                     Log.d("ActionExecutor", "Swipe: model coords=[${coords[0]}, ${coords[1]}] -> [${coords[2]}, ${coords[3]}]")
                     Log.d("ActionExecutor", "Swipe: screen coords=($x1, $y1) -> ($x2, $y2)")
                     
                     runShell("input swipe $x1 $y1 $x2 $y2 300")
                 }
            }
            
            "home" -> runShell("input keyevent KEYCODE_HOME")
            "back" -> runShell("input keyevent KEYCODE_BACK")
            "enter" -> runShell("input keyevent KEYCODE_ENTER")
            
            // Phase 2: Take_over 实现
            "take_over" -> {
                val message = action.message ?: action.content ?: "需要人工介入"
                Log.i("ActionExecutor", "Take_over requested: $message")
                onTakeOver?.invoke(message)
            }
            
            // Phase 2: Interact 实现
            "interact" -> {
                val message = action.message ?: action.content ?: "请选择"
                Log.i("ActionExecutor", "Interact requested: $message")
                val result = onInteract?.invoke(message)
                Log.d("ActionExecutor", "User selected: $result")
                // TODO: 将用户选择结果传回 Agent
            }
            
            // Phase 2: Note 实现
            "note" -> {
                val message = action.message ?: action.content ?: "True"
                Log.i("ActionExecutor", "Note: $message")
                onNote?.invoke(message)
            }
            
            // Call_API 在 AgentCore 中处理，这里只记录
            "call_api" -> {
                val instruction = action.instruction ?: action.content ?: ""
                Log.i("ActionExecutor", "Call_API: $instruction")
                // 实际处理在 AgentCore.kt 中
            }
            
            "wait" -> { 
                val duration = action.duration ?: 2000
                try { Thread.sleep(duration.toLong()) } catch (e: Exception) {}
            }
            
            "finish" -> { 
                val message = action.message ?: action.content ?: "任务完成"
                Log.i("ActionExecutor", "Task Finished: $message")
            }
            
            else -> {
                Log.w("ActionExecutor", "Unknown action: $type")
            }
        }
    }

    private fun findPackageName(appName: String): String? {
        // Static Map Lookup ONLY
        AppMap.PACKAGES[appName]?.let { return it }
        
        // Case-insensitive match
        AppMap.PACKAGES.entries.find { it.key.equals(appName, ignoreCase = true) }?.let { return it.value }

        Log.w("ActionExecutor", "App not found in static map: $appName")
        return null
    }

    private fun runShell(cmd: String) {
        try {
            Log.d("Executor", "Running: $cmd")
            service.executeShellCommand(cmd)
        } catch (e: Exception) {
            Log.e("Executor", "Shell failed", e)
        }
    }
    
    // ==================== Phase 3: IME Management ====================
    
    /**
     * 还原原始输入法
     * 应在任务完成或发生错误时调用
     */
    fun restoreIME() {
        if (!imeHasBeenSwitched || originalIME == null) {
            Log.d(TAG, "No need to restore IME (not switched or no original IME)")
            return
        }
        
        try {
            Log.i(TAG, "Restoring IME to: $originalIME")
            val success = service.setIME(originalIME!!)
            
            if (success) {
                Log.i(TAG, "Successfully restored IME")
            } else {
                Log.w(TAG, "Failed to restore IME")
            }
            
            // 重置状态
            imeHasBeenSwitched = false
            originalIME = null
        } catch (e: Exception) {
            Log.e(TAG, "Exception while restoring IME", e)
        }
    }
    
    /**
     * 检查是否已切换 IME
     */
    fun hasIMEBeenSwitched(): Boolean = imeHasBeenSwitched
}
