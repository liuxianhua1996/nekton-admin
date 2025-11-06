package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;

/**
 * 结束节点处理器
 * 处理工作流的结束节点
 */
public class EndNode extends BaseNode {
    public EndNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 结束节点标记工作流结束
            context.setEndTime(System.currentTimeMillis());
            context.setStatus(WorkflowContext.WorkflowStatus.COMPLETED);
            
            // 设置节点执行结果
            context.setNodeResult(nodeDefinition.getId(), NodeResult.builder()
                    .nodeId(nodeDefinition.getId()).nodeName(nodeDefinition.getData().getLabel()).executeResult("工作流已结束").build());
            
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success("工作流已结束");
            result.setExecutionTime(executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("结束节点执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            
            return result;
        }
    }
    
    @Override
    public boolean supports(String nodeType) {
        return "end".equals(nodeType);
    }
}