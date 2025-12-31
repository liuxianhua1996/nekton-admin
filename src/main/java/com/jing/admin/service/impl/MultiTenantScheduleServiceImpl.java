package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    /**
     * 获取所有租户的定时任务并执行
     */
    @Override
    public void executeAllTenantScheduledJobs() {
        log.info("开始执行所有租户的定时任务");
        
        // 获取所有活跃租户
        List<Tenant> tenants = getAllActiveTenants();
        
        for (Tenant tenant : tenants) {
            String tenantId = tenant.getId();
            log.debug("处理租户: {}", tenantId);
            
            // 设置当前租户上下文
            TenantContextHolder.setTenantId(tenantId);
            
            try {
                // 获取当前租户的所有启用的调度任务
                List<ScheduleJob> scheduleJobs = getEnabledScheduleJobsForTenant(tenantId);
                
                for (ScheduleJob scheduleJob : scheduleJobs) {
                    log.debug("执行租户 {} 的调度任务: {}", tenantId, scheduleJob.getId());
                    executeScheduleJob(scheduleJob);
                }
            } catch (Exception e) {
                log.error("处理租户 {} 的调度任务时发生错误: {}", tenantId, e.getMessage(), e);
            } finally {
                // 清理租户上下文
                TenantContextHolder.clear();
            }
        }
        
        log.info("完成执行所有租户的定时任务");
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
     * 获取指定租户的启用的调度任务
     * @param tenantId 租户ID
     * @return 启用的调度任务列表
     */
    private List<ScheduleJob> getEnabledScheduleJobsForTenant(String tenantId) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ENABLED"); // 只获取启用的调度任务
        // 由于每个租户有独立的数据库，当前数据源已指向正确的租户数据库，无需按tenant_id过滤
        return scheduleJobRepository.list(queryWrapper);
    }

    /**
     * 执行调度任务
     * @param scheduleJob 调度任务
     */
    private void executeScheduleJob(ScheduleJob scheduleJob) {
        try {
            // 获取当前租户ID，用于传递给执行线程
            String currentTenantId = TenantContextHolder.getTenantId();
            
            // 使用租户上下文包装器来确保在执行过程中保持租户上下文
            Runnable wrappedRunnable = TenantContextWrapper.wrap(() -> {
                // 这里调用现有的调度任务执行逻辑
                // 注意：执行结果会被保存到当前租户的数据库中，因为租户上下文已经设置
                workflowExecutionService.executeWorkflowWithLog(
                    com.jing.admin.model.dto.WorkflowExecution.builder()
                        .jobId(scheduleJob.getId())
                        .workflowId(scheduleJob.getWorkflowId())
                        .startParams(new java.util.HashMap<>())
                        .workflowInstanceId(scheduleJob.getId())
                        .triggerType("SCHEDULED")
                        .extraLogInfo(null)
                        .build()
                );
            }, currentTenantId);
            
            wrappedRunnable.run();
        } catch (Exception e) {
            log.error("执行调度任务 {} 失败: {}", scheduleJob.getId(), e.getMessage(), e);
        }
    }
}