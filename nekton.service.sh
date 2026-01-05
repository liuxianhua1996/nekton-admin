[Unit]
# 服务描述
Description=dataCenter server
# 依赖配置：确保网络已经连接后再启动
After=syslog.target network.target

[Service]
# 1. 运行服务的用户
User=toncen

# ============================================================
# 2. 【关键修改】工作目录
#    这里必须填你解压后的那个文件夹的路径！
#    系统会去这个目录下找 BOOT-INF, META-INF 等文件夹
# ============================================================
WorkingDirectory=/home/toncen/project/dataCenter/server/nekton

# ============================================================
# 3. 【核心修改】启动命令
#    A. 去掉了 -jar 参数
#    B. 启动类改为 org.springframework.boot.loader.launch.JarLauncher
#    C. 也就是告诉 Java：去当前目录加载 Spring Boot 的启动器
# ============================================================
ExecStart=/home/toncen/env/graalvm-community-openjdk-21.0.2+13.1/bin/java \
    -Xms2048m -Xmx2048m \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+ZGenerational \
    -XX:+EnableJVMCI \
    -XX:+UseJVMCICompiler \
    -Dgraalvm.locatorDisabled=true \
    org.springframework.boot.loader.launch.JarLauncher \
    --spring.profiles.active=prod

# 4. 守护配置：崩溃自动重启
Restart=always
RestartSec=60

# 5. 退出码处理
SuccessExitStatus=143

# 6. 日志黑洞配置 (保持你原来的设置)
StandardOutput=null
StandardError=null

[Install]
# 开机自启级别
WantedBy=multi-user.target