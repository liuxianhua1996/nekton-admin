package com.jing.admin.core.workflow.context;

import com.jing.admin.core.workflow.definition.NodeResult;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流执行上下文
 * 用于存储工作流执行过程中的数据和状态
 */
@Data
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
    private String currentNodeName;
    
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
    private Map<String, NodeResult> nodeResults;
    
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
    public void setNodeResult(String nodeId, NodeResult result) {
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