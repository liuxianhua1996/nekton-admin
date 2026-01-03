package com.jing.admin.core.schedule.job;

import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author zhicheng
 * @date 2024/4/15
 **/
@Slf4j
@DisallowConcurrentExecution//避免执行过慢,并发执行
public class JobScheduler extends QuartzJobBean {
    public JobScheduler() {
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        JobTask jobTask = (JobTask) jobDataMap.get("taskData");
        
        // Get tenant ID from job data map if available
        String tenantId = (String) jobDataMap.get("tenantId");
        
        // Set tenant context for the current thread before executing the task
        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContextHolder.setTenantId(tenantId);
            log.info("执行定时任务：{}, 租户ID: {}", jobTask.getName(), tenantId);
        } else {
            log.warn("租户ID为空或null，任务: {}, 任务ID: {}", jobTask.getName(), jobTask.getId());
        }
        
        // Execute the task - TaskEngine will handle context propagation to thread pool
        jobTask.run();
    }
}
