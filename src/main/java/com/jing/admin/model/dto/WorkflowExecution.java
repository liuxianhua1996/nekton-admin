package com.jing.admin.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 工作流执行请求DTO
 */
@Data
@Builder
public class WorkflowExecution {
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
    
    /**
     * 构造函数
     */
    public WorkflowExecution(String workflowId, Map<String, Object> startParams,
                             String workflowInstanceId, String triggerType, Map<String, Object> extraLogInfo) {
        this.workflowId = workflowId;
        this.startParams = startParams;
        this.workflowInstanceId = workflowInstanceId;
        this.triggerType = triggerType;
        this.extraLogInfo = extraLogInfo;
    }
    
    /**
     * 默认构造函数
     */
    public WorkflowExecution() {
    }
    
    // Getter和Setter方法由Lombok的@Data注解自动生成
}