package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.dto.ScheduleJobLogRequest;
import com.jing.admin.core.dto.ScheduleJobLogResponse;
import com.jing.admin.core.entity.ScheduleJobLog;
import com.jing.admin.mapper.ScheduleJobLogMapper;
import com.jing.admin.service.ScheduleJobLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度任务执行记录Service实现类
 */
@Service
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLog> implements ScheduleJobLogService {

    @Override
    public ScheduleJobLogResponse createScheduleJobLog(ScheduleJobLogRequest request) {
        ScheduleJobLog scheduleJobLog = new ScheduleJobLog();
        BeanUtils.copyProperties(request, scheduleJobLog);
        scheduleJobLog.setCreateTime(System.currentTimeMillis());
        scheduleJobLog.setUpdateTime(System.currentTimeMillis());
        
        this.save(scheduleJobLog);
        
        ScheduleJobLogResponse response = new ScheduleJobLogResponse();
        BeanUtils.copyProperties(scheduleJobLog, response);
        return response;
    }

    @Override
    public ScheduleJobLogResponse updateScheduleJobLog(String id, ScheduleJobLogRequest request) {
        ScheduleJobLog scheduleJobLog = this.getById(id);
        if (scheduleJobLog == null) {
            throw new RuntimeException("调度任务执行记录不存在");
        }
        
        BeanUtils.copyProperties(request, scheduleJobLog);
        scheduleJobLog.setId(id);
        scheduleJobLog.setUpdateTime(System.currentTimeMillis());
        
        this.updateById(scheduleJobLog);
        
        ScheduleJobLogResponse response = new ScheduleJobLogResponse();
        BeanUtils.copyProperties(scheduleJobLog, response);
        return response;
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
        
        ScheduleJobLogResponse response = new ScheduleJobLogResponse();
        BeanUtils.copyProperties(scheduleJobLog, response);
        return response;
    }

    @Override
    public List<ScheduleJobLogResponse> getScheduleJobLogList() {
        List<ScheduleJobLog> scheduleJobLogs = this.list();
        List<ScheduleJobLogResponse> responses = new ArrayList<>();
        
        for (ScheduleJobLog scheduleJobLog : scheduleJobLogs) {
            ScheduleJobLogResponse response = new ScheduleJobLogResponse();
            BeanUtils.copyProperties(scheduleJobLog, response);
            responses.add(response);
        }
        
        return responses;
    }

    @Override
    public List<ScheduleJobLogResponse> getScheduleJobLogByJobId(String jobId) {
        QueryWrapper<ScheduleJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        
        List<ScheduleJobLog> scheduleJobLogs = this.list(queryWrapper);
        List<ScheduleJobLogResponse> responses = new ArrayList<>();
        
        for (ScheduleJobLog scheduleJobLog : scheduleJobLogs) {
            ScheduleJobLogResponse response = new ScheduleJobLogResponse();
            BeanUtils.copyProperties(scheduleJobLog, response);
            responses.add(response);
        }
        
        return responses;
    }
}