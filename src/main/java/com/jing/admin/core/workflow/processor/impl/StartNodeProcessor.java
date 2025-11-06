package com.jing.admin.core.workflow.processor.impl;

import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.conversion.ParameterConverter;
import com.jing.admin.core.workflow.definition.NodeData;
import com.jing.admin.core.workflow.definition.NodeDefinition;
import com.jing.admin.core.workflow.definition.NodeResult;
import com.jing.admin.core.workflow.node.NodeExecutionResult;
import com.jing.admin.core.workflow.node.NodeExecutor;
import com.jing.admin.core.workflow.processor.BaseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 开始节点处理器
 * 处理工作流的开始节点
 */
public class StartNodeProcessor extends BaseProcessor {


    public StartNodeProcessor(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();

        try {
            // 开始节点不需要特殊处理，只是标记工作流开始
            context.setStartTime(startTime);
            context.setStatus(WorkflowContext.WorkflowStatus.RUNNING);

            // 处理输出参数
            NodeData nodeData = nodeDefinition.getData();
            Map<String, Object> outputData = new HashMap<>();
            outputData.put("test","heihei");
            // 设置节点执行结果
            context.setNodeResult(nodeDefinition.getId(), NodeResult.builder()
                    .nodeId(nodeDefinition.getId()).nodeName(nodeDefinition.getData().getLabel()).executeResult(outputData).build());

            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success(outputData);
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