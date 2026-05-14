# GitHub 上传说明

## 快速开始

由于需要 GitHub 凭证认证，请在本地终端执行以下命令：

### 步骤 1: 配置 Git 用户信息

```bash
git config --global user.name "你的GitHub用户名"
git config --global user.name "你的GitHub邮箱"
```

### 步骤 2: 添加所有文件

```bash
git add .
```

### 步骤 3: 提交代码

```bash
git commit -m "Initial commit: 倒数日纪念日应用 v1.0"
```

### 步骤 4: 创建 GitHub 仓库

1. 访问 https://github.com/new
2. Repository name: `CountdownDay`
3. **不要勾选** "Initialize this repository with a README"
4. 点击 "Create repository"

### 步骤 5: 推送代码

复制你在 GitHub 创建仓库时显示的命令，或者使用以下命令：

```bash
git remote add origin https://github.com/你的用户名/CountdownDay.git
git branch -M main
git push -u origin main
```

## 或者使用 Token（更安全）

如果你有 GitHub Personal Access Token：

```bash
# 先在 GitHub 创建仓库（步骤 4）
# 然后使用 Token 推送

git remote add origin https://github.com/你的用户名/CountdownDay.git
git remote set-url origin https://你的TOKEN@github.com/你的用户名/CountdownDay.git
git branch -M main
git push -u origin main
```

## 生成 GitHub Token

1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 选择权限：
   - ✅ repo (Full control of private repositories)
4. 生成 token 并保存（只显示一次！）

## 验证上传成功

```bash
# 查看远程仓库
git remote -v

# 应该显示：
# origin  https://github.com/你的用户名/CountdownDay.git (fetch)
# origin  https://github.com/你的用户名/CountdownDay.git (push)
```

然后访问 https://github.com/你的用户名/CountdownDay 查看你的项目！

## 遇到问题？

### 问题 1: 仓库已存在
如果 GitHub 上已有同名仓库，使用不同的名字：
```bash
git remote add origin https://github.com/你的用户名/CountdownDay-Android.git
```

### 问题 2: 认证失败
确保：
- 用户名和仓库名正确
- Token 有 repo 权限
- Token 没有过期

### 问题 3: 推送被拒绝
可能需要先拉取：
```bash
git pull origin main --allow-unrelated-histories
```

## 恭喜！🎉

上传成功后，别忘了：
- ⭐ 给项目点个 Star
- 📝 更新 README 中的用户名
- 📱 使用 Android Studio 打开并运行应用

## 后续开发

后续更新代码时：
```bash
git add .
git commit -m "你的更新说明"
git push
```

Happy coding! 🚀
