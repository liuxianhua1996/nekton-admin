package com.jing.admin.model.api;

import lombok.Data;

/**
 * 调度任务执行记录查询请求参数
 */
@Data
public class ScheduleJobLogQueryRequest {
    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示条数
     */
    private Long size = 10L;

    /**
     * 任务ID（必需）
     */
    private String jobId;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 状态
     */
    private String status;
}