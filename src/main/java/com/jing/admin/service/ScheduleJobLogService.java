package com.jing.admin.service;

import com.jing.admin.core.dto.ScheduleJobLogRequest;
import com.jing.admin.core.dto.ScheduleJobLogResponse;
import com.jing.admin.core.entity.ScheduleJobLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 调度任务执行记录Service接口
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLog> {
    /**
     * 创建调度任务执行记录
     */
    ScheduleJobLogResponse createScheduleJobLog(ScheduleJobLogRequest request);

    /**
     * 更新调度任务执行记录
     */
    ScheduleJobLogResponse updateScheduleJobLog(String id, ScheduleJobLogRequest request);

    /**
     * 删除调度任务执行记录
     */
    Boolean deleteScheduleJobLog(String id);

    /**
     * 根据ID获取调度任务执行记录
     */
    ScheduleJobLogResponse getScheduleJobLogById(String id);

    /**
     * 获取调度任务执行记录列表
     */
    List<ScheduleJobLogResponse> getScheduleJobLogList();

    /**
     * 根据任务ID获取执行记录列表
     */
    List<ScheduleJobLogResponse> getScheduleJobLogByJobId(String jobId);
}