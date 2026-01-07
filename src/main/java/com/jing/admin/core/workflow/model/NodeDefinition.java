package com.jing.admin.core.workflow.model;

import lombok.Data;

/**
 * 节点定义类
 * 用于表示工作流中的单个节点
 */
@Data
public class NodeDefinition {
    
    /**
     * 节点ID
     */
    private String id;
    
    /**
     * 节点类型
     */
    private String type;
    
    /**
     * 父节点ID（用于循环节点的子节点）
     */
    private String parentId;
    
    /**
     * 节点数据
     */
    private NodeData data;
}