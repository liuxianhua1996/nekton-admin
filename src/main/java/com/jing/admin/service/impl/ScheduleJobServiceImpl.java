package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.workflow.WorkflowExecutor;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.model.api.ScheduleJobRequest;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.mapping.ScheduleJobMapping;
import com.jing.admin.repository.ScheduleJobRepository;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.ScheduleJobLogService;
import com.jing.admin.service.ScheduleJobService;
import com.jing.admin.service.WorkflowExecutionService;
import com.jing.admin.service.WorkflowGlobalParamService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 调度工作流Service实现类
 */
@Service
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
        
        return ScheduleJobMapping.INSTANCE.toDTO(existingJob);
    }

    @Override
    public Boolean deleteScheduleJob(String id) {
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
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, UUID.fromString(id))
                .set(ScheduleJob::getStatus, "DISABLED")
                .set(ScheduleJob::getUpdateTime, System.currentTimeMillis());
        return this.update(updateWrapper);
    }

    @Override
    public Boolean enableJob(String id) {
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, UUID.fromString(id))
                .set(ScheduleJob::getStatus, "ENABLED")
                .set(ScheduleJob::getUpdateTime, System.currentTimeMillis());
        return this.update(updateWrapper);
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
            workflowId,
            new HashMap<>(), // startParams
            id, // jobId作为第一个参数
            "SCHEDULED", // triggerType
            null // extraLogInfo
        );
        
        return result.isSuccess();
    }
    

}