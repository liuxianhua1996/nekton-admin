package com.jing.admin.core.dto;

import lombok.Data;

/**
 * 调度工作流响应DTO
 */
@Data
public class ScheduleJobResponse {
    private String id;
    private String name;
    private String workflowId;
    private String triggerType;
    private String triggerConfig;
    private String status;
    private String description;
    private String tenantId;
    private Long createTime;
    private Long updateTime;
    private String createUserId;
    private String updateUserId;
}