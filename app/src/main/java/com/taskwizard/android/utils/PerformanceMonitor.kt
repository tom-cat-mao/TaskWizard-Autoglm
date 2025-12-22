package com.taskwizard.android.utils

import androidx.compose.runtime.*
import android.util.Log
import com.taskwizard.android.BuildConfig

/**
 * 性能监控工具
 *
 * 用于监控Compose组件的重组次数和性能
 */

/**
 * 监控组件重组次数
 *
 * 使用方法：
 * ```
 * @Composable
 * fun MyScreen() {
 *     RecompositionCounter("MyScreen")
 *     // ... 组件内容
 * }
 * ```
 */
@Composable
fun RecompositionCounter(tag: String) {
    val recompositions = remember { mutableIntStateOf(0) }

    SideEffect {
        recompositions.intValue++
        if (BuildConfig.DEBUG) {
            Log.d("Recomposition", "$tag: ${recompositions.intValue} recompositions")
        }
    }
}

/**
 * 监控特定状态的变化
 *
 * 使用方法：
 * ```
 * StateChangeLogger("ThemeMode", state.themeMode)
 * ```
 */
@Composable
fun <T> StateChangeLogger(tag: String, value: T) {
    LaunchedEffect(value) {
        if (BuildConfig.DEBUG) {
            Log.d("StateChange", "$tag changed to: $value")
        }
    }
}

/**
 * 测量组件渲染时间
 *
 * 使用方法：
 * ```
 * @Composable
 * fun MyScreen() {
 *     RenderTimeTracker("MyScreen") {
 *         // ... 组件内容
 *     }
 * }
 * ```
 */
@Composable
fun RenderTimeTracker(tag: String, content: @Composable () -> Unit) {
    val startTime = remember { System.nanoTime() }

    content()

    SideEffect {
        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1_000_000.0
        if (BuildConfig.DEBUG) {
            Log.d("RenderTime", "$tag rendered in ${durationMs}ms")
        }
    }
}

/**
 * 帧率监控器
 *
 * 监控应用的帧率，检测掉帧情况
 */
class FrameRateMonitor {
    private var lastFrameTime = System.nanoTime()
    private val frameTimes = mutableListOf<Long>()
    private val maxSamples = 60 // 保留最近60帧的数据

    fun recordFrame() {
        val currentTime = System.nanoTime()
        val frameTime = currentTime - lastFrameTime
        lastFrameTime = currentTime

        frameTimes.add(frameTime)
        if (frameTimes.size > maxSamples) {
            frameTimes.removeAt(0)
        }

        // 检测掉帧（超过16.67ms表示低于60fps）
        val frameTimeMs = frameTime / 1_000_000.0
        if (frameTimeMs > 16.67 && BuildConfig.DEBUG) {
            Log.w("FrameRate", "Frame drop detected: ${frameTimeMs}ms (${1000.0 / frameTimeMs} fps)")
        }
    }

    fun getAverageFps(): Double {
        if (frameTimes.isEmpty()) return 0.0
        val avgFrameTime = frameTimes.average()
        return 1_000_000_000.0 / avgFrameTime
    }

    fun getStats(): FrameStats {
        if (frameTimes.isEmpty()) {
            return FrameStats(0.0, 0.0, 0.0, 0)
        }

        val frameTimesMs = frameTimes.map { it / 1_000_000.0 }
        val avgFps = getAverageFps()
        val minFrameTime = frameTimesMs.minOrNull() ?: 0.0
        val maxFrameTime = frameTimesMs.maxOrNull() ?: 0.0
        val droppedFrames = frameTimes.count { it > 16_670_000 } // 超过16.67ms

        return FrameStats(avgFps, minFrameTime, maxFrameTime, droppedFrames)
    }

    fun reset() {
        frameTimes.clear()
        lastFrameTime = System.nanoTime()
    }
}

data class FrameStats(
    val averageFps: Double,
    val minFrameTimeMs: Double,
    val maxFrameTimeMs: Double,
    val droppedFrames: Int
)
