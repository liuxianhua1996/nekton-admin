package com.jing.admin.core.dto;

import lombok.Data;

/**
 * 调度工作流执行记录请求DTO
 */
@Data
public class ScheduleJobLogRequest {
    private String id;
    private String jobId;
    private String workflowId;
    private String triggerType;
    private String status;
    private String result;
    private Long startTime;
    private Long endTime;
    private Long executionTime;
    private String errorMessage;
    private String tenantId;
}