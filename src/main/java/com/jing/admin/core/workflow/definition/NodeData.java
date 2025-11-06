package com.jing.admin.core.workflow.definition;

import java.util.List;
import java.util.Map;

/**
 * 节点数据类
 * 用于存储节点的具体数据内容
 */
public class NodeData {
    
    /**
     * 节点图标URL
     */
    private String icon;
    
    /**
     * 节点标签
     */
    private String label;
    
    /**
     * 节点描述
     */
    private String description;
    
    /**
     * 节点类型（start, end, sdk等）
     */
    private String type;
    
    /**
     * 节点代码
     */
    private String code;
    
    /**
     * 节点内容
     */
    private NodeContent content;
    
    /**
     * 是否选中
     */
    private Boolean selected;
    
    /**
     * 是否有错误
     */
    private Boolean error;
    
    public NodeData() {
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public NodeContent getContent() {
        return content;
    }
    
    public void setContent(NodeContent content) {
        this.content = content;
    }
    
    public Boolean getSelected() {
        return selected;
    }
    
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
    public Boolean getError() {
        return error;
    }
    
    public void setError(Boolean error) {
        this.error = error;
    }
    
    /**
     * 节点内容类
     */
    public static class NodeContent {
        /**
         * 输入参数
         */
        private Map<String, Object> inParams;
        
        /**
         * 输出参数
         */
        private Map<String, Object> outParams;
        
        /**
         * 条件列表
         */
        private List<Object> conditions;
        
        /**
         * SDK参数（当节点类型为sdk时使用）
         */
        private Map<String, Object> sdkParams;
        
        public NodeContent() {
        }
        
        public Map<String, Object> getInParams() {
            return inParams;
        }
        
        public void setInParams(Map<String, Object> inParams) {
            this.inParams = inParams;
        }
        
        public Map<String, Object> getOutParams() {
            return outParams;
        }
        
        public void setOutParams(Map<String, Object> outParams) {
            this.outParams = outParams;
        }
        
        public List<Object> getConditions() {
            return conditions;
        }
        
        public void setConditions(List<Object> conditions) {
            this.conditions = conditions;
        }
        
        public Map<String, Object> getSdkParams() {
            return sdkParams;
        }
        
        public void setSdkParams(Map<String, Object> sdkParams) {
            this.sdkParams = sdkParams;
        }
    }
}