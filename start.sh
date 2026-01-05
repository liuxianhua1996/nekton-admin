[Unit]
# 服务描述，随便写
Description=dataCenter server
# 依赖配置：确保网络已经连接后再启动
After=syslog.target network.target

[Service]
# 1. 运行服务的用户 (为了安全建议用普通用户，如果为了方便可以用 root)
User=toncen

# 2. 你的 jar 包所在的文件夹绝对路径
WorkingDirectory=/home/toncen/project/dataCenter/server/

# 3. 启动命令 (核心部分)
#    注意A: 必须使用 GraalVM 的绝对路径
#    注意B: 不要在这里写 nohup 或 > /dev/null，Systemd 有专用配置
ExecStart=/home/toncen/env/graalvm-community-openjdk-21.0.2+13.1/bin/java \
    -Xms2048m -Xmx2048m \
    -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+ZGenerational \
    -Dspring.profiles.active=prod \
    -jar /home/toncen/project/dataCenter/server/admin-0.0.1-SNAPSHOT.jar

# 4. 守护配置：如果程序崩了，60秒后自动重启 (MES系统必备)
Restart=always
RestartSec=60

# 5. 退出码处理：Java 正常的 kill 信号通常是 143，告诉 Systemd 这是正常退出
SuccessExitStatus=143

# ==========================================
# 6. 日志黑洞配置 (满足你的需求)
#    这就等同于脚本里的 > /dev/null 2>&1
#    Systemd 不会记录任何日志，完全依赖你 SpringBoot 内部的 Logback
# ==========================================
StandardOutput=null
StandardError=null

[Install]
# 开机自启级别
WantedBy=multi-user.target