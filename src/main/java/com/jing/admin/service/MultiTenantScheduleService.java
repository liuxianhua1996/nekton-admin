package com.jing.admin.service;

/**
 * 多租户调度服务接口
 */
public interface MultiTenantScheduleService {
    
    /**
     * 获取所有租户的定时任务并执行
     */
    void executeAllTenantScheduledJobs();
}