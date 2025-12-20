package com.example.autoglm

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.autoglm.ui.components.*
import com.example.autoglm.ui.theme.AutoGLMTheme
import org.junit.Rule
import org.junit.Test

/**
 * 对话框组件测试
 *
 * 测试范围：
 * 1. ConfirmDialog显示和交互
 * 2. TakeOverDialog显示和交互
 * 3. PermissionDialog显示和交互
 * 4. 对话框按钮功能
 */
class DialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== ConfirmDialog测试 ====================

    @Test
    fun confirmDialog_显示正确的消息() {
        val testMessage = "确认删除此项目？"

        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = testMessage,
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText(testMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun confirmDialog_显示标题() {
        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("确认操作")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun confirmDialog_显示警告图标() {
        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("警告")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun confirmDialog_确认按钮可点击() {
        var confirmed = false

        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = { confirmed = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("确认执行")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(confirmed)
    }

    @Test
    fun confirmDialog_取消按钮可点击() {
        var dismissed = false

        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("取消")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(dismissed)
    }

    @Test
    fun confirmDialog_显示风险提示() {
        composeTestRule.setContent {
            AutoGLMTheme {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("此操作可能具有风险，请确认是否继续")
            .assertExists()
            .assertIsDisplayed()
    }

    // ==================== TakeOverDialog测试 ====================

    @Test
    fun takeOverDialog_显示正确的消息() {
        val testMessage = "请手动输入验证码"

        composeTestRule.setContent {
            AutoGLMTheme {
                TakeOverDialog(
                    message = testMessage,
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText(testMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun takeOverDialog_显示标题() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TakeOverDialog(
                    message = "测试消息",
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("需要人工介入")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun takeOverDialog_显示操作步骤() {
        composeTestRule.setContent {
            AutoGLMTheme {
                TakeOverDialog(
                    message = "测试消息",
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("操作步骤：")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun takeOverDialog_继续任务按钮可点击() {
        var completed = false

        composeTestRule.setContent {
            AutoGLMTheme {
                TakeOverDialog(
                    message = "测试消息",
                    onComplete = { completed = true },
                    onCancel = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("继续任务")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(completed)
    }

    @Test
    fun takeOverDialog_取消任务按钮可点击() {
        var cancelled = false

        composeTestRule.setContent {
            AutoGLMTheme {
                TakeOverDialog(
                    message = "测试消息",
                    onComplete = {},
                    onCancel = { cancelled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("取消任务")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(cancelled)
    }

    // ==================== PermissionDialog测试 ====================

    @Test
    fun permissionDialog_Shizuku_显示正确的标题和消息() {
        val testTitle = "需要Shizuku权限"
        val testMessage = "本应用需要Shizuku权限来执行自动化操作"

        composeTestRule.setContent {
            AutoGLMTheme {
                PermissionDialog(
                    title = testTitle,
                    message = testMessage,
                    permissionType = PermissionType.SHIZUKU,
                    onGrant = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText(testTitle)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(testMessage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun permissionDialog_Shizuku_显示授权步骤() {
        composeTestRule.setContent {
            AutoGLMTheme {
                PermissionDialog(
                    title = "测试标题",
                    message = "测试消息",
                    permissionType = PermissionType.SHIZUKU,
                    onGrant = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("授权步骤：")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("去授权")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun permissionDialog_ADBKeyboard_显示安装步骤() {
        composeTestRule.setContent {
            AutoGLMTheme {
                PermissionDialog(
                    title = "测试标题",
                    message = "测试消息",
                    permissionType = PermissionType.ADB_KEYBOARD,
                    onGrant = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("安装步骤：")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("去安装")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun permissionDialog_授权按钮可点击() {
        var granted = false

        composeTestRule.setContent {
            AutoGLMTheme {
                PermissionDialog(
                    title = "测试标题",
                    message = "测试消息",
                    permissionType = PermissionType.SHIZUKU,
                    onGrant = { granted = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("去授权")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(granted)
    }

    @Test
    fun permissionDialog_稍后按钮可点击() {
        var dismissed = false

        composeTestRule.setContent {
            AutoGLMTheme {
                PermissionDialog(
                    title = "测试标题",
                    message = "测试消息",
                    permissionType = PermissionType.SHIZUKU,
                    onGrant = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("稍后")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        assert(dismissed)
    }

    // ==================== 对话框主题测试 ====================

    @Test
    fun confirmDialog_在暗色主题下正确显示() {
        composeTestRule.setContent {
            AutoGLMTheme(themeMode = com.example.autoglm.ui.theme.ThemeMode.DARK) {
                ConfirmDialog(
                    message = "测试消息",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("确认操作")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun takeOverDialog_在暗色主题下正确显示() {
        composeTestRule.setContent {
            AutoGLMTheme(themeMode = com.example.autoglm.ui.theme.ThemeMode.DARK) {
                TakeOverDialog(
                    message = "测试消息",
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("需要人工介入")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun permissionDialog_在暗色主题下正确显示() {
        composeTestRule.setContent {
            AutoGLMTheme(themeMode = com.example.autoglm.ui.theme.ThemeMode.DARK) {
                PermissionDialog(
                    title = "测试标题",
                    message = "测试消息",
                    permissionType = PermissionType.SHIZUKU,
                    onGrant = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("测试标题")
            .assertExists()
            .assertIsDisplayed()
    }
}
