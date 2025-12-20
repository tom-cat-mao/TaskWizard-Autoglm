package com.example.autoglm

import android.app.Application
import com.example.autoglm.data.SystemMessageType
import com.example.autoglm.ui.theme.ThemeMode
import com.example.autoglm.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * MainViewModel单元测试
 *
 * 测试范围：
 * 1. 主题管理（亮色/暗色/Pure Black切换）
 * 2. API配置管理（保存和加载）
 * 3. 任务状态管理（启动/停止）
 * 4. 消息管理（添加和清空）
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // 使用Robolectric的Application
        application = RuntimeEnvironment.getApplication()
        viewModel = MainViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== 主题管理测试 ====================

    @Test
    fun `初始主题应该是亮色模式`() {
        val state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertFalse(state.pureBlackEnabled)
    }

    @Test
    fun `切换到暗色模式应该更新状态`() = runTest {
        // When
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(ThemeMode.DARK, state.themeMode)
    }

    @Test
    fun `切换到亮色模式应该更新状态`() = runTest {
        // Given
        viewModel.updateThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // When
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(ThemeMode.LIGHT, state.themeMode)
    }

    @Test
    fun `启用Pure Black应该更新状态`() = runTest {
        // When
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.pureBlackEnabled)
    }

    @Test
    fun `禁用Pure Black应该更新状态`() = runTest {
        // Given
        viewModel.togglePureBlack(true)
        advanceUntilIdle()

        // When
        viewModel.togglePureBlack(false)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.pureBlackEnabled)
    }

    // ==================== API配置管理测试 ====================

    @Test
    fun `更新API Key应该更新状态`() = runTest {
        // When
        val testApiKey = "test-api-key-12345"
        viewModel.updateApiKey(testApiKey)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testApiKey, state.apiKey)
    }

    @Test
    fun `更新Base URL应该更新状态`() = runTest {
        // When
        val testUrl = "https://test.example.com"
        viewModel.updateBaseUrl(testUrl)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testUrl, state.baseUrl)
    }

    @Test
    fun `更新Model名称应该更新状态`() = runTest {
        // When
        val testModel = "test-model"
        viewModel.updateModel(testModel)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testModel, state.model)
    }

    @Test
    fun `保存配置应该添加成功消息`() = runTest {
        // Given
        viewModel.updateApiKey("test-key")
        viewModel.updateBaseUrl("https://test.com")
        viewModel.updateModel("test-model")
        advanceUntilIdle()

        // When
        viewModel.saveSettings()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.messages.isNotEmpty())
        assertTrue(state.messages.any {
            it is com.example.autoglm.data.MessageItem.SystemMessage &&
            it.type == SystemMessageType.SUCCESS
        })
    }

    // ==================== 任务状态管理测试 ====================

    @Test
    fun `初始任务状态应该是未运行`() {
        val state = viewModel.state.value
        assertFalse(state.isRunning)
        assertEquals("", state.currentTask)
    }

    @Test
    fun `更新任务描述应该更新状态`() = runTest {
        // When
        val testTask = "测试任务"
        viewModel.updateTask(testTask)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testTask, state.currentTask)
    }

    @Test
    fun `清空任务描述应该清空状态`() = runTest {
        // Given
        viewModel.updateTask("测试任务")
        advanceUntilIdle()

        // When
        viewModel.clearTask()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("", state.currentTask)
    }

    @Test
    fun `启动任务时没有API Key应该显示错误`() = runTest {
        // Given
        viewModel.updateTask("测试任务")
        viewModel.updateApiKey("") // 清空API Key
        advanceUntilIdle()

        // When
        viewModel.startTask()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isRunning)
        assertTrue(state.messages.any {
            it is com.example.autoglm.data.MessageItem.SystemMessage &&
            it.type == SystemMessageType.ERROR
        })
    }

    @Test
    fun `启动任务时没有任务描述应该显示警告`() = runTest {
        // Given
        viewModel.updateTask("") // 清空任务
        advanceUntilIdle()

        // When
        viewModel.startTask()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isRunning)
        assertTrue(state.messages.any {
            it is com.example.autoglm.data.MessageItem.SystemMessage &&
            it.type == SystemMessageType.WARNING
        })
    }

    @Test
    fun `停止任务应该更新运行状态`() = runTest {
        // Given
        viewModel.updateTask("测试任务")
        viewModel.updateApiKey("test-key")
        viewModel.startTask()
        advanceUntilIdle()

        // When
        viewModel.stopTask()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isRunning)
    }

    // ==================== 消息管理测试 ====================

    @Test
    fun `添加思考消息应该更新消息列表`() = runTest {
        // When
        viewModel.addThinkMessage("AI正在思考...")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(1, state.messages.size)
        assertTrue(state.messages[0] is com.example.autoglm.data.MessageItem.ThinkMessage)
    }

    @Test
    fun `添加系统消息应该更新消息列表`() = runTest {
        // When
        viewModel.addSystemMessage("系统提示", SystemMessageType.INFO)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(1, state.messages.size)
        assertTrue(state.messages[0] is com.example.autoglm.data.MessageItem.SystemMessage)
    }

    @Test
    fun `清空消息应该清空消息列表`() = runTest {
        // Given
        viewModel.addThinkMessage("消息1")
        viewModel.addSystemMessage("消息2", SystemMessageType.INFO)
        advanceUntilIdle()

        // When
        viewModel.clearMessages()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.messages.isEmpty())
    }

    @Test
    fun `多条消息应该按顺序添加`() = runTest {
        // When
        viewModel.addThinkMessage("消息1")
        viewModel.addThinkMessage("消息2")
        viewModel.addThinkMessage("消息3")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(3, state.messages.size)
    }
}
