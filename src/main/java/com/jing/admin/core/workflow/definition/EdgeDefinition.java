package com.jing.admin.core.workflow.definition;

import java.util.Map;

/**
 * 边定义类
 * 用于表示工作流中节点之间的连接关系
 */
public class EdgeDefinition {
    
    /**
     * 边ID
     */
    private String id;
    
    /**
     * 边类型
     */
    private String type;
    
    /**
     * 源节点ID
     */
    private String source;
    
    /**
     * 源节点连接点ID
     */
    private String sourceHandle;
    
    /**
     * 目标节点ID
     */
    private String target;
    
    /**
     * 目标节点连接点ID
     */
    private String targetHandle;
    
    /**
     * 边样式
     */
    private Map<String, Object> style;
    
    public EdgeDefinition() {
    }
    
    public EdgeDefinition(String id, String source, String target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceHandle() {
        return sourceHandle;
    }
    
    public void setSourceHandle(String sourceHandle) {
        this.sourceHandle = sourceHandle;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getTargetHandle() {
        return targetHandle;
    }
    
    public void setTargetHandle(String targetHandle) {
        this.targetHandle = targetHandle;
    }
    
    public Map<String, Object> getStyle() {
        return style;
    }
    
    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }
}