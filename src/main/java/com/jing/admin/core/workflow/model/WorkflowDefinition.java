package com.jing.admin.core.workflow.model;

import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作流定义类
 * 用于表示整个工作流的结构，包含节点和边
 */
@Data
public class WorkflowDefinition {

    private Map<String, GlobalParams> globalParams;

    private Map startParams;
    
    /**
     * 工作流节点列表
     */
    private List<NodeDefinition> nodes;
    
    /**
     * 工作流边列表（节点之间的连接关系）
     */
    private List<EdgeDefinition> edges;
    
    public WorkflowDefinition() {
    }
    
    public WorkflowDefinition(List<NodeDefinition> nodes, List<EdgeDefinition> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
    
    /**
     * 根据节点ID获取节点定义
     */
    public NodeDefinition getNodeById(String nodeId) {
        if (nodes == null) {
            return null;
        }
        return nodes.stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取起始节点
     */
    public NodeDefinition getStartNode() {
        if (nodes == null) {
            return null;
        }
        return nodes.stream()
                .filter(node -> "start".equals(node.getData().getType()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取结束节点
     */
    public NodeDefinition getEndNode() {
        if (nodes == null) {
            return null;
        }
        return nodes.stream()
                .filter(node -> "end".equals(node.getData().getType()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取指定节点的下一个节点
     */
    public NodeDefinition getNextNode(String currentNodeId) {
        if (edges == null) {
            return null;
        }
        return edges.stream()
                .filter(edge -> currentNodeId.equals(edge.getSource()))
                .findFirst()
                .map(edge -> getNodeById(edge.getTarget()))
                .orElse(null);
    }
    
    /**
     * 根据当前节点ID和工作流上下文获取下一个节点（支持条件分支）
     * 
     * @param currentNodeId 当前节点ID
     * @param context 工作流执行上下文
     * @return 下一个节点定义
     */
    public NodeDefinition getNextNodeConditional(String currentNodeId, WorkflowContext context) {
        if (edges == null) {
            return null;
        }
        
        // 获取当前节点
        NodeDefinition currentNode = getNodeById(currentNodeId);
        if (currentNode == null) {
            return null;
        }
        
        // 如果当前节点是IF节点，需要根据条件判断结果选择分支
        if ("verify".equals(currentNode.getData().getType())) {
            // 从上下文中获取匹配的条件ID
            Object matchedConditionsObj = ((Map<String,Object>)context.getVariable(currentNode.getId()).getData()).get("matchedConditions");
            
            if (matchedConditionsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String,String>> matchedConditionIds = (List<Map<String,String>>) matchedConditionsObj;
                
                // 查找与匹配条件ID对应的边
                for (Map<String,String> condition : matchedConditionIds) {
                    String sourceHandle = "source-" + condition.get("conditionId");
                    
                    // 查找sourceHandle匹配的边
                    EdgeDefinition matchingEdge = edges.stream()
                            .filter(edge -> currentNodeId.equals(edge.getSource()) && 
                                          sourceHandle.equals(edge.getSourceHandle()))
                            .findFirst()
                            .orElse(null);
                    
                    if (matchingEdge != null) {
                        return getNodeById(matchingEdge.getTarget());
                    }
                }
            }
            return getEndNode();
        }
        
        // 对于非IF节点，使用普通逻辑
        return getNextNode(currentNodeId);
    }
}