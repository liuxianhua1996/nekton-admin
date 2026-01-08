package com.jing.admin.core.workflow.exception;

import lombok.Data;

import java.util.Map;

/**
 * 节点执行结果类
 * 用于存储节点执行的结果和状态
 */
@Data
public class NodeExecutionResult {
    
    /**
     * 执行是否成功
     */
    private boolean success;

    private long startTime;
    private long endTime;
    
    /**
     * 执行结果数据
     */
    private Object data;

    private Map<String, Object> inputData;

    private int sort;
    
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
}