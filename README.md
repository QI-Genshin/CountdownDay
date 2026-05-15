# Shiguang Countdown Day

一个简洁美观的倒数日纪念日 Android 应用，使用 Kotlin 和 Jetpack Compose 开发。

## 功能特点

- 🎨 **精美设计**：淡蓝色主色调，低饱和配色，圆角设计
- 📅 **事件管理**：记录生日、纪念日、考试、节假日等重要日子
- 🔍 **分类筛选**：支持按分类筛选事件
- ⭐ **置顶功能**：重要事件置顶显示
- 🔒 **隐私加密**：私密事件密码保护
- 🌙 **深色模式**：支持浅色/深色主题切换
- 📱 **桌面小组件**：支持添加到桌面小组件（设计实现）

## 技术栈

- **Kotlin** - 开发语言
- **Jetpack Compose** - UI 框架
- **Material Design 3** - 设计规范
- **MVVM** - 架构模式
- **Jetpack Navigation** - 导航（可选）
- **GitHub Actions** - CI/CD 自动化构建

## 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34

### 构建

```bash
# 克隆项目
git clone https://github.com/QI-Genshin/CountdownDay.git

# 进入项目目录
cd CountdownDay

# 使用 Gradle 构建
./gradlew assembleDebug

# 或使用 Android Studio 打开项目
```

### APK 下载

每次推送到 main 分支都会自动构建 APK。可以在 GitHub Actions 中查看构建结果并下载 APK。

## 项目结构

```
CountdownDay/
├── app/
│   ├── src/main/
│   │   ├── java/com/countdownday/
│   │   │   ├── MainActivity.kt          # 主界面
│   │   │   ├── model/
│   │   │   │   └── Models.kt            # 数据模型
│   │   │   └── ui/theme/
│   │   │       ├── Color.kt             # 颜色配置
│   │   │       ├── Theme.kt             # 主题配置
│   │   │       └── Type.kt              # 字体配置
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle
├── gradle.properties
└── .github/workflows/build.yml           # GitHub Actions 配置
```

## 设计规范

### 颜色系统

- **主色调**：淡蓝 `#E0EDFF`
- **辅助色**：米白、浅粉、浅绿、浅紫
- **深色模式**：完整适配

### 字体规范

- **标题**：18sp，加粗
- **正文**：15sp
- **辅助小字**：12sp
- **超大数字**：45sp，加粗

### 圆角规范

- **全局控件圆角**：14px
- **卡片圆角**：18px
- **按钮圆角**：25px

## 页面说明

### 1. 首页
- 事件列表展示
- 分类标签筛选
- 置顶事件优先显示
- 私密事件模糊遮盖

### 2. 新建/编辑
- 事件名称、日期、分类
- 备注信息
- 置顶、私密、小组件开关
- 卡片背景色选择

### 3. 分类管理
- 系统默认分类：生日、纪念日、考试、生活
- 支持添加自定义分类
- 颜色自定义

### 4. 事件详情
- 全屏渐变背景
- 超大天数展示
- 编辑、置顶、加密、小组件快捷操作

### 5. 个人中心
- 主题模式切换
- 桌面小组件设置
- 隐私密码锁
- 数据备份与恢复
- 清除缓存
- 关于我们

## License

MIT License
