# LocalWriter - 安卓本地写作APP

一个功能完整的安卓本地写作应用，支持多文档管理、导出、深色模式和查找功能。

## ✅ 已实现功能

### 核心功能
- ✍️ **多文档管理** - 支持新建、打开、删除、重命名多个文档
- 🔤 **字体大小调整** - 支持10sp-30sp范围调整
- 📏 **行间距设置** - 支持1.0x-2.5x行间距调整
- 📊 **实时字数统计** - 显示字符数、字数和行数
- 💾 **自动保存** - 自动保存文本内容和所有设置
- 🎨 **Material Design** - 采用Material Design设计风格

### 新增功能
- 📂 **多文档管理** - SQLite数据库存储，支持多个文档
- 📤 **导出功能** - 支持导出为TXT格式（PDF导出需要添加iText库）
- 🌙 **深色模式** - 支持浅色/深色主题切换
- 🔍 **查找功能** - 支持在文档中查找文本
- ⌨️ **输入法支持** - 完全支持手机输入法（系统自动调用）

## 技术栈

- **语言**：Kotlin
- **最低SDK**：API 21 (Android 5.0)
- **目标SDK**：API 34 (Android 14)
- **数据库**：SQLite（通过SQLiteOpenHelper）
- **架构**：多Activity应用
- **UI组件**：Material Design Components

## 项目结构

```
LocalWriter/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/localwriter/
│   │   │   ├── MainActivity.kt          # 主编辑界面
│   │   │   ├── DocumentListActivity.kt  # 文档列表界面
│   │   │   ├── DocumentAdapter.kt      # 文档列表适配器
│   │   │   └── DatabaseHelper.kt       # 数据库帮助类
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml        # 主界面布局
│   │   │   │   ├── activity_document_list.xml # 文档列表布局
│   │   │   │   ├── item_document.xml        # 文档项布局
│   │   │   │   ├── dialog_new_document.xml  # 新建/重命名对话框
│   │   │   │   └── dialog_search.xml        # 查找对话框
│   │   │   ├── menu/
│   │   │   │   ├── main_menu.xml      # 主界面菜单
│   │   │   │   └── document_menu.xml # 文档上下文菜单
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       └── file_paths.xml     # FileProvider配置
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## 使用方法

### 1. 在Android Studio中打开项目

1. 打开Android Studio
2. 选择 "Open an Existing Project"
3. 选择项目根目录 `D:\WorkBuddy\2026-06-03-10-08-26`
4. 等待Gradle同步完成

### 2. 运行应用

1. 连接安卓设备或启动模拟器
2. 点击 "Run" 按钮
3. 应用将安装并启动

### 3. 使用功能

#### 文档管理
- **新建文档** - 点击右下角"+"按钮
- **打开文档** - 在文档列表中点击文档
- **删除文档** - 长按文档，选择"删除"
- **重命名文档** - 长按文档，选择"重命名"

#### 编辑功能
- **写作** - 直接在编辑区域输入文本
- **调整字体** - 使用底部滑块调整字体大小（10-30sp）
- **调整行间距** - 使用底部滑块调整行间距（1.0x-2.5x）
- **查看统计** - 底部状态栏显示字符数、字数和行数

#### 其他功能
- **查找文本** - 点击右上角菜单，选择"查找"
- **导出TXT** - 点击右上角菜单，选择"导出为TXT"
- **深色模式** - 点击右上角菜单，选择"深色模式"
- **文档列表** - 点击右上角菜单，选择"文档列表"

## 数据存储

- **文档数据** - 使用SQLite数据库存储（路径：`data/data/com.example.localwriter/databases/localwriter.db`）
- **设置数据** - 使用SharedPreferences存储

## 构建要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- Gradle 8.2或更高版本
- Kotlin 1.9.0或更高版本

## 注意事项

- 本应用完全在本地运行，不联网，保护您的隐私
- 所有数据自动保存，无需手动保存
- 支持Android 5.0及以上版本
- TXT导出功能已完整实现，PDF导出需要添加iText库

## 未来改进计划

- [ ] 完整实现PDF导出功能（添加iText库）
- [ ] 支持Markdown语法高亮
- [ ] 添加更多导出格式（DOCX、HTML等）
- [ ] 支持文档分类和标签
- [ ] 添加语音输入功能
- [ ] 支持云同步（可选）

## 在线打包（推荐）

本项目支持通过 GitHub Actions 自动构建 APK，无需本地配置开发环境。

### 快速开始

1. **创建 GitHub 仓库**
   - 将项目代码上传到 GitHub（推荐使用 GitHub Desktop 或 git 命令行）

2. **启用 GitHub Actions**
   - 推送代码后，`.github/workflows/android.yml` 将自动生效
   - 首次推送会触发构建，稍等几分钟后即可下载 APK

3. **下载 APK**
   - 进入仓库的 **Actions** 页面
   - 选择最新的 workflow run
   - 下载 **debug-apk** artifacts 中的 APK 文件

### 详细指南

请参考 [GitHub打包指南.md](GitHub打包指南.md) 获取完整的图文教程。

### Release 版本打包（如需发布到应用市场）

1. **配置签名密钥**
   - 在本地生成 keystore 文件：
     ```bash
     keytool -genkey -v -keystore release.keystore -alias mykey -keyalg RSA -keysize 2048 -validity 10000
     ```
   - 将 keystore 文件转换为 base64：
     ```bash
     base64 release.keystore > release_keystore_base64.txt
     ```
   - 在 GitHub 仓库 Settings → Secrets 中添加：
     - `KEYSTORE_BASE64`: base64 编码的 keystore 内容
     - `KEYSTORE_PASSWORD`: keystore 密码
     - `KEY_ALIAS`: 密钥别名
     - `KEY_PASSWORD`: 密钥密码

2. **触发 Release 构建**
   - 推送到 main 分支将自动触发 signed release 构建

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎反馈。
