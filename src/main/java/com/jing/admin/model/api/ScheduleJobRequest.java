package com.jing.admin.model.api;

import lombok.Data;

/**
 * 调度工作流请求DTO
 */
@Data
public class ScheduleJobRequest {
    private String id;
    private String name;
    private String workflowId;
    private String triggerType;
    private String triggerConfig;
    private String status;
    private String description;
    private String tenantId;
}