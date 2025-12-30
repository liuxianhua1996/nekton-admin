package com.jing.admin.model.api;

import lombok.Data;

/**
 * 调度任务查询请求参数
 */
@Data
public class ScheduleJobQueryRequest {
    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示条数
     */
    private Long size = 10L;

    /**
     * 调度名称
     */
    private String name;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 触发方式
     */
    private String triggerType;

    /**
     * 状态
     */
    private String status;
}