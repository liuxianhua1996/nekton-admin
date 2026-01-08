package com.jing.admin.service;

import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.ScheduleJobRequest;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 调度工作流Service接口
 */
public interface ScheduleJobService extends IService<ScheduleJob> {
    /**
     * 创建调度工作流
     */
    ScheduleJobDTO createScheduleJob(ScheduleJobRequest request);

    /**
     * 更新调度工作流
     */
    ScheduleJobDTO updateScheduleJob(String id, ScheduleJobRequest request);

    /**
     * 删除调度工作流
     */
    Boolean deleteScheduleJob(String id);

    /**
     * 根据ID获取调度工作流
     */
    ScheduleJobDTO getScheduleJobById(String id);

    /**
     * 获取调度工作流列表
     */
    List<ScheduleJobDTO> getScheduleJobList();

    /**
     * 获取调度工作流分页列表
     */
    PageResult<ScheduleJobDTO> getScheduleJobPage(ScheduleJobQueryRequest queryRequest);

    /**
     * 停用调度工作流
     */
    Boolean disableJob(String id);

    /**
     * 启用调度工作流
     */
    Boolean enableJob(String id);

    /**
     * 立即执行调度工作流
     */
    Boolean executeJob(String id);
    
    /**
     * Webhook触发调度任务
     */
    Boolean triggerWebhookJob(String id, Map inputData);
}