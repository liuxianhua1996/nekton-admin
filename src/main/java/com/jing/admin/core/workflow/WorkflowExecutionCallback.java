package com.jing.admin.core.workflow;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.NodeResult;

/**
 * 工作流执行回调接口，用于处理执行结果和执行过程
 */
public interface WorkflowExecutionCallback {
    
    /**
     * 执行完成后的回调方法
     * @param result 执行结果
     */
    void onExecutionComplete(WorkflowExecutionResult result);
    
    /**
     * 执行过程中的回调方法，节点执行前后都会调用
     * @param nodeResult 节点执行结果
     * @param status 节点状态 (BEFORE_EXECUTION, AFTER_EXECUTION, ERROR)
     */
    void onExecutionProgress(NodeResult nodeResult, ExecutionStatus status);
    
    /**
     * 执行状态枚举
     */
    enum ExecutionStatus {
        BEFORE_EXECUTION,    // 节点执行前
        AFTER_EXECUTION,     // 节点执行后
        ERROR                // 节点执行错误
    }
}