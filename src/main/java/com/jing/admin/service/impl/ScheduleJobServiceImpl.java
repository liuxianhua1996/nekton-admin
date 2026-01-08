package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.constant.ConstantEnum;
import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.ThreadPoolConfig;
import com.jing.admin.core.schedule.job.JobTaskManager;
import com.jing.admin.core.tenant.TenantContextHolder;
import com.jing.admin.core.workflow.WorkflowExecutor;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.model.api.ScheduleJobRequest;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.dto.WorkflowExecution;
import com.jing.admin.model.mapping.ScheduleJobMapping;
import com.jing.admin.repository.ScheduleJobRepository;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.ScheduleJobLogService;
import com.jing.admin.service.ScheduleJobService;
import com.jing.admin.service.WorkflowGlobalParamService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 调度工作流Service实现类
 */
@Service
@Slf4j
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowExecutor workflowExecutor;
    @Autowired
    private ScheduleJobLogService scheduleJobLogService;
    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;
    @Autowired
    private com.jing.admin.service.WorkflowExecutionService workflowExecutionService;
    @Autowired
    private JobTaskManager jobTaskManager;

    @Override
    public ScheduleJobDTO createScheduleJob(ScheduleJobRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }

        ScheduleJob scheduleJob = ScheduleJobMapping.INSTANCE.toEntity(request);
        scheduleJob.setCreateTime(System.currentTimeMillis());
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        scheduleJob.setCreateUserId(MDC.get("userId"));
        scheduleJob.setUpdateUserId(MDC.get("userId"));

        this.save(scheduleJob);

        // 如果新创建的任务状态是启用的，需要注册定时任务
        if ("ENABLED".equals(request.getStatus()) && "triggerType".equals(request.getTriggerType())) {
            registerCronJob(scheduleJob);
        }

        return ScheduleJobMapping.INSTANCE.toDTO(scheduleJob);
    }

    @Override
    public ScheduleJobDTO updateScheduleJob(String id, ScheduleJobRequest request) {
        // 首先检查记录是否存在
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        ScheduleJob existingJob = this.getOne(queryWrapper);
        if (existingJob == null) {
            throw new RuntimeException("调度工作流不存在");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }

        // 记录原始状态，用于判断是否需要处理定时任务
        String originalStatus = existingJob.getStatus();
        String newStatus = request.getStatus();

        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, UUID.fromString(id))
                .set(ScheduleJob::getName, request.getName())
                .set(ScheduleJob::getDescription, request.getDescription())
                .set(ScheduleJob::getTriggerConfig, request.getTriggerConfig())
                .set(ScheduleJob::getStatus, request.getStatus())
                .set(ScheduleJob::getWorkflowId, request.getWorkflowId())
                .set(ScheduleJob::getUpdateTime, System.currentTimeMillis())
                .set(ScheduleJob::getUpdateUserId, MDC.get("userId"));

        this.update(updateWrapper);

        // 返回更新后的记录
        existingJob.setName(request.getName());
        existingJob.setDescription(request.getDescription());
        existingJob.setTriggerConfig(request.getTriggerConfig());
        existingJob.setStatus(request.getStatus());
        existingJob.setWorkflowId(request.getWorkflowId());
        existingJob.setUpdateTime(System.currentTimeMillis());
        existingJob.setUpdateUserId(MDC.get("userId"));

        // 如果状态发生了变化，需要处理定时任务
        if (!originalStatus.equals(newStatus)) {
            if ("ENABLED".equals(newStatus) && "DISABLED".equals(originalStatus)) {
                // 从禁用变为启用，注册定时任务
                registerCronJob(existingJob);
            } else if ("DISABLED".equals(newStatus) && "ENABLED".equals(originalStatus)) {
                // 从启用变为禁用，暂停定时任务
                jobTaskManager.pauseJob(id);
            }
        }

        return ScheduleJobMapping.INSTANCE.toDTO(existingJob);
    }

    @Override
    public Boolean deleteScheduleJob(String id) {
        // 在删除前，先暂停并删除对应的定时任务
        jobTaskManager.deleteJob(id);
        return this.removeById(id);
    }

    @Override
    public ScheduleJobDTO getScheduleJobById(String id) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        ScheduleJob scheduleJob = this.getOne(queryWrapper);
        if (scheduleJob == null) {
            return null;
        }

        return ScheduleJobMapping.INSTANCE.toDTO(scheduleJob);
    }

    @Override
    public List<ScheduleJobDTO> getScheduleJobList() {
        List<ScheduleJob> scheduleJobs = this.list();
        return scheduleJobs.stream()
                .map(ScheduleJobMapping.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleJobDTO> getScheduleJobPage(ScheduleJobQueryRequest queryRequest) {
        // 创建分页对象
        Page<ScheduleJobDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 使用Repository的自定义分页查询方法（关联用户表）
        IPage<ScheduleJobDTO> scheduleJobPage = scheduleJobRepository.selectScheduleJobPageWithUser(page, queryRequest);

        // 直接使用DTO列表
        List<ScheduleJobDTO> records = scheduleJobPage.getRecords();

        // 构建分页结果
        return PageResult.of(
                records,
                scheduleJobPage.getTotal(),
                scheduleJobPage.getCurrent(),
                scheduleJobPage.getSize()
        );
    }

    @Override
    public Boolean disableJob(String id) {
        // 首先检查调度任务是否存在
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        ScheduleJob existingJob = this.getOne(queryWrapper);
        if (existingJob == null) {
            throw new RuntimeException("调度任务不存在");
        }

        // 更新数据库状态
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, UUID.fromString(id))
                .set(ScheduleJob::getStatus, "DISABLED")
                .set(ScheduleJob::getUpdateTime, System.currentTimeMillis());
        boolean updateResult = this.update(updateWrapper);

        // 如果更新成功，暂停对应的定时任务
        if (updateResult) {
            jobTaskManager.pauseJob(id);
        }

        return updateResult;
    }

    @Override
    public Boolean enableJob(String id) {
        // 首先检查调度任务是否存在
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        ScheduleJob existingJob = this.getOne(queryWrapper);
        if (existingJob == null) {
            throw new RuntimeException("调度任务不存在");
        }

        // 检查任务当前状态，如果是已启用则直接返回
        if ("ENABLED".equals(existingJob.getStatus())) {
            return true; // 已经是启用状态，无需操作
        }

        // 更新数据库状态
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, UUID.fromString(id))
                .set(ScheduleJob::getStatus, "ENABLED")
                .set(ScheduleJob::getUpdateTime, System.currentTimeMillis());
        boolean updateResult = this.update(updateWrapper);

        // 如果更新成功，处理对应的定时任务
        if (updateResult) {
            // 检查任务是否已存在于JobTaskManager中
            if (jobTaskManager.getJobTask(id) != null) {
                // 如果任务存在，直接恢复
                jobTaskManager.resumeJob(id);
            } else {
                // 如果任务不存在，需要创建并注册定时任务
                registerCronJob(existingJob);
            }
        }

        return updateResult;
    }

    @Override
    public Boolean executeJob(String id) {
        // 获取调度任务信息
        ScheduleJob scheduleJob = scheduleJobRepository.getById(id);
        if (scheduleJob == null) {
            throw new RuntimeException("调度任务不存在");
        }

        String workflowId = scheduleJob.getWorkflowId();

        // 执行工作流（带日志记录）
        WorkflowExecutionResult result = workflowExecutionService.executeWorkflowWithLog(
                WorkflowExecution.builder()
                        .jobId(id)
                        .workflowId(workflowId)
                        .startParams(new HashMap<>())
                        .workflowInstanceId(id)
                        .triggerType("SCHEDULED")
                        .extraLogInfo(null)
                        .build()
        );

        return result.isSuccess();
    }

    @Override
    public Boolean triggerWebhookJob(String id, Map inputData) {
        // 获取调度任务信息
        ScheduleJob scheduleJob = scheduleJobRepository.getById(id);
        if (scheduleJob == null) {
            throw new RuntimeException("调度任务不存在");
        }

        // 验证调度任务的触发类型是否为webhook
        if (!"webhook".equalsIgnoreCase(scheduleJob.getTriggerType())) {
            throw new RuntimeException("该调度任务不是webhook触发类型");
        }

        String workflowId = scheduleJob.getWorkflowId();

        // 使用线程池异步执行工作流（带日志记录），触发类型为webhook
        // 避免直接创建线程，使用预定义的webhook线程池
        ThreadPoolConfig.WEBHOOK_THREAD_POOL.submit(() -> {
            try {
                WorkflowExecutionResult result = workflowExecutionService.executeWorkflowWithLog(
                        WorkflowExecution.builder()
                                .jobId(id)
                                .workflowId(workflowId)
                                .startParams(inputData == null ? new HashMap<>() : inputData)
                                .workflowInstanceId(id)
                                .triggerType("WEBHOOK")
                                .extraLogInfo(null)
                                .build()
                );
                
                log.info("Webhook触发的工作流执行完成，任务ID: {}, 结果: {}", id, result.isSuccess());
            } catch (Exception e) {
                log.error("Webhook触发的工作流执行失败，任务ID: {}", id, e);
            }
        });

        // 立即返回成功，表示webhook已接收并开始处理
        return true;
    }

    /**
     * 注册cron调度任务
     */
    private void registerCronJob(ScheduleJob scheduleJob) {
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
            
            // 注册到JobTaskManager，租户ID暂时为null（ScheduleJob实体中没有tenantId字段）
            jobTaskManager.addCronJob(jobId, jobName, cronExpression, jobTask, null);
            
            log.info("成功注册cron任务: {}, 租户: null, cron表达式: {}", jobId, cronExpression);
        } catch (Exception e) {
            log.error("注册cron任务 {} 失败: {}", scheduleJob.getId(), e.getMessage(), e);
            throw new RuntimeException("注册cron任务失败", e);
        }
    }

}