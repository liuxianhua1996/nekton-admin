package com.jing.admin.core.workflow.processor.impl;

import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.definition.NodeDefinition;
import com.jing.admin.core.workflow.definition.NodeResult;
import com.jing.admin.core.workflow.node.NodeExecutionResult;
import com.jing.admin.core.workflow.node.NodeExecutor;
import org.springframework.stereotype.Component;

/**
 * 开始节点处理器
 * 处理工作流的开始节点
 */
@Component
public class StartNodeProcessor implements NodeExecutor {
    
    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 开始节点不需要特殊处理，只是标记工作流开始
            context.setStartTime(startTime);
            context.setStatus(WorkflowContext.WorkflowStatus.RUNNING);
            
            // 设置节点执行结果
            context.setNodeResult(nodeDefinition.getId(), NodeResult.builder()
                    .nodeId(nodeDefinition.getId()).nodeName(nodeDefinition.getData().getLabel()).executeResult("工作流已启动").build());
            
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success("工作流已启动");
            result.setExecutionTime(executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("启动节点执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            
            return result;
        }
    }
    
    @Override
    public boolean supports(String nodeType) {
        return "start".equals(nodeType);
    }
}