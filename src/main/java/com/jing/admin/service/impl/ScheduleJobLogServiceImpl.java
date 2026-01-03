package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.ScheduleJobLogQueryRequest;
import com.jing.admin.model.api.ScheduleJobLogRequest;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.mapper.ScheduleJobLogMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.dto.ScheduleJobLogDTO;
import com.jing.admin.model.mapping.ScheduleJobLogMapping;
import com.jing.admin.repository.ScheduleJobLogRepository;
import com.jing.admin.service.ScheduleJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 调度任务执行记录Service实现类
 */
@Service
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLog> implements ScheduleJobLogService {

    @Autowired
    private ScheduleJobLogRepository scheduleJobLogRepository;


    @Override
    public ScheduleJobLogDTO createScheduleJobLog(ScheduleJobLogRequest request) {
        ScheduleJobLog scheduleJobLog = ScheduleJobLogMapping.INSTANCE.toEntity(request);
        scheduleJobLog.setCreateTime(System.currentTimeMillis());
        scheduleJobLog.setUpdateTime(System.currentTimeMillis());
        this.save(scheduleJobLog);
        return ScheduleJobLogMapping.INSTANCE.toDTO(scheduleJobLog);
    }

    @Override
    public ScheduleJobLogDTO updateScheduleJobLog(String id, ScheduleJobLogRequest request) {
        // 首先检查记录是否存在
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        ScheduleJobLog existingLog = this.getOne(queryWrapper);
        if (existingLog == null) {
            throw new RuntimeException("调度任务执行记录不存在");
        }
        
        LambdaUpdateWrapper<ScheduleJobLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJobLog::getId, id)
                .set(ScheduleJobLog::getJobId, request.getJobId())
                .set(ScheduleJobLog::getWorkflowId, request.getWorkflowId())
                .set(ScheduleJobLog::getTriggerType, request.getTriggerType())
                .set(ScheduleJobLog::getStatus, request.getStatus())
                .set(ScheduleJobLog::getResult, request.getResult())
                .set(ScheduleJobLog::getStartTime, request.getStartTime())
                .set(ScheduleJobLog::getEndTime, request.getEndTime())
                .set(ScheduleJobLog::getExecutionTime, request.getExecutionTime())
                .set(ScheduleJobLog::getErrorMessage, request.getErrorMessage())
                .set(ScheduleJobLog::getUpdateTime, System.currentTimeMillis());
        
        this.update(updateWrapper);
        
        // 返回更新后的记录
        existingLog.setJobId(request.getJobId());
        existingLog.setWorkflowId(request.getWorkflowId());
        existingLog.setTriggerType(request.getTriggerType());
        existingLog.setStatus(request.getStatus());
        existingLog.setResult(request.getResult());
        existingLog.setStartTime(request.getStartTime());
        existingLog.setEndTime(request.getEndTime());
        existingLog.setExecutionTime(request.getExecutionTime());
        existingLog.setErrorMessage(request.getErrorMessage());
        existingLog.setUpdateTime(System.currentTimeMillis());
        
        return ScheduleJobLogMapping.INSTANCE.toDTO(existingLog);
    }

    @Override
    public Boolean deleteScheduleJobLog(String id) {
        return this.removeById(id);
    }

    @Override
    public ScheduleJobLogDTO getScheduleJobLogById(String id) {
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        ScheduleJobLog scheduleJobLog = this.getOne(queryWrapper);
        if (scheduleJobLog == null) {
            return null;
        }
        
        return ScheduleJobLogMapping.INSTANCE.toDTO(scheduleJobLog);
    }

    @Override
    public List<ScheduleJobLogDTO> getScheduleJobLogList() {
        List<ScheduleJobLog> scheduleJobLogs = this.list();
        return scheduleJobLogs.stream()
                .map(ScheduleJobLogMapping.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleJobLogDTO> getScheduleJobLogByJobId(String jobId) {
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        
        List<ScheduleJobLog> scheduleJobLogs = this.list(queryWrapper);
        return scheduleJobLogs.stream()
                .map(ScheduleJobLogMapping.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleJobLogDTO> getScheduleJobLogPage(ScheduleJobLogQueryRequest queryRequest) {
        // 创建分页对象
        Page<ScheduleJobLogDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 使用Repository的自定义分页查询方法（关联用户表）
        IPage<ScheduleJobLogDTO> scheduleJobLogPage = scheduleJobLogRepository.selectScheduleJobLogPageWithUser(page, queryRequest);

        // 直接使用DTO列表
        List<ScheduleJobLogDTO> records = scheduleJobLogPage.getRecords();

        // 构建分页结果
        return PageResult.of(
                records,
                scheduleJobLogPage.getTotal(),
                scheduleJobLogPage.getCurrent(),
                scheduleJobLogPage.getSize()
        );
    }


}