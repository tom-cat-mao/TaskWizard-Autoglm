package com.example.autoglm.data

import com.example.autoglm.ui.theme.ThemeMode

/**
 * 应用全局状态
 * 使用单一数据源模式，所有UI状态集中管理
 */
data class AppState(
    // ==================== 配置相关 ====================
    val apiKey: String = "",
    val baseUrl: String = "",
    val model: String = "autoglm-phone",

    // ==================== 主题相关 ====================
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val pureBlackEnabled: Boolean = false,

    // ==================== 任务相关 ====================
    val isRunning: Boolean = false,
    val currentTask: String = "",
    val messages: List<MessageItem> = emptyList(),

    // ==================== 系统状态 ====================
    val hasShizukuPermission: Boolean = false,
    val isADBKeyboardInstalled: Boolean = false,
    val isADBKeyboardEnabled: Boolean = false,

    // ==================== UI状态 ====================
    val isSettingsOpen: Boolean = false
)
