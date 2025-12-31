package com.jing.admin.service;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;

import java.util.Map;

/**
 * 工作流执行服务接口
 * 提供带日志和不带日志的工作流执行方法
 */
public interface WorkflowExecutionService {
    
    /**
     * 执行工作流（带日志记录）
     * 
     * @param workflowId 工作流ID
     * @param startParams 启动参数
     * @param triggerType 触发类型（如：SCHEDULED, MANUAL, TEST等）
     * @param extraLogInfo 额外的日志信息
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithLog(String workflowId, Map<String, Object> startParams, String triggerType, Map<String, Object> extraLogInfo);
    
    /**
     * 执行工作流（不记录日志，用于测试等场景）
     * 
     * @param workflowId 工作流ID
     * @param startParams 启动参数
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithoutLog(String workflowId, Map<String, Object> startParams);
    
    /**
     * 执行工作流（带日志记录，支持指定工作流实例ID）
     * 
     * @param workflowId 工作流ID
     * @param startParams 启动参数
     * @param workflowInstanceId 工作流实例ID
     * @param triggerType 触发类型
     * @param extraLogInfo 额外的日志信息
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithLog(String workflowId, Map<String, Object> startParams, String workflowInstanceId, String triggerType, Map<String, Object> extraLogInfo);
    
    /**
     * 执行工作流（不记录日志，支持指定工作流实例ID，用于测试等场景）
     * 
     * @param workflowId 工作流ID
     * @param startParams 启动参数
     * @param workflowInstanceId 工作流实例ID
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithoutLog(String workflowId, Map<String, Object> startParams, String workflowInstanceId);
    
    /**
     * 执行工作流（不记录日志，用于测试等场景，直接使用工作流数据）
     * 
     * @param workflowJson 工作流JSON数据
     * @param globalParams 全局参数
     * @param startParams 启动参数
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithoutLogByData(String workflowJson, Map<String, Object> globalParams, Map<String, Object> startParams);
}