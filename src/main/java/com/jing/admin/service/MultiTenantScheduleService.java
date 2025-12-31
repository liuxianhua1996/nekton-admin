package com.jing.admin.service;

/**
 * 多租户调度服务接口
 */
public interface MultiTenantScheduleService {
    
    /**
     * 初始化所有租户的定时任务（注册cron任务到调度器）
     */
    void initializeAllTenantScheduledJobs();
}