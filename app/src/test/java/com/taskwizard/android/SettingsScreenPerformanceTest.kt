package com.taskwizard.android

import android.app.Application
import com.taskwizard.android.ui.theme.ThemeMode
import com.taskwizard.android.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

/**
 * 设置页面性能测试
 *
 * 测试范围：
 * 1. ViewModel状态更新性能
 * 2. 主题切换响应时间
 * 3. API配置验证性能
 * 4. 批量状态更新性能
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SettingsScreenPerformanceTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = RuntimeEnvironment.getApplication()

        // 清空SharedPreferences
        application.getSharedPreferences("app_prefs", 0)
            .edit()
            .clear()
            .commit()

        viewModel = MainViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== 主题切换性能测试 ====================

    @Test
    fun `主题切换应该在10ms内完成`() = runTest {
        val executionTime = measureTimeMillis {
            viewModel.updateThemeMode(ThemeMode.DARK)
            advanceUntilIdle()
        }

        println("主题切换耗时: ${executionTime}ms")
        assertTrue(executionTime < 10, "主题切换耗时${executionTime}ms，超过10ms阈值")
    }

    @Test
    fun `Pure Black切换应该在10ms内完成`() = runTest {
        val executionTime = measureTimeMillis {
            viewModel.togglePureBlack(true)
            advanceUntilIdle()
        }

        println("Pure Black切换耗时: ${executionTime}ms")
        assertTrue(executionTime < 10, "Pure Black切换耗时${executionTime}ms，超过10ms阈值")
    }

    @Test
    fun `连续快速切换主题应该保持流畅`() = runTest {
        val iterations = 10
        val executionTime = measureTimeMillis {
            repeat(iterations) {
                viewModel.updateThemeMode(if (it % 2 == 0) ThemeMode.DARK else ThemeMode.LIGHT)
            }
            advanceUntilIdle()
        }

        val avgTime = executionTime / iterations
        println("平均主题切换耗时: ${avgTime}ms (总计${executionTime}ms / ${iterations}次)")
        assertTrue(avgTime < 10, "平均主题切换耗时${avgTime}ms，超过10ms阈值")
    }

    // ==================== API配置性能测试 ====================

    @Test
    fun `API Key更新应该在5ms内完成`() = runTest {
        val testApiKey = "test-api-key-1234567890"
        val executionTime = measureTimeMillis {
            viewModel.updateApiKey(testApiKey)
            advanceUntilIdle()
        }

        println("API Key更新耗时: ${executionTime}ms")
        assertTrue(executionTime < 5, "API Key更新耗时${executionTime}ms，超过5ms阈值")
    }

    @Test
    fun `Base URL更新应该在5ms内完成`() = runTest {
        val testUrl = "https://api.example.com"
        val executionTime = measureTimeMillis {
            viewModel.updateBaseUrl(testUrl)
            advanceUntilIdle()
        }

        println("Base URL更新耗时: ${executionTime}ms")
        assertTrue(executionTime < 5, "Base URL更新耗时${executionTime}ms，超过5ms阈值")
    }

    @Test
    fun `Model更新应该在5ms内完成`() = runTest {
        val testModel = "autoglm-phone"
        val executionTime = measureTimeMillis {
            viewModel.updateModel(testModel)
            advanceUntilIdle()
        }

        println("Model更新耗时: ${executionTime}ms")
        assertTrue(executionTime < 5, "Model更新耗时${executionTime}ms，超过5ms阈值")
    }

    @Test
    fun `批量更新API配置应该在20ms内完成`() = runTest {
        val executionTime = measureTimeMillis {
            viewModel.updateApiKey("test-api-key-1234567890")
            viewModel.updateBaseUrl("https://api.example.com")
            viewModel.updateModel("autoglm-phone")
            advanceUntilIdle()
        }

        println("批量API配置更新耗时: ${executionTime}ms")
        assertTrue(executionTime < 20, "批量API配置更新耗时${executionTime}ms，超过20ms阈值")
    }

    // ==================== 配置保存性能测试 ====================

    @Test
    fun `保存配置应该在50ms内完成`() = runTest {
        // 先设置配置
        viewModel.updateApiKey("test-api-key-1234567890")
        viewModel.updateBaseUrl("https://api.example.com")
        viewModel.updateModel("autoglm-phone")
        advanceUntilIdle()

        // 测量保存时间
        val executionTime = measureTimeMillis {
            viewModel.saveSettings()
            advanceUntilIdle()
        }

        println("保存配置耗时: ${executionTime}ms")
        assertTrue(executionTime < 50, "保存配置耗时${executionTime}ms，超过50ms阈值")
    }

    // ==================== 状态读取性能测试 ====================

    @Test
    fun `读取状态应该是即时的`() = runTest {
        val executionTime = measureTimeMillis {
            repeat(1000) {
                val state = viewModel.state.value
                // 访问所有状态字段
                state.themeMode
                state.pureBlackEnabled
                state.apiKey
                state.baseUrl
                state.model
                state.isRunning
                state.currentTask
                state.messages
            }
        }

        val avgTime = executionTime / 1000.0
        println("平均状态读取耗时: ${avgTime}ms (总计${executionTime}ms / 1000次)")
        assertTrue(avgTime < 0.1, "平均状态读取耗时${avgTime}ms，超过0.1ms阈值")
    }

    // ==================== 内存性能测试 ====================

    @Test
    fun `大量状态更新不应该导致内存泄漏`() = runTest {
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // 执行大量状态更新
        repeat(100) { i ->
            viewModel.updateThemeMode(if (i % 2 == 0) ThemeMode.DARK else ThemeMode.LIGHT)
            viewModel.togglePureBlack(i % 3 == 0)
            viewModel.updateApiKey("test-key-$i")
            viewModel.updateBaseUrl("https://api-$i.example.com")
            viewModel.updateModel("model-$i")
        }
        advanceUntilIdle()

        // 强制垃圾回收
        System.gc()
        Thread.sleep(100)

        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryIncrease = (finalMemory - initialMemory) / 1024.0 / 1024.0 // MB

        println("内存增长: ${memoryIncrease}MB")
        assertTrue(memoryIncrease < 10, "内存增长${memoryIncrease}MB，超过10MB阈值")
    }

    // ==================== 并发性能测试 ====================

    @Test
    fun `并发状态更新应该保持一致性`() = runTest {
        val executionTime = measureTimeMillis {
            // 模拟多个并发更新
            viewModel.updateThemeMode(ThemeMode.DARK)
            viewModel.togglePureBlack(true)
            viewModel.updateApiKey("concurrent-test-key")
            viewModel.updateBaseUrl("https://concurrent.example.com")
            viewModel.updateModel("concurrent-model")
            advanceUntilIdle()
        }

        // 验证最终状态一致性
        val state = viewModel.state.value
        assertTrue(state.themeMode == ThemeMode.DARK)
        assertTrue(state.pureBlackEnabled)
        assertTrue(state.apiKey == "concurrent-test-key")
        assertTrue(state.baseUrl == "https://concurrent.example.com")
        assertTrue(state.model == "concurrent-model")

        println("并发更新耗时: ${executionTime}ms")
        assertTrue(executionTime < 30, "并发更新耗时${executionTime}ms，超过30ms阈值")
    }

    // ==================== 压力测试 ====================

    @Test
    fun `压力测试 - 1000次随机状态更新`() = runTest {
        val executionTime = measureTimeMillis {
            repeat(1000) { i ->
                when (i % 5) {
                    0 -> viewModel.updateThemeMode(if (i % 2 == 0) ThemeMode.DARK else ThemeMode.LIGHT)
                    1 -> viewModel.togglePureBlack(i % 3 == 0)
                    2 -> viewModel.updateApiKey("stress-test-key-$i")
                    3 -> viewModel.updateBaseUrl("https://stress-$i.example.com")
                    4 -> viewModel.updateModel("stress-model-$i")
                }
            }
            advanceUntilIdle()
        }

        val avgTime = executionTime / 1000.0
        println("压力测试 - 平均更新耗时: ${avgTime}ms (总计${executionTime}ms / 1000次)")
        assertTrue(avgTime < 1, "平均更新耗时${avgTime}ms，超过1ms阈值")
    }

    // ==================== 响应时间测试 ====================

    @Test
    fun `用户交互响应时间应该小于16ms（60fps）`() = runTest {
        // 模拟用户点击主题切换
        val themeChangeTime = measureTimeMillis {
            viewModel.updateThemeMode(ThemeMode.DARK)
            advanceUntilIdle()
        }

        // 模拟用户输入API Key
        val apiKeyInputTime = measureTimeMillis {
            viewModel.updateApiKey("user-input-key")
            advanceUntilIdle()
        }

        // 模拟用户点击保存按钮
        viewModel.updateApiKey("test-api-key-1234567890")
        viewModel.updateBaseUrl("https://api.example.com")
        viewModel.updateModel("autoglm-phone")
        advanceUntilIdle()

        val saveTime = measureTimeMillis {
            viewModel.saveSettings()
            advanceUntilIdle()
        }

        println("主题切换响应时间: ${themeChangeTime}ms")
        println("API Key输入响应时间: ${apiKeyInputTime}ms")
        println("保存按钮响应时间: ${saveTime}ms")

        // 60fps = 16.67ms per frame
        assertTrue(themeChangeTime < 16, "主题切换响应时间${themeChangeTime}ms，超过16ms（60fps）")
        assertTrue(apiKeyInputTime < 16, "API Key输入响应时间${apiKeyInputTime}ms，超过16ms（60fps）")
        // 保存操作可以稍慢，因为涉及磁盘I/O
        assertTrue(saveTime < 50, "保存按钮响应时间${saveTime}ms，超过50ms阈值")
    }
}
