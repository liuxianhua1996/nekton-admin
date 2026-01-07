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

    /**
     * 处理自定义输出字段并添加到结果中
     */
    private void processCustomOutputFieldsForResult(Object item, List<Map<String, Object>> customOutputFields, 
                                          Map<String, Object> result, String loopVariable) {
        for (Map<String, Object> field : customOutputFields) {
            String fieldName = (String) field.get("name");
            
            if (item instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                Object fieldValue = itemMap.get(fieldName);
                
                // 将循环变量的子属性添加到结果中，如 value.a
                String variableName = loopVariable + "." + fieldName;
                result.put(variableName, fieldValue);
            } else if (item != null) {
                // 尝试通过反射获取对象属性
                try {
                    java.lang.reflect.Field fieldRef = item.getClass().getDeclaredField(fieldName);
                    fieldRef.setAccessible(true);
                    Object fieldValue = fieldRef.get(item);
                    
                    // 将循环变量的子属性添加到结果中，如 value.a
                    String variableName = loopVariable + "." + fieldName;
                    result.put(variableName, fieldValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.debug("无法通过反射获取对象属性: {}", fieldName);
                }
            }
        }
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
            String loopValueField = (String) loopParams.get("loopValueField");
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

            List<Object> results = new ArrayList<>();
            int maxIterations = (Integer) loopParams.getOrDefault("maxIterations", 100);

            if ("array".equals(loopType)) {
                // 数组循环
                results = processArrayLoop(loopData, loopVariable, loopKey, customOutputFields, context, maxIterations);
            } else if ("object".equals(loopType)) {
                // 对象循环
                results = processObjectLoop(loopData, loopVariable, loopKey, customOutputFields, context, maxIterations);
            } else {
                throw new RuntimeException("不支持的循环类型: " + loopType);
            }

            // 准备输出数据
            Map<String, Object> outputData = new HashMap<>();
            outputData.put("results", results);
            outputData.put("count", results.size());
            
            // 将最后一次循环的结果合并到输出数据中，以便后续节点可以访问循环变量
            if (!results.isEmpty()) {
                Object lastResult = results.get(results.size() - 1);
                if (lastResult instanceof Map) {
                    Map<String, Object> lastResultMap = (Map<String, Object>) lastResult;
                    // 将最后一次循环的变量添加到输出数据中
                    for (Map.Entry<String, Object> entry : lastResultMap.entrySet()) {
                        if (!"index".equals(entry.getKey()) && !"value".equals(entry.getKey())) {
                            outputData.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

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

    /**
     * 处理数组循环
     */
    private List<Object> processArrayLoop(Object loopData, String loopVariable, String loopKey, 
                                         List<Map<String, Object>> customOutputFields, 
                                         WorkflowContext context, int maxIterations) {
        List<Object> results = new ArrayList<>();
        
        List<?> listData;
        if (loopData instanceof List) {
            listData = (List<?>) loopData;
        } else if (loopData instanceof Object[]) {
            listData = Arrays.asList((Object[]) loopData);
        } else {
            // 尝试将其他类型转换为列表
            listData = Collections.singletonList(loopData);
        }

        int index = 0;
        for (Object item : listData) {
            if (index >= maxIterations) {
                log.warn("达到最大迭代次数限制: {}", maxIterations);
                break;
            }

            // 创建新的上下文，但不设置循环变量到上下文中，因为循环内部没有子节点执行
            // 这里只是准备循环结果数据
            WorkflowContext newContext = createNewContext(context);

            // 执行循环体（这里可以扩展以支持子节点）
            Map<String, Object> loopResult = new HashMap<>();
            loopResult.put("index", index);
            loopResult.put("value", item);
            // 将循环变量添加到结果中，以便ParameterConverter可以访问
            loopResult.put(loopVariable, item);
            if (loopKey != null && !loopKey.isEmpty()) {
                loopResult.put(loopKey, index);
            }
            
            // 处理自定义输出字段，将它们也添加到结果中
            if (customOutputFields != null && !customOutputFields.isEmpty()) {
                processCustomOutputFieldsForResult(item, customOutputFields, loopResult, loopVariable);
            }

            results.add(loopResult);
            index++;
        }

        return results;
    }

    /**
     * 处理对象循环
     */
    private List<Object> processObjectLoop(Object loopData, String loopVariable, String loopKey,
                                          List<Map<String, Object>> customOutputFields,
                                          WorkflowContext context, int maxIterations) {
        List<Object> results = new ArrayList<>();

        if (loopData instanceof Map) {
            Map<?, ?> mapData = (Map<?, ?>) loopData;
            int index = 0;

            for (Map.Entry<?, ?> entry : mapData.entrySet()) {
                if (index >= maxIterations) {
                    log.warn("达到最大迭代次数限制: {}", maxIterations);
                    break;
                }

                // 创建新的上下文，但不设置循环变量到上下文中，因为循环内部没有子节点执行
                // 这里只是准备循环结果数据
                WorkflowContext newContext = createNewContext(context);

                // 执行循环体（这里可以扩展以支持子节点）
                Map<String, Object> loopResult = new HashMap<>();
                loopResult.put("key", entry.getKey());
                loopResult.put("value", entry.getValue());
                // 将循环变量添加到结果中，以便ParameterConverter可以访问
                loopResult.put(loopVariable, entry.getValue());
                if (loopKey != null && !loopKey.isEmpty()) {
                    loopResult.put(loopKey, entry.getKey());
                }

                // 处理自定义输出字段，将它们也添加到结果中
                if (customOutputFields != null && !customOutputFields.isEmpty()) {
                    processCustomOutputFieldsForResult(entry.getValue(), customOutputFields, loopResult, loopVariable);
                }

                results.add(loopResult);
                index++;
            }
        } else {
            log.warn("对象循环的数据不是Map类型: {}", loopData.getClass().getName());
        }

        return results;
    }



    /**
     * 创建新的上下文并复制原始上下文的变量
     */
    private WorkflowContext createNewContext(WorkflowContext originalContext) {
        WorkflowContext newContext = new WorkflowContext();
        
        // 复制基本属性
        newContext.setInstanceId(originalContext.getInstanceId());
        newContext.setDefinitionId(originalContext.getDefinitionId());
        newContext.setCurrentNodeId(originalContext.getCurrentNodeId());
        newContext.setCurrentNodeName(originalContext.getCurrentNodeName());
        newContext.setStatus(originalContext.getStatus());
        newContext.setErrorMessage(originalContext.getErrorMessage());
        newContext.setStartTime(originalContext.getStartTime());
        newContext.setEndTime(originalContext.getEndTime());
        newContext.setGlobalParams(originalContext.getGlobalParams());
        
        // 复制变量
        if (originalContext.getVariables() != null) {
            for (Map.Entry<String, NodeExecutionResult> entry : originalContext.getVariables().entrySet()) {
                newContext.setVariable(entry.getKey(), entry.getValue());
            }
        }
        
        // 复制节点结果
        if (originalContext.getNodeResults() != null) {
            newContext.setNodeResults(new HashMap<>(originalContext.getNodeResults()));
        }
        
        return newContext;
    }

    @Override
    public boolean supports(String nodeType) {
        return "loop".equalsIgnoreCase(nodeType);
    }
}