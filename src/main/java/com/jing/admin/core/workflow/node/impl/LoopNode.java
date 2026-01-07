package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeData;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 循环节点处理器
 * 根据循环类型（数组循环或对象循环）遍历数据
 */
@Slf4j
public class LoopNode extends BaseNode {

    public LoopNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        try {
            // 获取节点数据
            NodeData nodeData = nodeDefinition.getData();
            if (nodeData == null || nodeData.getContent() == null) {
                throw new RuntimeException("循环节点数据为空");
            }

            // 获取循环参数
            Map<String, Object> loopParams = nodeData.getContent().getLoopParams();
            
            if (loopParams == null) {
                throw new RuntimeException("循环节点参数为空");
            }

            String loopType = (String) loopParams.get("loopType");
            String loopValue = (String) loopParams.get("loopValue");
            String loopVariable = (String) loopParams.get("loopVariable");
            String loopKey = (String) loopParams.get("loopKey");
            List<Map<String, Object>> customOutputFields = (List<Map<String, Object>>) loopParams.get("customOutputFields");
            
            // 获取循环数据
            Object loopData = parameterConverter.convertParameter(loopValue, "column", context);
            
            if (loopData == null) {
                log.warn("循环数据为空，跳过循环执行");
                Map<String, Object> outputData = new HashMap<>();
                outputData.put("results", new ArrayList<>());
                outputData.put("count", 0);
                
                long executionTime = System.currentTimeMillis() - startTime;
                NodeExecutionResult result = NodeExecutionResult.success(outputData);
                result.setExecutionTime(executionTime);
                return result;
            }

            // 收集循环依赖数据，以便WorkflowExecutor处理
            Map<String, Object> outputData = new HashMap<>();
            outputData.put("loopType", loopType);
            outputData.put("loopValue", loopValue);
            outputData.put("loopVariable", loopVariable);
            outputData.put("loopKey", loopKey);
            outputData.put("customOutputFields", customOutputFields);
            outputData.put("loopData", loopData);
            
            // 将循环参数返回，用于WorkflowExecutor进行后续处理
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success(outputData);
            result.setExecutionTime(executionTime);
            return result;

        } catch (Exception e) {
            log.error("循环节点执行异常: {}", e.getMessage(), e);
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("循环节点执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            return result;
        }
    }

    @Override
    public boolean supports(String nodeType) {
        return "loop".equalsIgnoreCase(nodeType);
    }
}