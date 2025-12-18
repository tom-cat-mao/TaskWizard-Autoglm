package com.example.autoglm.core

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.autoglm.IAutoGLMService
import com.example.autoglm.data.Action
import com.example.autoglm.config.AppMap

class ActionExecutor(
    private val context: Context,
    private val service: IAutoGLMService,
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    fun execute(action: Action) {
        val type = action.action ?: return
        
        Log.d("ActionExecutor", "Executing: $type with params: ${action.location} / ${action.content}")

        // Normalize action type (case insensitive)
        when (type.lowercase()) {
            "launch" -> {
                val appName = action.content ?: return
                // Try to find package via static map or dynamic scan
                val packageName = findPackageName(appName)
                
                if (packageName != null) {
                    Log.d("ActionExecutor", "Resolved app '$appName' to package '$packageName'")
                    // Use monkey trick to launch app without knowing exact activity
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
                    runShell("input tap $x $y")
                }
            }
            "type", "type_name" -> {
                val text = action.content ?: return
                val base64 = android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.NO_WRAP)
                try {
                    service.injectInputBase64(base64)
                } catch (e: Exception) {
                    Log.e("Executor", "Input failed", e)
                }
            }
            "swipe" -> {
                 val coords = action.location ?: return
                 if (coords.size >= 4) {
                     val x1 = (coords[0] / 1000.0 * screenWidth).toInt()
                     val y1 = (coords[1] / 1000.0 * screenHeight).toInt()
                     val x2 = (coords[2] / 1000.0 * screenWidth).toInt()
                     val y2 = (coords[3] / 1000.0 * screenHeight).toInt()
                     runShell("input swipe $x1 $y1 $x2 $y2 300")
                 }
            }
            "home" -> runShell("input keyevent KEYCODE_HOME")
            "back" -> runShell("input keyevent KEYCODE_BACK")
            "enter" -> runShell("input keyevent KEYCODE_ENTER")
            
            "long press" -> {
                val coords = action.location ?: return
                if (coords.size >= 2) {
                    val x = (coords[0] / 1000.0 * screenWidth).toInt()
                    val y = (coords[1] / 1000.0 * screenHeight).toInt()
                    runShell("input swipe $x $y $x $y 1000")
                }
            }
            "wait" -> { 
                try { Thread.sleep(2000) } catch (e: Exception) {}
            }
            "finish" -> { 
                Log.i("ActionExecutor", "Task Finished: ${action.content}")
            }
            else -> {
                Log.w("ActionExecutor", "Unknown action: $type")
            }
        }
    }

    private fun findPackageName(appName: String): String? {
        // 1. Static Map Lookup ONLY (Strict adherence to privacy/original project)
        // Check exact match
        AppMap.PACKAGES[appName]?.let { return it }
        
        // Check case-insensitive match in map
        AppMap.PACKAGES.entries.find { it.key.equals(appName, ignoreCase = true) }?.let { return it.value }

        // Dynamic lookup removed per user instruction to respect privacy and original project scope.
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
}
