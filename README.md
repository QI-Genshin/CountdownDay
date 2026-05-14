# CountdownDay - 倒数日纪念日应用

## 项目概述

一个简洁美观的 Android 应用，用于记录和跟踪重要日期。显示距离目标日期的天数，支持添加、编辑和删除纪念日。

## 功能特性

✅ **添加纪念日** - 创建新的纪念日，设置名称和日期
✅ **倒计时显示** - 实时显示距离目标日期的天数
✅ **纪念日列表** - 展示所有纪念日，美观的卡片式设计
✅ **Material Design** - 使用 Material 3 设计风格，响应式界面
✅ **搜索功能** - 快速查找纪念日
✅ **颜色主题** - 支持多种颜色主题选择

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **最低SDK**: API 26 (Android 8.0)
- **目标SDK**: API 34 (Android 14)
- **架构**: MVVM (简化版本)

## 项目结构

```
/app
  src/main/
    java/com/countdownday/
      CountdownDayApp.kt     # Application入口
      MainActivity.kt         # 主Activity和UI实现
    res/
      values/                 # 资源文件
```

## 快速开始

### 方法一：使用 Android Studio（推荐）

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开 Android Studio，选择 "Open an Existing Project"
3. 选择本项目根目录
4. 等待 Gradle 同步完成（首次同步可能需要几分钟）
5. 连接 Android 设备或启动模拟器
6. 点击运行按钮 (▶️)

### 方法二：命令行构建

确保已安装 JDK 17 或更高版本，然后执行：

```bash
# 克隆项目
cd CountdownDay

# 构建 Debug 版本
./gradlew assembleDebug

# APK 将生成在 app/build/outputs/apk/debug/
```

## 使用说明

### 添加纪念日

1. 点击右下角的 Floating Action Button (+)
2. 输入纪念日名称
3. 选择日期
4. 点击保存

### 查看倒计时

每个纪念日卡片都会显示：
- 纪念日名称
- 日期
- 距离该日期的天数（倒计时或已过天数）

## 开发说明

### 核心类

- **MainActivity**: 包含所有 UI 和逻辑实现
- **Anniversary**: 数据类，代表纪念日

### 主题配置

主题颜色在 `ui/theme/Color.kt` 中配置，使用温暖的红色调为主色调。

## 版本信息

- **版本号**: 1.0
- **版本名称**: 1.0
- **更新日期**: 2026-05-13

## 作者

由 AI 助手开发

## 许可证

本项目仅供学习和参考使用。
