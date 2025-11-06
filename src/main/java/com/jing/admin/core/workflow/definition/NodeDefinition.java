package com.jing.admin.core.workflow.definition;

import lombok.Data;

import java.util.Map;

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
     * 节点数据
     */
    private NodeData data;
}