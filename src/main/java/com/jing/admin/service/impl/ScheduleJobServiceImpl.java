package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.dto.ScheduleJobRequest;
import com.jing.admin.core.dto.ScheduleJobResponse;
import com.jing.admin.core.entity.ScheduleJob;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.service.ScheduleJobService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(request, scheduleJob);
        scheduleJob.setCreateTime(System.currentTimeMillis());
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        
        this.save(scheduleJob);
        
        ScheduleJobResponse response = new ScheduleJobResponse();
        BeanUtils.copyProperties(scheduleJob, response);
        return response;
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
        
        BeanUtils.copyProperties(request, scheduleJob);
        scheduleJob.setId(id);
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        
        this.updateById(scheduleJob);
        
        ScheduleJobResponse response = new ScheduleJobResponse();
        BeanUtils.copyProperties(scheduleJob, response);
        return response;
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
        
        ScheduleJobResponse response = new ScheduleJobResponse();
        BeanUtils.copyProperties(scheduleJob, response);
        return response;
    }

    @Override
    public List<ScheduleJobResponse> getScheduleJobList() {
        List<ScheduleJob> scheduleJobs = this.list();
        List<ScheduleJobResponse> responses = new ArrayList<>();
        
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleJobResponse response = new ScheduleJobResponse();
            BeanUtils.copyProperties(scheduleJob, response);
            responses.add(response);
        }
        
        return responses;
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