package com.jing.admin.core.schedule;

import com.jing.admin.service.MultiTenantScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 多租户定时任务调度器
 * 定期执行所有租户的定时任务
 */
@Slf4j
@Component
public class MultiTenantJobScheduler {

    @Autowired
    private MultiTenantScheduleService multiTenantScheduleService;

    /**
     * 每分钟执行一次所有租户的定时任务检查
     * 这个方法会检查所有租户的定时任务配置，并执行需要执行的任务
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void executeAllTenantScheduledJobs() {
        log.info("开始执行多租户定时任务检查");
        try {
            multiTenantScheduleService.executeAllTenantScheduledJobs();
        } catch (Exception e) {
            log.error("执行多租户定时任务时发生错误", e);
        }
        log.info("完成执行多租户定时任务检查");
    }
}