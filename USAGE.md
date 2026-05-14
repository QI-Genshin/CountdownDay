# 使用指南

## 注意事项

当前开发环境存在网络连接限制，无法通过命令行完整下载依赖。建议使用 **Android Studio** 进行构建。

## 使用 Android Studio 构建

### 1. 安装 Android Studio
- 从 [developer.android.com/studio](https://developer.android.com/studio) 下载并安装 Android Studio
- 首次启动时会引导您安装 Android SDK 等必要组件

### 2. 打开项目
- 启动 Android Studio
- 选择 "Open an Existing Project" (打开现有项目)
- 选择本项目的根目录 `CountdownDay` 文件夹
- 等待 Gradle 同步完成（可能需要几分钟）

### 3. 配置镜像（如果网络较慢）
如果同步过程中遇到网络问题，可以在 Android Studio 中配置国内镜像：
1. 打开项目根目录下的 `settings.gradle` 文件
2. 在 `repositories` 部分确保配置了国内镜像源
3. 或者，在 Android Studio 的设置中配置 Gradle 使用国内镜像

### 4. 运行项目
- 连接 Android 手机（确保开启 USB 调试）或启动 Android 模拟器
- 点击工具栏中的绿色运行按钮 (▶️)
- 选择目标设备
- 应用将自动安装并启动

## 项目特点

### 功能
- 📅 纪念日管理 - 添加、查看、删除纪念日
- ⏱️ 倒计时显示 - 实时显示距离日期的天数
- 🎨 美观界面 - Material Design 3 设计风格
- 📱 响应式布局 - 适配不同屏幕尺寸

### 技术亮点
- Jetpack Compose - 现代声明式 UI 框架
- Kotlin - 现代、简洁的编程语言
- Material 3 - 最新的 Material Design 设计系统

## 代码结构

主要文件：
- [MainActivity.kt](file:///workspace/app/src/main/java/com/countdownday/MainActivity.kt) - 主界面和业务逻辑
- [build.gradle.kts](file:///workspace/app/build.gradle.kts) - 应用配置和依赖

## 下一步（完整功能）

当前版本包含简化实现，完整版本包含：
- Room 数据库持久化
- Hilt 依赖注入
- 完整的编辑和删除功能
- 更高级的搜索和筛选

可以根据需要逐步添加这些功能！
