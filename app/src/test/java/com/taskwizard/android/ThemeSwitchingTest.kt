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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 主题切换集成测试
 *
 * 测试范围：
 * 1. 主题状态在ViewModel中的传递
 * 2. 主题持久化（SharedPreferences）
 * 3. Pure Black联动逻辑
 * 4. 主题切换的完整流程
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ThemeSwitchingTest {

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

    // ==================== 主题切换流程测试 ====================

    @Test
    fun `完整的主题切换流程 - 亮色到暗色`() = runTest {
        // 1. 初始状态应该是亮色模式
        var state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertFalse(state.pureBlackEnabled)

        // 2. 切换到暗色模式
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)

        // 3. 验证状态已持久化
        val prefs = application.getSharedPreferences("app_prefs", 0)
        assertEquals("DARK", prefs.getString("theme_mode", ""))
    }

    @Test
    fun `完整的主题切换流程 - 暗色到亮色`() = runTest {
        // 1. 先切换到暗色模式
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // 2. 再切换回亮色模式
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()

        // 3. 验证状态
        val state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)

        // 4. 验证持久化
        val prefs = application.getSharedPreferences("app_prefs", 0)
        assertEquals("LIGHT", prefs.getString("theme_mode", ""))
    }

    @Test
    fun `Pure Black只在暗色模式下有效`() = runTest {
        // 1. 在亮色模式下启用Pure Black
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        var state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertTrue(state.pureBlackEnabled) // 状态已保存

        // 2. 切换到暗色模式，Pure Black应该生效
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertTrue(state.pureBlackEnabled) // Pure Black在暗色模式下生效
    }

    @Test
    fun `主题状态应该正确持久化和恢复`() = runTest {
        // 1. 设置主题为暗色 + Pure Black
        viewModel.updateThemeMode(ThemeMode.DARK)
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        // 2. 创建新的ViewModel实例（模拟应用重启）
        val newViewModel = MainViewModel(application)
        advanceUntilIdle()

        // 3. 验证状态已恢复
        val state = newViewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertTrue(state.pureBlackEnabled)
    }

    @Test
    fun `多次快速切换主题应该保持最后的状态`() = runTest {
        // 快速切换多次
        viewModel.updateThemeMode(ThemeMode.DARK)
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        viewModel.updateThemeMode(ThemeMode.DARK)
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // 验证最终状态
        val state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
    }

    // ==================== Pure Black联动测试 ====================

    @Test
    fun `Pure Black状态应该正确持久化`() = runTest {
        // 1. 启用Pure Black
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        // 2. 验证持久化
        val prefs = application.getSharedPreferences("app_prefs", 0)
        assertTrue(prefs.getBoolean("pure_black_enabled", false))

        // 3. 禁用Pure Black
        viewModel.togglePureBlack(false)
        advanceUntilIdle()

        // 4. 验证持久化
        assertFalse(prefs.getBoolean("pure_black_enabled", true))
    }

    @Test
    fun `Pure Black切换应该独立于主题模式`() = runTest {
        // 1. 在亮色模式下切换Pure Black
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        var state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertTrue(state.pureBlackEnabled)

        // 2. 切换到暗色模式，Pure Black状态应该保持
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertTrue(state.pureBlackEnabled)

        // 3. 禁用Pure Black
        viewModel.togglePureBlack(false)
        advanceUntilIdle()

        state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertFalse(state.pureBlackEnabled)
    }

    // ==================== 边界情况测试 ====================

    @Test
    fun `首次启动应该使用默认主题（亮色）`() = runTest {
        // 清空所有配置
        application.getSharedPreferences("app_prefs", 0)
            .edit()
            .clear()
            .commit()

        // 创建新的ViewModel
        val newViewModel = MainViewModel(application)
        advanceUntilIdle()

        // 验证默认状态
        val state = newViewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertFalse(state.pureBlackEnabled)
    }

    @Test
    fun `无效的主题配置应该回退到默认值`() = runTest {
        // 写入无效的主题配置
        application.getSharedPreferences("app_prefs", 0)
            .edit()
            .putString("theme_mode", "INVALID_MODE")
            .commit()

        // 创建新的ViewModel
        val newViewModel = MainViewModel(application)
        advanceUntilIdle()

        // 应该回退到默认的亮色模式
        val state = newViewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
    }

    // ==================== ViewModel共享测试 ====================

    @Test
    fun `同一个ViewModel实例的状态应该在所有观察者中同步`() = runTest {
        // 模拟两个页面观察同一个ViewModel
        val state1 = viewModel.state
        val state2 = viewModel.state

        // 更新主题
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // 两个观察者应该看到相同的状态
        assertEquals(state1.value.themeMode, state2.value.themeMode)
        assertEquals(ThemeMode.DARK, state1.value.themeMode)
        assertEquals(ThemeMode.DARK, state2.value.themeMode)
    }
}
