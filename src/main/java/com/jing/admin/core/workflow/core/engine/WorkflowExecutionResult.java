package com.jing.admin.core.workflow.core.engine;

import com.jing.admin.core.workflow.core.context.WorkflowContext;

/**
 * 工作流执行结果类
 * 用于存储工作流执行的结果和状态
 */
public class WorkflowExecutionResult {
    
    /**
     * 执行是否成功
     */
    private boolean success;
    
    /**
     * 工作流执行上下文
     */
    private WorkflowContext context;
    
    /**
     * 执行结果消息
     */
    private String message;
    
    public WorkflowExecutionResult() {
    }
    
    public WorkflowExecutionResult(boolean success, WorkflowContext context, String message) {
        this.success = success;
        this.context = context;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public WorkflowContext getContext() {
        return context;
    }
    
    public void setContext(WorkflowContext context) {
        this.context = context;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}