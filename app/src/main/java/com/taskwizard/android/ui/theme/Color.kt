package com.taskwizard.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Material3 颜色系统
 *
 * 基于Material Design 3规范生成的完整颜色系统
 * 种子颜色: #1976D2 (蓝色)
 *
 * 颜色系统包括：
 * - Primary: 主要操作和强调
 * - Secondary: 次要操作和辅助
 * - Tertiary: 对比和强调
 * - Error: 错误状态
 * - Surface: 背景和表面
 * - Surface Container: 容器层级系统
 */

// ==================== 亮色主题 (Light Theme) ====================

// Primary - 主色调（蓝色系）
val md_theme_light_primary = Color(0xFF1976D2)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFBBDEFB)
val md_theme_light_onPrimaryContainer = Color(0xFF0D47A1)

// Secondary - 次要色调（灰色系）
val md_theme_light_secondary = Color(0xFF424242)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE0E0E0)
val md_theme_light_onSecondaryContainer = Color(0xFF212121)

// Tertiary - 第三色调（青色系）
val md_theme_light_tertiary = Color(0xFF00796B)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFB2DFDB)
val md_theme_light_onTertiaryContainer = Color(0xFF004D40)

// Error - 错误色调
val md_theme_light_error = Color(0xFFB3261E)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFF9DEDC)
val md_theme_light_onErrorContainer = Color(0xFF410E0B)

// Background - 背景色
val md_theme_light_background = Color(0xFFFFFBFE)
val md_theme_light_onBackground = Color(0xFF1C1B1F)

// Surface - 表面色（基础层）
val md_theme_light_surface = Color(0xFFFFFBFE)
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)

// Surface Container - 容器层级系统（Material3新增）
val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xFFF7F2FA)
val md_theme_light_surfaceContainer = Color(0xFFF3EDF7)
val md_theme_light_surfaceContainerHigh = Color(0xFFECE6F0)
val md_theme_light_surfaceContainerHighest = Color(0xFFE6E0E9)

// Outline - 轮廓和分隔线
val md_theme_light_outline = Color(0xFF79747E)
val md_theme_light_outlineVariant = Color(0xFFCAC4D0)

// Inverse - 反色（用于Snackbar等）
val md_theme_light_inverseSurface = Color(0xFF313033)
val md_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
val md_theme_light_inversePrimary = Color(0xFF64B5F6)

// Scrim - 遮罩层
val md_theme_light_scrim = Color(0xFF000000)

// ==================== 暗色主题 (Dark Theme) ====================

// Primary - 主色调（亮蓝色系）
val md_theme_dark_primary = Color(0xFF64B5F6)
val md_theme_dark_onPrimary = Color(0xFF0D47A1)
val md_theme_dark_primaryContainer = Color(0xFF1565C0)
val md_theme_dark_onPrimaryContainer = Color(0xFFBBDEFB)

// Secondary - 次要色调（亮灰色系）
val md_theme_dark_secondary = Color(0xFFBDBDBD)
val md_theme_dark_onSecondary = Color(0xFF212121)
val md_theme_dark_secondaryContainer = Color(0xFF424242)
val md_theme_dark_onSecondaryContainer = Color(0xFFE0E0E0)

// Tertiary - 第三色调（亮青色系）
val md_theme_dark_tertiary = Color(0xFF4DB6AC)
val md_theme_dark_onTertiary = Color(0xFF004D40)
val md_theme_dark_tertiaryContainer = Color(0xFF00695C)
val md_theme_dark_onTertiaryContainer = Color(0xFFB2DFDB)

// Error - 错误色调
val md_theme_dark_error = Color(0xFFF2B8B5)
val md_theme_dark_onError = Color(0xFF601410)
val md_theme_dark_errorContainer = Color(0xFF8C1D18)
val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)

// Background - 背景色
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = Color(0xFFE6E1E5)

// Surface - 表面色（基础层）
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE6E1E5)
val md_theme_dark_surfaceVariant = Color(0xFF49454F)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)

// Surface Container - 容器层级系统（Material3新增）
val md_theme_dark_surfaceContainerLowest = Color(0xFF0F0D13)
val md_theme_dark_surfaceContainerLow = Color(0xFF1D1B20)
val md_theme_dark_surfaceContainer = Color(0xFF211F26)
val md_theme_dark_surfaceContainerHigh = Color(0xFF2B2930)
val md_theme_dark_surfaceContainerHighest = Color(0xFF36343B)

// Outline - 轮廓和分隔线
val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_outlineVariant = Color(0xFF49454F)

// Inverse - 反色（用于Snackbar等）
val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
val md_theme_dark_inverseOnSurface = Color(0xFF313033)
val md_theme_dark_inversePrimary = Color(0xFF1976D2)

// Scrim - 遮罩层
val md_theme_dark_scrim = Color(0xFF000000)

// ==================== Pure Black主题 (OLED优化) ====================

// Pure Black主题继承暗色主题的所有颜色，只修改背景和表面相关颜色
// 这样可以在OLED屏幕上节省电量，并提供更深邃的视觉效果

// Background - 纯黑背景
val md_theme_pure_black_background = Color(0xFF000000)

// Surface - 纯黑表面
val md_theme_pure_black_surface = Color(0xFF000000)

// Surface Container - 容器层级系统（Pure Black版本）
val md_theme_pure_black_surfaceContainerLowest = Color(0xFF000000)
val md_theme_pure_black_surfaceContainerLow = Color(0xFF0A0A0A)
val md_theme_pure_black_surfaceContainer = Color(0xFF121212)
val md_theme_pure_black_surfaceContainerHigh = Color(0xFF1A1A1A)
val md_theme_pure_black_surfaceContainerHighest = Color(0xFF222222)

// Inverse Surface - Pure Black版本
val md_theme_pure_black_inverseSurface = Color(0xFFE6E1E5)
val md_theme_pure_black_inverseOnSurface = Color(0xFF000000)
