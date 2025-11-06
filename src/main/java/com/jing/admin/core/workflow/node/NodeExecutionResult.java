package com.jing.admin.core.workflow.node;

/**
 * 节点执行结果类
 * 用于存储节点执行的结果和状态
 */
public class NodeExecutionResult {
    
    /**
     * 执行是否成功
     */
    private boolean success;
    
    /**
     * 执行结果数据
     */
    private Object data;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行耗时（毫秒）
     */
    private long executionTime;
    
    public NodeExecutionResult() {
    }
    
    public NodeExecutionResult(boolean success) {
        this.success = success;
    }
    
    public NodeExecutionResult(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }
    
    public NodeExecutionResult(boolean success, Object data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 创建成功的执行结果
     */
    public static NodeExecutionResult success(Object data) {
        return new NodeExecutionResult(true, data);
    }
    
    /**
     * 创建成功的执行结果（无数据）
     */
    public static NodeExecutionResult success() {
        return new NodeExecutionResult(true);
    }
    
    /**
     * 创建失败的执行结果
     */
    public static NodeExecutionResult failure(String errorMessage) {
        return new NodeExecutionResult(false, null, errorMessage);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}