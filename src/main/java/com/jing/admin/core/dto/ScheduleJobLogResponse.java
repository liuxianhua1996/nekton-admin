package com.jing.admin.core.dto;

import lombok.Data;

/**
 * 调度工作流执行记录响应DTO
 */
@Data
public class ScheduleJobLogResponse {
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
    private Long createTime;
    private Long updateTime;
    private String createUserId;
    private String updateUserId;
}