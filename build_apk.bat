@echo off
echo ===================================
echo LocalWriter APK构建脚本
echo ===================================

set PROJECT_DIR=D:\WorkBuddy\2026-06-03-10-08-26
set APK_OUTPUT=%PROJECT_DIR%\app\build\outputs\apk\debug\app-debug.apk

echo.
echo [1/4] 检查项目目录...
if not exist "%PROJECT_DIR%" (
    echo 错误：项目目录不存在！
    pause
    exit /b 1
)
echo 项目目录：%PROJECT_DIR%

echo.
echo [2/4] 检查Android SDK...
if not exist "%ANDROID_HOME%" (
    if not exist "%ANDROID_SDK_ROOT%" (
        echo 警告：未找到Android SDK环境变量
        echo 请先安装Android Studio
        echo.
        echo 是否继续？(需要手动指定SDK路径)
        pause
    )
)

echo.
echo [3/4] 开始构建APK...
cd /d "%PROJECT_DIR%"

REM 尝试使用gradlew构建
if exist "gradlew.bat" (
    echo 使用Gradle Wrapper构建...
    call gradlew.bat assembleDebug
) else (
    echo 错误：未找到gradlew.bat
    echo 请在Android Studio中打开项目并构建
    pause
    exit /b 1
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo 构建失败！请检查：
    echo 1. Android SDK是否已安装
    echo 2. local.properties中的sdk.dir是否正确
    echo 3. 是否在Android Studio中同步过项目
    pause
    exit /b 1
)

echo.
echo [4/4] 构建成功！
echo.
echo APK文件位置：
echo %APK_OUTPUT%
echo.
echo 是否打开输出目录？(Y/N)
set /p OPEN_DIR=
if /i "%OPEN_DIR%"=="Y" (
    explorer "%PROJECT_DIR%\app\build\outputs\apk\debug"
)

echo.
echo ===================================
echo 构建完成！
echo ===================================
pause
