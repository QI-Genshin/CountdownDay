package com.countdownday.model

import androidx.compose.ui.graphics.Color
import com.countdownday.ui.theme.CategoryColors
import com.countdownday.ui.theme.GradientColors
import com.countdownday.ui.theme.GradientColorsDark
import java.time.LocalDate

// 分类数据类
data class Category(
    val id: Long,
    val name: String,
    val color: Color,
    val isSystem: Boolean = false
)

// 事件数据类
data class Event(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val categoryId: Long,
    val note: String = "",
    val gradientColors: List<Color> = GradientColors[0],
    val gradientColorsDark: List<Color> = GradientColorsDark[0],
    val isPinned: Boolean = false,
    val isPrivate: Boolean = false,
    val isWidget: Boolean = false,
    val createTime: Long = System.currentTimeMillis()
)

// 页面枚举
enum class Screen {
    HOME, NEW, CATEGORY, MINE
}

// 主题模式
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

// 默认分类
val defaultCategories = listOf(
    Category(1, "生日", CategoryColors[0], isSystem = true),
    Category(2, "纪念日", CategoryColors[1], isSystem = true),
    Category(3, "考试", CategoryColors[2], isSystem = true),
    Category(4, "生活", CategoryColors[3], isSystem = true)
)

// 示例数据
val sampleEvents = listOf(
    Event(
        id = 1,
        name = "生日聚会",
        date = LocalDate.of(2026, 6, 15),
        categoryId = 1,
        note = "邀请好朋友们来家里吃饭",
        gradientColors = GradientColors[0],
        gradientColorsDark = GradientColorsDark[0],
        isPinned = true
    ),
    Event(
        id = 2,
        name = "结婚纪念日",
        date = LocalDate.of(2026, 5, 20),
        categoryId = 2,
        note = "一起去看海",
        gradientColors = GradientColors[1],
        gradientColorsDark = GradientColorsDark[1],
        isPinned = false
    ),
    Event(
        id = 3,
        name = "期末考试",
        date = LocalDate.of(2026, 7, 1),
        categoryId = 3,
        note = "好好复习，争取考个好成绩",
        gradientColors = GradientColors[2],
        gradientColorsDark = GradientColorsDark[2],
        isPinned = false
    )
)
