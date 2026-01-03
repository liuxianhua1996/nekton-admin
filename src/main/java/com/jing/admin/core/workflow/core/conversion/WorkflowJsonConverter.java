package com.jing.admin.core.workflow.core.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.model.EdgeDefinition;
import com.jing.admin.core.workflow.model.NodeData;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.WorkflowDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流JSON转换器
 * 用于将JSON格式的工作流定义转换为工作流定义对象
 */
public class WorkflowJsonConverter {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将JSON字符串转换为工作流定义对象
     * 
     * @param json JSON字符串
     * @return 工作流定义对象
     */
    public static WorkflowDefinition convertFromJson(String json) throws IOException {
        Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);
        
        // 转换节点列表
        List<Map<String, Object>> nodesJson = (List<Map<String, Object>>) jsonMap.get("nodes");
        List<NodeDefinition> nodes = new ArrayList<>();
        
        if (nodesJson != null) {
            for (Map<String, Object> nodeJson : nodesJson) {
                NodeDefinition node = convertNode(nodeJson);
                nodes.add(node);
            }
        }
        
        // 转换边列表
        List<Map<String, Object>> edgesJson = (List<Map<String, Object>>) jsonMap.get("edges");
        List<EdgeDefinition> edges = new ArrayList<>();
        
        if (edgesJson != null) {
            for (Map<String, Object> edgeJson : edgesJson) {
                EdgeDefinition edge = convertEdge(edgeJson);
                edges.add(edge);
            }
        }
        
        return new WorkflowDefinition(nodes, edges);
    }
    
    /**
     * 转换节点JSON为节点定义对象
     */
    private static NodeDefinition convertNode(Map<String, Object> nodeJson) {
        NodeDefinition node = new NodeDefinition();
        
        node.setId((String) nodeJson.get("id"));
        node.setType((String) nodeJson.get("type"));
        // 转换节点数据
        Map<String, Object> dataJson = (Map<String, Object>) nodeJson.get("data");
        if (dataJson != null) {
            NodeData data = convertNodeData(dataJson);
            node.setData(data);
        }
        
        return node;
    }
    
    /**
     * 转换节点数据JSON为节点数据对象
     */
    private static NodeData convertNodeData(Map<String, Object> dataJson) {
        NodeData data = new NodeData();
        
        data.setIcon((String) dataJson.get("icon"));
        data.setLabel((String) dataJson.get("label"));
        data.setDescription((String) dataJson.get("description"));
        data.setType((String) dataJson.get("type"));
        data.setCode((String) dataJson.get("code"));
        data.setSelected((Boolean) dataJson.get("selected"));
        data.setError((Boolean) dataJson.get("error"));
        
        // 转换节点内容
        Map<String, Object> contentJson = (Map<String, Object>) dataJson.get("content");
        if (contentJson != null) {
            NodeData.NodeContent content = convertNodeContent(contentJson);
            data.setContent(content);
        }
        
        return data;
    }
    
    /**
     * 转换节点内容JSON为节点内容对象
     */
    private  static NodeData.NodeContent convertNodeContent(Map<String, Object> contentJson) {
        NodeData.NodeContent content = new NodeData.NodeContent();
        
        content.setInParams((Map<String, Object>) contentJson.getOrDefault("inParams", new HashMap<>()));
        content.setOutParams((Map<String, Object>) contentJson.getOrDefault("outParams",new HashMap<>()));
        content.setConditions((List<Object>) contentJson.get("conditions"));
        content.setSdkParams((Map<String, Object>) contentJson.getOrDefault("sdkParams",new HashMap<>()));
        content.setCode((String) contentJson.getOrDefault("code",""));
        
        return content;
    }
    
    /**
     * 转换边JSON为边定义对象
     */
    private static EdgeDefinition convertEdge(Map<String, Object> edgeJson) {
        EdgeDefinition edge = new EdgeDefinition();
        
        edge.setId((String) edgeJson.get("id"));
        edge.setType((String) edgeJson.get("type"));
        edge.setSource((String) edgeJson.get("source"));
        edge.setSourceHandle((String) edgeJson.get("sourceHandle"));
        edge.setTarget((String) edgeJson.get("target"));
        edge.setTargetHandle((String) edgeJson.get("targetHandle"));
        edge.setStyle((Map<String, Object>) edgeJson.get("style"));
        
        return edge;
    }
}