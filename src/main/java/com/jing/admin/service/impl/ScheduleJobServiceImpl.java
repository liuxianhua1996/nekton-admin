package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.dto.ScheduleJobRequest;
import com.jing.admin.core.dto.ScheduleJobResponse;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.mapping.ScheduleJobMapping;
import com.jing.admin.service.ScheduleJobService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 调度工作流Service实现类
 */
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Override
    public ScheduleJobResponse createScheduleJob(ScheduleJobRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }
        
        ScheduleJob scheduleJob = ScheduleJobMapping.INSTANCE.toEntity(request);
        scheduleJob.setCreateTime(System.currentTimeMillis());
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        scheduleJob.setCreateUserId(MDC.get("userId"));
        scheduleJob.setUpdateUserId(MDC.get("userId"));
        
        this.save(scheduleJob);
        
        return ScheduleJobMapping.INSTANCE.toResponse(scheduleJob);
    }

    @Override
    public ScheduleJobResponse updateScheduleJob(String id, ScheduleJobRequest request) {
        ScheduleJob scheduleJob = this.getById(id);
        if (scheduleJob == null) {
            throw new RuntimeException("调度工作流不存在");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }
        
        scheduleJob = ScheduleJobMapping.INSTANCE.updateEntityFromRequest(request);
        scheduleJob.setId(id);
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        
        this.updateById(scheduleJob);
        
        return ScheduleJobMapping.INSTANCE.toResponse(scheduleJob);
    }

    @Override
    public Boolean deleteScheduleJob(String id) {
        return this.removeById(id);
    }

    @Override
    public ScheduleJobResponse getScheduleJobById(String id) {
        ScheduleJob scheduleJob = this.getById(id);
        if (scheduleJob == null) {
            return null;
        }
        
        return ScheduleJobMapping.INSTANCE.toResponse(scheduleJob);
    }

    @Override
    public List<ScheduleJobResponse> getScheduleJobList() {
        List<ScheduleJob> scheduleJobs = this.list();
        return scheduleJobs.stream()
                .map(ScheduleJobMapping.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleJobResponse> getScheduleJobPage(ScheduleJobQueryRequest queryRequest) {
        // 创建分页对象
        Page<ScheduleJob> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 构建查询条件
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getName() != null && !queryRequest.getName().isEmpty()) {
            queryWrapper.like("name", queryRequest.getName());
        }
        if (queryRequest.getWorkflowId() != null && !queryRequest.getWorkflowId().isEmpty()) {
            queryWrapper.eq("workflow_id", queryRequest.getWorkflowId());
        }
        if (queryRequest.getTriggerType() != null && !queryRequest.getTriggerType().isEmpty()) {
            queryWrapper.eq("trigger_type", queryRequest.getTriggerType());
        }
        if (queryRequest.getStatus() != null && !queryRequest.getStatus().isEmpty()) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }

        // 执行分页查询
        IPage<ScheduleJob> scheduleJobPage = this.page(page, queryWrapper);

        // 转换为响应对象列表
        List<ScheduleJobResponse> records = scheduleJobPage.getRecords().stream()
                .map(ScheduleJobMapping.INSTANCE::toResponse)
                .collect(Collectors.toList());

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
        ScheduleJob scheduleJob = this.getById(id);
        if (scheduleJob == null) {
            return false;
        }
        
        scheduleJob.setStatus("DISABLED");
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        return this.updateById(scheduleJob);
    }

    @Override
    public Boolean enableJob(String id) {
        ScheduleJob scheduleJob = this.getById(id);
        if (scheduleJob == null) {
            return false;
        }
        scheduleJob.setStatus("ENABLED");
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        return this.updateById(scheduleJob);
    }

    @Override
    public Boolean executeJob(String id) {
        // 这里可以实现立即执行调度工作流的逻辑
        // 暂时返回true，实际实现需要根据具体工作流来执行
        return true;
    }
}