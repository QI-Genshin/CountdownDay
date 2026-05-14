package com.countdownday.ui.theme

import androidx.compose.ui.graphics.Color

// 主色调：淡蓝
val PrimaryLight = Color(0xE0EDFF)
val Primary = Color(0xFFB8D4E8)
val PrimaryDark = Color(0xFF9BBFD6)

// 辅助色
val SecondaryPink = Color(0xFFFFE6EA)
val SecondaryGreen = Color(0xFFE8F5E9)
val SecondaryPurple = Color(0xFFF3E5F5)
val SecondaryOrange = Color(0xFFFFF3E0)

// 基础色 - 浅色模式
val Background = Color(0xFFF8F9FA)
val Surface = Color(0xFFFFFFFF)
val OnPrimary = Color(0xFF2D3748)
val OnBackground = Color(0xFF2D3748)
val OnSurface = Color(0xFF4A5568)
val OnSurfaceVariant = Color(0xFF718096)

// 深色模式基础色
val BackgroundDark = Color(0xFF1A202C)
val SurfaceDark = Color(0xFF2D3748)
val SurfaceVariantDark = Color(0xFF3D4758)
val PrimaryDarkMode = Color(0xFF63B3ED)
val PrimaryDarkModeLight = Color(0xFF4A5568)
val PrimaryDarkModeDark = Color(0xFF2C5282)
val OnPrimaryDark = Color(0xFFFFFFFF)
val OnBackgroundDark = Color(0xFFE2E8F0)
val OnSurfaceDark = Color(0xFFCBD5E0)
val OnSurfaceVariantDark = Color(0xFFA0AEC0)

// 深色模式辅助色
val SecondaryPinkDark = Color(0xFF7B3453)
val SecondaryGreenDark = Color(0xFF276749)
val SecondaryPurpleDark = Color(0xFF553C9A)
val SecondaryOrangeDark = Color(0xFF7C2D12)

// 卡片渐变背景色（浅色模式）
val GradientColors = listOf(
    listOf(Color(0xE0EDFF), Color(0xFFF0F7FF)), // 淡蓝渐变
    listOf(Color(0xFFFFE6EA), Color(0xFFFFF0F3)), // 浅粉渐变
    listOf(Color(0xFFE8F5E9), Color(0xFFF1F8F2)), // 浅绿渐变
    listOf(Color(0xFFF3E5F5), Color(0xFFF9F0FA)), // 浅紫渐变
    listOf(Color(0xFFFFF3E0), Color(0xFFFFF8E8))  // 浅橙渐变
)

// 卡片渐变背景色（深色模式）
val GradientColorsDark = listOf(
    listOf(Color(0xFF2A4365), Color(0xFF1A365D)), // 深蓝渐变
    listOf(Color(0xFF742A64), Color(0xFF553C9A)), // 深紫渐变
    listOf(Color(0xFF22543D), Color(0xFF276749)), // 深绿渐变
    listOf(Color(0xFF434193), Color(0xFF322659)), // 深紫蓝渐变
    listOf(Color(0xFF6B3419), Color(0xFF7C2D12))  // 深橙渐变
)

// 分类颜色
val CategoryColors = listOf(
    Color(0xFF81D4FA),
    Color(0xFFFFAB91),
    Color(0xFFA5D6A7),
    Color(0xFFCE93D8),
    Color(0xFFFFCC80),
    Color(0xFF90CAF9)
)

// 分类颜色（深色模式）
val CategoryColorsDark = listOf(
    Color(0xFF4299E1),
    Color(0xFFFC8181),
    Color(0xFF68D391),
    Color(0xFFB794F4),
    Color(0xFFF6AD55),
    Color(0xFF63B3ED)
)
