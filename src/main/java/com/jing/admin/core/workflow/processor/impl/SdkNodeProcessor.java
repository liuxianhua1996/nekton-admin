package com.jing.admin.core.workflow.processor.impl;

import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.definition.NodeData;
import com.jing.admin.core.workflow.definition.NodeDefinition;
import com.jing.admin.core.workflow.definition.NodeResult;
import com.jing.admin.core.workflow.node.NodeExecutionResult;
import com.jing.admin.core.workflow.node.NodeExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SDK节点处理器
 * 处理系统集成类型的节点
 */
@Component
public class SdkNodeProcessor implements NodeExecutor {
    
    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取节点数据
            NodeData nodeData = nodeDefinition.getData();
            if (nodeData == null || nodeData.getContent() == null) {
                return NodeExecutionResult.failure("SDK节点数据为空");
            }
            
            // 获取SDK参数
            Map<String, Object> sdkParams = nodeData.getContent().getSdkParams();
            if (sdkParams == null) {
                return NodeExecutionResult.failure("SDK参数为空");
            }
            
            // 获取系统和方法信息
            String system = (String) sdkParams.get("system");
            String method = (String) sdkParams.get("method");
            
            if (system == null || method == null) {
                return NodeExecutionResult.failure("系统或方法参数为空");
            }
            
            // 这里应该根据不同的系统和方法调用相应的SDK
            // 由于是示例，这里只做模拟处理
            Object result = executeSdkCall(system, method, sdkParams, context);
            
            // 设置节点执行结果
            context.setNodeResult(nodeDefinition.getId(), NodeResult.builder()
                    .nodeId(nodeDefinition.getId()).nodeName(nodeDefinition.getData().getLabel()).executeResult(result).build());
            
            // 将结果添加到上下文变量中
            if (nodeData.getContent().getOutParams() != null) {
                nodeData.getContent().getOutParams().forEach((key, param) -> {
                    context.setVariable(key, result);
                });
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult executionResult = NodeExecutionResult.success(result);
            executionResult.setExecutionTime(executionTime);
            
            return executionResult;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("SDK节点执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            
            return result;
        }
    }
    
    /**
     * 执行SDK调用
     * 这里是示例实现，实际应用中应该根据具体的系统和方法实现
     */
    private Object executeSdkCall(String system, String method, Map<String, Object> params, WorkflowContext context) {
        // 模拟SDK调用
        if ("kingdee_sky".equals(system) && "query".equals(method)) {
            // 模拟金蝶查询
            return "金蝶系统查询结果";
        }
        
        // 默认返回模拟结果
        return "SDK调用结果: " + system + "." + method;
    }
    
    @Override
    public boolean supports(String nodeType) {
        return "sdk".equals(nodeType);
    }
}