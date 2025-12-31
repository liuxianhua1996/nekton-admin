package com.jing.admin.service;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.model.dto.WorkflowExecution;

import java.util.Map;

/**
 * 工作流执行服务接口
 * 提供带日志和不带日志的工作流执行方法
 */
public interface WorkflowExecutionService {
    
    /**
     * 执行工作流（带日志记录）
     * 
     * @param request 工作流执行请求对象
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithLog(WorkflowExecution request);
    
    /**
     * 执行工作流（不记录日志，用于测试等场景）
     * 
     * @param request 工作流执行请求对象
     * @return 执行结果
     */
    WorkflowExecutionResult executeWorkflowWithoutLog(WorkflowExecution request);
    
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