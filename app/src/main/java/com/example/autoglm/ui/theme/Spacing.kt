package com.example.autoglm.ui.theme

import androidx.compose.ui.unit.dp

/**
 * 统一的间距系统
 *
 * 遵循Material Design的8dp网格系统
 * 所有间距都是8的倍数，确保视觉一致性
 */
object Spacing {
    // 基础间距单位
    val none = 0.dp
    val extraSmall = 4.dp    // 0.5x
    val small = 8.dp         // 1x - 基础单位
    val medium = 12.dp       // 1.5x
    val normal = 16.dp       // 2x - 标准间距
    val large = 24.dp        // 3x
    val extraLarge = 32.dp   // 4x
    val huge = 48.dp         // 6x

    // 常用组合间距
    val cardPadding = normal          // Card内边距: 16dp
    val screenPadding = normal        // 屏幕边距: 16dp
    val itemSpacing = medium          // 列表项间距: 12dp
    val sectionSpacing = large        // 区块间距: 24dp
    val buttonSpacing = small         // 按钮内部间距: 8dp
}
