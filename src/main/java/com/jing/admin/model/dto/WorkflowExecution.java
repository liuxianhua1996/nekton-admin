package com.jing.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 工作流执行请求DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecution {
    private String tenantId;
    private String jobId;
    
    /**
     * 工作流ID
     */
    private String workflowId;
    
    /**
     * 启动参数
     */
    private Map<String, Object> startParams;
    
    /**
     * 工作流实例ID
     */
    private String workflowInstanceId;
    
    /**
     * 触发类型（如：SCHEDULED, MANUAL, TEST等）
     */
    private String triggerType;
    
    /**
     * 额外的日志信息
     */
    private Map<String, Object> extraLogInfo;
}