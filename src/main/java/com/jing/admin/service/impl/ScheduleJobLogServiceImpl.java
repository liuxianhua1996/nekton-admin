package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.dto.ScheduleJobLogRequest;
import com.jing.admin.core.dto.ScheduleJobLogResponse;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.mapper.ScheduleJobLogMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.mapping.ScheduleJobLogMapping;
import com.jing.admin.service.ScheduleJobLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 调度任务执行记录Service实现类
 */
@Service
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLog> implements ScheduleJobLogService {


    @Override
    public ScheduleJobLogResponse createScheduleJobLog(ScheduleJobLogRequest request) {
        ScheduleJobLog scheduleJobLog = ScheduleJobLogMapping.INSTANCE.toEntity(request);
        scheduleJobLog.setCreateTime(System.currentTimeMillis());
        scheduleJobLog.setUpdateTime(System.currentTimeMillis());
        this.save(scheduleJobLog);
        return ScheduleJobLogMapping.INSTANCE.toResponse(scheduleJobLog);
    }

    @Override
    public ScheduleJobLogResponse updateScheduleJobLog(String id, ScheduleJobLogRequest request) {
        ScheduleJobLog scheduleJobLog = this.getById(id);
        if (scheduleJobLog == null) {
            throw new RuntimeException("调度任务执行记录不存在");
        }
        
        scheduleJobLog = ScheduleJobLogMapping.INSTANCE.updateEntityFromRequest(request);
        scheduleJobLog.setId(id);
        scheduleJobLog.setUpdateTime(System.currentTimeMillis());
        
        this.updateById(scheduleJobLog);
        
        return ScheduleJobLogMapping.INSTANCE.toResponse(scheduleJobLog);
    }

    @Override
    public Boolean deleteScheduleJobLog(String id) {
        return this.removeById(id);
    }

    @Override
    public ScheduleJobLogResponse getScheduleJobLogById(String id) {
        ScheduleJobLog scheduleJobLog = this.getById(id);
        if (scheduleJobLog == null) {
            return null;
        }
        
        return ScheduleJobLogMapping.INSTANCE.toResponse(scheduleJobLog);
    }

    @Override
    public List<ScheduleJobLogResponse> getScheduleJobLogList() {
        List<ScheduleJobLog> scheduleJobLogs = this.list();
        return scheduleJobLogs.stream()
                .map(ScheduleJobLogMapping.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleJobLogResponse> getScheduleJobLogByJobId(String jobId) {
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        
        List<ScheduleJobLog> scheduleJobLogs = this.list(queryWrapper);
        return scheduleJobLogs.stream()
                .map(ScheduleJobLogMapping.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleJobLogResponse> getScheduleJobLogPage(ScheduleJobQueryRequest queryRequest) {
        // 创建分页对象
        Page<ScheduleJobLog> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 构建查询条件
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getWorkflowId() != null && !queryRequest.getWorkflowId().isEmpty()) {
            queryWrapper.eq("job_id", queryRequest.getWorkflowId()); // Using workflowId as jobId for logs
        }

        // 执行分页查询
        IPage<ScheduleJobLog> scheduleJobLogPage = this.page(page, queryWrapper);

        // 转换为响应对象列表
        List<ScheduleJobLogResponse> records = scheduleJobLogPage.getRecords().stream()
                .map(ScheduleJobLogMapping.INSTANCE::toResponse)
                .collect(Collectors.toList());

        // 构建分页结果
        return PageResult.of(
                records,
                scheduleJobLogPage.getTotal(),
                scheduleJobLogPage.getCurrent(),
                scheduleJobLogPage.getSize()
        );
    }
}