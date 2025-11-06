package com.jing.admin.core.workflow.model;

import java.util.List;

/**
 * 工作流定义类
 * 用于表示整个工作流的结构，包含节点和边
 */
public class WorkflowDefinition {
    
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
    
    public List<NodeDefinition> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<NodeDefinition> nodes) {
        this.nodes = nodes;
    }
    
    public List<EdgeDefinition> getEdges() {
        return edges;
    }
    
    public void setEdges(List<EdgeDefinition> edges) {
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
}