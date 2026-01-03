package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.core.constant.ConstantEnum;
import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.job.JobTaskManager;
import com.jing.admin.core.tenant.TenantContextWrapper;
import com.jing.admin.core.tenant.TenantContextHolder;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.model.domain.Tenant;
import com.jing.admin.repository.ScheduleJobRepository;
import com.jing.admin.repository.TenantRepository;
import com.jing.admin.service.MultiTenantScheduleService;
import com.jing.admin.service.WorkflowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 多租户调度服务实现类
 * 用于获取所有租户的定时任务并执行
 */
@Slf4j
@Service
public class MultiTenantScheduleServiceImpl implements MultiTenantScheduleService {

    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private ScheduleJobRepository scheduleJobRepository;
    
    @Autowired
    private WorkflowExecutionService workflowExecutionService;
    
    @Autowired
    private JobTaskManager jobTaskManager;

    /**
     * 初始化所有租户的定时任务
     */
    @Override
    public void initializeAllTenantScheduledJobs() {
        log.info("开始初始化所有租户的定时任务");
        
        // 获取所有活跃租户
        List<Tenant> tenants = getAllActiveTenants();
        
        for (Tenant tenant : tenants) {
            String tenantId = tenant.getId();
            log.debug("处理租户: {}", tenantId);
            
            // 设置当前租户上下文
            TenantContextHolder.setTenantId(tenantId);
            
            try {
                // 获取当前租户的所有启用的cron调度任务并注册
                List<ScheduleJob> cronJobs = getEnabledCronJobsForTenant(tenantId);
                
                for (ScheduleJob scheduleJob : cronJobs) {
                    log.debug("注册租户 {} 的cron调度任务: {}", tenantId, scheduleJob.getId());
                    registerCronJob(scheduleJob, tenantId);
                }
            } catch (Exception e) {
                log.error("处理租户 {} 的定时任务时发生错误: {}", tenantId, e.getMessage(), e);
            } finally {
                // 清理租户上下文
                TenantContextHolder.clear();
            }
        }
        
        log.info("完成初始化所有租户的定时任务");
    }

    /**
     * 获取所有活跃租户
     * @return 活跃租户列表
     */
    private List<Tenant> getAllActiveTenants() {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ACTIVE"); // 只获取活跃状态的租户
        return tenantRepository.list(queryWrapper);
    }

    /**
     * 获取指定租户的启用的cron调度任务
     * @param tenantId 租户ID
     * @return 启用的cron调度任务列表
     */
    private List<ScheduleJob> getEnabledCronJobsForTenant(String tenantId) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ENABLED"); // 只获取启用的调度任务
        queryWrapper.eq("trigger_type", "cron"); // 只获取cron类型的任务
        return scheduleJobRepository.list(queryWrapper);
    }

    /**
     * 注册cron调度任务
     */
    private void registerCronJob(ScheduleJob scheduleJob, String tenantId) {
        try {
            String cronExpression = scheduleJob.getTriggerConfig();
            String jobId = scheduleJob.getId();
            String jobName = scheduleJob.getName();
            
            // 创建一个实现AbstractJobTask的作业任务，用于执行工作流
            AbstractJobTask jobTask = new AbstractJobTask() {
                @Override
                public void run() {
                    // 直接执行工作流，租户上下文将在JobScheduler中处理
                    workflowExecutionService.executeWorkflowWithLog(
                        com.jing.admin.model.dto.WorkflowExecution.builder()
                            .jobId(scheduleJob.getId())
                            .workflowId(scheduleJob.getWorkflowId())
                            .startParams(new HashMap<>())
                            .workflowInstanceId(scheduleJob.getId())
                            .triggerType("CRON")
                            .build()
                    );
                }
            };
            jobTask.setTaskId(jobId);
            jobTask.setTaskData(scheduleJob);
            jobTask.setStatus(ConstantEnum.WAIT);
            
            // 注册到JobTaskManager with tenant ID
            jobTaskManager.addCronJob(jobId, jobName, cronExpression, jobTask, tenantId);
            
            log.info("成功注册cron任务: {}, 租户: {}, cron表达式: {}", jobId, tenantId, cronExpression);
        } catch (Exception e) {
            log.error("注册cron任务 {} 失败: {}", scheduleJob.getId(), e.getMessage(), e);
        }
    }
}