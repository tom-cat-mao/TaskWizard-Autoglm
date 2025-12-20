package com.example.autoglm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material3 形状系统
 *
 * 定义应用中使用的所有圆角形状
 */
val Shapes = Shapes(
    // Extra Small - 4dp（指示器、小标签）
    extraSmall = RoundedCornerShape(4.dp),

    // Small - 8dp（Chip、小按钮）
    small = RoundedCornerShape(8.dp),

    // Medium - 12dp（Button、TextField）
    medium = RoundedCornerShape(12.dp),

    // Large - 16dp（Card、大组件）
    large = RoundedCornerShape(16.dp),

    // Extra Large - 28dp（Dialog、BottomSheet）
    extraLarge = RoundedCornerShape(28.dp)
)
