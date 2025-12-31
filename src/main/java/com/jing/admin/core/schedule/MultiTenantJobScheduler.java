package com.jing.admin.core.schedule;

import com.jing.admin.service.MultiTenantScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 多租户定时任务调度器
 * 负责初始化基于Quartz的cron任务，并处理其他类型的即时任务
 */
@Slf4j
@Component
public class MultiTenantJobScheduler implements CommandLineRunner {

    @Autowired
    private MultiTenantScheduleService multiTenantScheduleService;

    /**
     * 应用启动时初始化所有cron类型的调度任务
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化多租户Quartz调度任务");
        try {
            // 初始化所有租户的cron调度任务
            multiTenantScheduleService.initializeAllTenantScheduledJobs();
        } catch (Exception e) {
            log.error("初始化多租户Quartz调度任务时发生错误", e);
        }
        log.info("完成初始化多租户Quartz调度任务");
    }
}