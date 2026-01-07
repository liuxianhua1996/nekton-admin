package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.*;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;
import com.jing.admin.core.workflow.sdk.ISdkClient;
import com.jing.admin.core.workflow.sdk.SdkManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * SDK节点处理器
 * 处理系统集成类型的节点
 */
@Component
public class SdkNode extends BaseNode {

    public SdkNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context, WorkflowDefinition workflowDefinition) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> processedParams = new HashMap<>(1);
        try {
            // 获取节点数据
            NodeData nodeData = nodeDefinition.getData();
            if (nodeData == null || nodeData.getContent() == null) {
                throw new RuntimeException("SDK节点数据为空");
            }

            // 获取SDK参数
            Map<String, Object> sdkParams = nodeData.getContent().getSdkParams();
            if (sdkParams == null) {
                throw new RuntimeException("SDK参数为空");
            }

            // 处理参数中的引用
            processedParams = processSdkParams(sdkParams, context);

            // 获取系统和方法信息
            String system = (String) processedParams.get("system");
            String method = (String) processedParams.get("method");

            if (system == null || method == null) {
                throw new RuntimeException("系统或方法参数为空");
            }
            Object result = executeSdkCall(system, method, processedParams, context);
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult executionResult = NodeExecutionResult.success(result);
            executionResult.setExecutionTime(executionTime);
            executionResult.setInputData(processedParams);
            return executionResult;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            result.setInputData(processedParams);
            return result;
        }
    }

    /**
     * 处理SDK参数中的引用
     *
     * @param sdkParams 原始SDK参数
     * @param context   工作流执行上下文
     * @return 处理后的SDK参数
     */
    private Map<String, Object> processSdkParams(Map<String, Object> sdkParams, WorkflowContext context) {
        Map<String, Object> processedParams = new HashMap<>();

        // 处理系统和方法参数
        processedParams.put("system", sdkParams.get("system"));
        processedParams.put("method", sdkParams.get("method"));
        processedParams.put("apiKeyId", sdkParams.get("apiKeyId"));
        Map newParams = new HashMap();
        // 处理params参数
        Map<String, Object> params = (Map<String, Object>) sdkParams.get("params");
        processedParams.put("params", newParams);
        if (params != null) {

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String paramName = entry.getKey();
                Map<String, Object> paramDetails = (Map<String, Object>) entry.getValue();
                // 获取参数值和类型
                Object value = paramDetails.get("value");
                String valueType = (String) paramDetails.get("valueType");
                // 使用参数转换器处理参数值
                Object convertedValue = parameterConverter.convertParameter(value, valueType, context);
                // 创建新的参数详情
                Map<String, Object> newParamDetails = new HashMap<>(paramDetails);
                newParamDetails.put("value", convertedValue);
                newParams.put(paramName, convertedValue);
            }
        }
        return processedParams;
    }

    /**
     * 执行SDK调用
     * 根据系统标识获取对应的SDK客户端并执行方法
     */
    private Object executeSdkCall(String system, String method, Map<String, Object> params, WorkflowContext context) {
        // 获取对应的SDK客户端
        ISdkClient sdkClient = SdkManager.getSdkClient(system);
        if (sdkClient == null) {
            // 如果没有找到对应的SDK客户端，返回错误信息
            throw new RuntimeException("不支持的系统类型: " + system);
        }
        String apiKey = (String) params.get("apiKeyId");
        GlobalParams globalParams = context.getGlobalParams().get(apiKey);
        Optional.of(globalParams);
        // 准备调用参数
        Map<String, Object> methodParams = new HashMap<>();
        if (params.containsKey("params")) {
            methodParams = (Map<String, Object>) params.get("params");
        }

        // 执行SDK调用
        return sdkClient.execute(method, methodParams, globalParams);
    }

    @Override
    public boolean supports(String nodeType) {
        return "sdk".equals(nodeType);
    }
}