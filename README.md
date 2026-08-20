# UnicodeIME

一个轻量的 Android Unicode 输入法，用于直接输入 Unicode 码点（十六进制）并实时预览对应字符。

## 功能特点

- **十六进制码点输入** — 通过虚拟键盘输入 Unicode 十六进制代码点（如 `4F60` → 你，`1F600` → 😀）
- **实时预览** — 键盘上方区域实时显示当前输入的码点及解析后的字符
- **BMP + SMP 全平面支持** — 支持基本多语言平面之外的字符（Emoji、罕用汉字等）
- **按键振动反馈** — 每次按键触发短振动（17ms）
- **边到边显示** — 适配系统导航栏，使用 WindowInsetsCompat 处理安全区域
- **Compose 设置引导** — 启动后提供启用输入法 / 切换输入法的分步引导界面

## 键盘布局

```
[ 1 ] [ 2 ] [ 3 ] [ A ] [ B ]
[ 4 ] [ 5 ] [ 6 ] [ C ] [ D ]
[ 7 ] [ 8 ] [ 9 ] [ E ] [ F ]
[ , ] [ 0 ] [ + ] [ENT] [DEL]
```

- `0-9`, `A-F` — 输入十六进制字符
- `,` — 分隔符
- `+` — 可选前缀（如 `U+`）
- `ENT` — 提交：尝试将已输入文本解析为 Unicode 字符并提交，解析失败则原样提交
- `DEL` — 退格（可长按连续删除）

## 输入格式

解析器支持以下格式（会自动忽略空格、下划线、短横线、`u`/`U+` 前缀）：

| 输入 | 解析结果 |
|------|----------|
| `4f60` | 你 |
| `U+4F60` | 你 |
| `1F600` | 😀 |
| `u 1f4a9` | 💩 |

## 系统要求

- Android 10+（API 29，`minSdk`）
- `targetSdk` / `compileSdk` = 36

## 构建

```bash
# 克隆仓库
git clone https://github.com/Langeskov/UnicodeIME.git
cd UnicodeIME

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需配置签名）
./gradlew assembleRelease
```

构建产物：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/release/app-release.apk`

## 安装

可以直接安装 `app/release/app-release.apk`（已包含签名的 Release 包）。

安装后：
1. 打开 **设置 → 系统 → 语言与输入法 → 虚拟键盘 → 管理键盘**
2. 启用 **UnicodeIME**
3. 在任意文本输入框中切换至 UnicodeIME 即可使用

## 项目结构

```
UnicodeIME/
├── app/src/main/
│   ├── java/com/example/unicodeime/
│   │   ├── MainActivity.kt       # Compose 引导界面（启用 / 切换输入法）
│   │   └── UnicodeIME.kt         # InputMethodService 实现（核心输入逻辑）
│   ├── res/
│   │   ├── layout/
│   │   │   ├── keyboard_view.xml     # 键盘 + 预览区整体布局
│   │   │   ├── keyboard_preview.xml  # 按键点击预览
│   │   │   └── keyboard_popup.xml    # 长按弹出
│   │   ├── xml/
│   │   │   ├── unicode_keyboard.xml  # 键盘按键定义（4行×5列）
│   │   │   └── method.xml            # 输入法元数据声明
│   │   └── drawable/
│   │       └── key_background.xml    # 按键背景样式
│   └── AndroidManifest.xml
├── app/release/
│   └── app-release.apk           # 已签名的 Release 包（可直接安装）
├── build.gradle.kts              # 根构建脚本
├── gradle/libs.versions.toml     # 版本目录（AGP 9.0.1 / Kotlin 2.2.10）
└── settings.gradle.kts
```

## 技术栈

- **Kotlin** + **Android InputMethodService** 框架
- **Jetpack Compose**（设置引导界面）
- **Material 3** 主题
- **AGP 9.0.1** / **Kotlin 2.2.10** / **Gradle 8.13**

## 许可证

本项目仅供学习和个人使用。

---

*作者：十二水磷酸二钠*
