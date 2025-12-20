package com.example.autoglm

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.autoglm.data.MessageItem
import com.example.autoglm.data.SystemMessageType
import com.example.autoglm.ui.components.*
import com.example.autoglm.ui.theme.AutoGLMTheme
import com.example.autoglm.ui.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

/**
 * UI组件测试
 *
 * 测试范围：
 * 1. TopStatusBar组件
 * 2. TaskInputBar组件
 * 3. MessageList组件
 * 4. 主题应用测试
 */
class UIComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== TopStatusBar测试 ====================

    @Test
    fun topStatusBar_显示模型名称() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TopStatusBar(
                    modelName = "autoglm-phone",
                    hasShizuku = true,
                    hasADBKeyboard = true,
                    onSettingsClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("autoglm-phone")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun topStatusBar_显示Shizuku状态() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TopStatusBar(
                    modelName = "test-model",
                    hasShizuku = true,
                    hasADBKeyboard = false,
                    onSettingsClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Shizuku")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun topStatusBar_显示ADBKeyboard状态() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TopStatusBar(
                    modelName = "test-model",
                    hasShizuku = false,
                    hasADBKeyboard = true,
                    onSettingsClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("ADB Keyboard")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun topStatusBar_设置按钮可点击() {
        var clicked = false
        composeTestRule.setContent {
            AutoGLMTheme {
                TopStatusBar(
                    modelName = "test-model",
                    hasShizuku = true,
                    hasADBKeyboard = true,
                    onSettingsClick = { clicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("设置")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(clicked)
    }

    // ==================== TaskInputBar测试 ====================

    @Test
    fun taskInputBar_显示输入框() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TaskInputBar(
                    task = "",
                    onTaskChange = {},
                    isRunning = false,
                    onStart = {},
                    onStop = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("输入任务描述...")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun taskInputBar_未运行时显示启动按钮() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TaskInputBar(
                    task = "测试任务",
                    onTaskChange = {},
                    isRunning = false,
                    onStart = {},
                    onStop = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("启动")
            .assertExists()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun taskInputBar_运行时显示停止按钮() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TaskInputBar(
                    task = "测试任务",
                    onTaskChange = {},
                    isRunning = true,
                    onStart = {},
                    onStop = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("停止")
            .assertExists()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun taskInputBar_空任务时启动按钮禁用() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TaskInputBar(
                    task = "",
                    onTaskChange = {},
                    isRunning = false,
                    onStart = {},
                    onStop = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("启动")
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun taskInputBar_运行时输入框禁用() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TaskInputBar(
                    task = "测试任务",
                    onTaskChange = {},
                    isRunning = true,
                    onStart = {},
                    onStop = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("测试任务")
            .assertExists()
            .assertIsNotEnabled()
    }

    // ==================== MessageList测试 ====================

    @Test
    fun messageList_空状态显示占位符() {
        composeTestRule.setContent {
            AutoGLMTheme {
                MessageList(messages = emptyList())
            }
        }

        composeTestRule
            .onNodeWithText("输入任务开始对话")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun messageList_显示思考消息() {
        val messages = listOf(
            MessageItem.ThinkMessage("AI正在思考...", System.currentTimeMillis())
        )

        composeTestRule.setContent {
            AutoGLMTheme {
                MessageList(messages = messages)
            }
        }

        composeTestRule
            .onNodeWithText("AI正在思考...")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("AI思考")
            .assertExists()
    }

    @Test
    fun messageList_显示系统消息() {
        val messages = listOf(
            MessageItem.SystemMessage(
                "系统提示",
                SystemMessageType.INFO,
                System.currentTimeMillis()
            )
        )

        composeTestRule.setContent {
            AutoGLMTheme {
                MessageList(messages = messages)
            }
        }

        composeTestRule
            .onNodeWithText("系统提示")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun messageList_显示多条消息() {
        val messages = listOf(
            MessageItem.ThinkMessage("消息1", System.currentTimeMillis()),
            MessageItem.ThinkMessage("消息2", System.currentTimeMillis()),
            MessageItem.SystemMessage("消息3", SystemMessageType.INFO, System.currentTimeMillis())
        )

        composeTestRule.setContent {
            AutoGLMTheme {
                MessageList(messages = messages)
            }
        }

        composeTestRule.onNodeWithText("消息1").assertExists()
        composeTestRule.onNodeWithText("消息2").assertExists()
        composeTestRule.onNodeWithText("消息3").assertExists()
    }

    // ==================== 主题测试 ====================

    @Test
    fun theme_亮色模式应用正确() {
        composeTestRule.setContent {
            AutoGLMTheme(
                themeMode = ThemeMode.LIGHT,
                pureBlackEnabled = false
            ) {
                TopStatusBar(
                    modelName = "test",
                    hasShizuku = true,
                    hasADBKeyboard = true,
                    onSettingsClick = {}
                )
            }
        }

        // 验证组件正常渲染
        composeTestRule
            .onNodeWithText("test")
            .assertExists()
    }

    @Test
    fun theme_暗色模式应用正确() {
        composeTestRule.setContent {
            AutoGLMTheme(
                themeMode = ThemeMode.DARK,
                pureBlackEnabled = false
            ) {
                TopStatusBar(
                    modelName = "test",
                    hasShizuku = true,
                    hasADBKeyboard = true,
                    onSettingsClick = {}
                )
            }
        }

        // 验证组件正常渲染
        composeTestRule
            .onNodeWithText("test")
            .assertExists()
    }

    @Test
    fun theme_PureBlack模式应用正确() {
        composeTestRule.setContent {
            AutoGLMTheme(
                themeMode = ThemeMode.DARK,
                pureBlackEnabled = true
            ) {
                TopStatusBar(
                    modelName = "test",
                    hasShizuku = true,
                    hasADBKeyboard = true,
                    onSettingsClick = {}
                )
            }
        }

        // 验证组件正常渲染
        composeTestRule
            .onNodeWithText("test")
            .assertExists()
    }
}
