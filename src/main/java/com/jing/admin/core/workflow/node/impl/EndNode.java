package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;

import java.util.HashMap;
import java.util.Map;

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
            long executionTime = System.currentTimeMillis() - startTime;
            Map<String, Object> outParams  = nodeDefinition.getData().getContent().getOutParams();
            outParams = processOutParams(outParams, context);
            NodeExecutionResult result = NodeExecutionResult.success("工作流已结束");
            result.setExecutionTime(executionTime);
            result.setInputData(new HashMap<>());
            result.setData(outParams);
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