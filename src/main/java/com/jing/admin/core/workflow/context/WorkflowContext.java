package com.jing.admin.core.workflow.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流执行上下文
 * 用于存储工作流执行过程中的数据和状态
 */
public class WorkflowContext {
    
    /**
     * 工作流实例ID
     */
    private String instanceId;
    
    /**
     * 工作流定义ID
     */
    private String definitionId;
    
    /**
     * 当前执行的节点ID
     */
    private String currentNodeId;
    
    /**
     * 工作流执行状态
     */
    private WorkflowStatus status;
    
    /**
     * 工作流变量存储
     */
    private Map<String, Object> variables;
    
    /**
     * 节点执行结果存储
     */
    private Map<String, Object> nodeResults;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 开始时间
     */
    private Long startTime;
    
    /**
     * 结束时间
     */
    private Long endTime;
    
    public WorkflowContext() {
        this.variables = new HashMap<>();
        this.nodeResults = new HashMap<>();
        this.status = WorkflowStatus.CREATED;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getDefinitionId() {
        return definitionId;
    }
    
    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }
    
    public String getCurrentNodeId() {
        return currentNodeId;
    }
    
    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }
    
    public WorkflowStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public Map<String, Object> getNodeResults() {
        return nodeResults;
    }
    
    public void setNodeResults(Map<String, Object> nodeResults) {
        this.nodeResults = nodeResults;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    
    public Long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * 设置变量值
     */
    public void setVariable(String key, Object value) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put(key, value);
    }
    
    /**
     * 获取变量值
     */
    public Object getVariable(String key) {
        if (variables == null) {
            return null;
        }
        return variables.get(key);
    }
    
    /**
     * 设置节点执行结果
     */
    public void setNodeResult(String nodeId, Object result) {
        if (nodeResults == null) {
            nodeResults = new HashMap<>();
        }
        nodeResults.put(nodeId, result);
    }
    
    /**
     * 获取节点执行结果
     */
    public Object getNodeResult(String nodeId) {
        if (nodeResults == null) {
            return null;
        }
        return nodeResults.get(nodeId);
    }
    
    /**
     * 工作流状态枚举
     */
    public enum WorkflowStatus {
        CREATED,    // 已创建
        RUNNING,    // 运行中
        COMPLETED,  // 已完成
        FAILED,     // 执行失败
        TERMINATED  // 已终止
    }
}