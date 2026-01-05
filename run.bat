@echo off
setlocal

:: =================== 核心配置区域 (只改这里) ===================

:: 1. 【Maven 设置】填入 Maven 的 bin 目录路径
set "CUSTOM_MAVEN_BIN=E:\env\maven\apache-maven-3.9.11\bin"

:: 2. 【JDK 设置】填入 JDK 21 的安装根目录
set "CUSTOM_JAVA_HOME=E:\env\jdk\graalvm-community-openjdk-21.0.2+13.1"

:: 3. Jar 包名称
set "JAR_NAME=nekton-0.0.1-SNAPSHOT.jar"

:: 4. 解压目录名
set "EXTRACT_DIR=target\nekton"

:: 5. 【环境设置】指定激活的 Profile (对应 application-dev.yml 等)
::    如果你想用 @profiles.active@ 这种 Maven 占位符的方式，
::    通常是在 pom.xml 资源过滤时替换的。
::    但在运行脚本里，直接指定参数是最稳妥的，优先级最高。
set "ACTIVE_PROFILE=dev"

:: ===============================================================

echo [INFO] 正在配置临时环境变量...

:: 1. 临时设置 JAVA_HOME
set "JAVA_HOME=%CUSTOM_JAVA_HOME%"

:: 2. 将指定的 JDK 和 Maven 加入临时 Path
set "Path=%CUSTOM_JAVA_HOME%\bin;%CUSTOM_MAVEN_BIN%;%Path%"

echo [INFO] ---------------------------------------------
echo [INFO] 当前使用的 Java 版本:
java -version
echo [INFO] ---------------------------------------------
echo [INFO] 当前使用的 Maven 版本:
call mvn -version
echo [INFO] ---------------------------------------------

echo.
echo [INFO] 开始构建项目...
:: 如果你在 pom.xml 里配置了资源过滤来替换 @profiles.active@，
:: 这一步 mvn package 会自动处理。
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo [ERROR] Maven 构建失败！请检查上方显示的 Java/Maven 版本是否正确。
    pause
    exit /b %errorlevel%
)

echo [INFO] 构建成功！准备解压...

if exist "%EXTRACT_DIR%" (
    rmdir /s /q "%EXTRACT_DIR%"
)
mkdir "%EXTRACT_DIR%"

cd "%EXTRACT_DIR%"
jar -xf ..\%JAR_NAME%

if %errorlevel% neq 0 (
    echo [ERROR] 解压失败。
    pause
    exit /b %errorlevel%
)

echo [INFO] 解压完成。正在启动应用...
echo [INFO] 运行模式: Exploded (解压运行)
echo [INFO] 激活环境: %ACTIVE_PROFILE%

:: ==================== 启动命令 ====================
:: 在最后加上 --spring.profiles.active 参数
:: 注意：对于 JarLauncher，程序参数放在类名后面
:: JVM 参数 (如 -Xmx) 应该放在 java 和 类名 之间
java -Dgraalvm.locatorDisabled=true -cp "BOOT-INF\classes;BOOT-INF\lib\*" com.jing.admin.AdminApplication --spring.profiles.active=%ACTIVE_PROFILE%

pause