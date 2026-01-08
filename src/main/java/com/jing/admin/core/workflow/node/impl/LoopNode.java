package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeData;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.node.BaseNode;
import com.jing.admin.core.workflow.exception.NodeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 循环节点处理器
 * 根据循环类型（数组循环或对象循环）遍历数据
 */
@Component
@Slf4j
public class LoopNode extends BaseNode implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public LoopNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context, WorkflowDefinition workflowDefinition) {
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
            Integer maxIterationsObj = (Integer) loopParams.get("maxIterations");
            int maxIterations = maxIterationsObj != null ? maxIterationsObj : Integer.MAX_VALUE;
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

            // 确保循环数据是可遍历的类型
            List<Object> iterableData = convertToIterable(loopData);
            
            if (iterableData == null || iterableData.isEmpty()) {
                log.warn("循环数据为空或无法遍历，跳过循环执行");
                Map<String, Object> outputData = new HashMap<>();
                outputData.put("results", new ArrayList<>());
                outputData.put("count", 0);
                
                long executionTime = System.currentTimeMillis() - startTime;
                NodeExecutionResult result = NodeExecutionResult.success(outputData);
                result.setExecutionTime(executionTime);
                return result;
            }

            // 获取循环的子节点
            List<NodeDefinition> childNodes = workflowDefinition.getChildNodes(nodeDefinition.getId());
            if (childNodes == null || childNodes.isEmpty()) {
                log.warn("循环节点没有子节点，直接返回循环数据");
                Map<String, Object> outputData = new HashMap<>();
                outputData.put("results", new ArrayList<>());
                outputData.put("count", 0);
                
                long executionTime = System.currentTimeMillis() - startTime;
                NodeExecutionResult result = NodeExecutionResult.success(outputData);
                result.setExecutionTime(executionTime);
                return result;
            }

            // 执行循环
            List<NodeExecutionResult> results = new ArrayList<>();
            int iterationCount = 0;
            
            for (Object item : iterableData) {
                // 检查最大迭代次数限制
                if (iterationCount >= maxIterations) {
                    log.info("达到最大迭代次数限制: {}", maxIterations);
                    break;
                }

                // 创建新的上下文副本，用于当前循环项
                WorkflowContext loopContext = new WorkflowContext();
                loopContext.setGlobalParams(context.getGlobalParams());
                loopContext.setInstanceId(context.getInstanceId());
                loopContext.setDefinitionId(context.getDefinitionId());
                loopContext.setVariables(new HashMap<>(context.getVariables()));
                loopContext.setNodeResults(new HashMap<>(context.getNodeResults()));

                // 将循环的当前迭代结果设置回主上下文，这样其他节点可以通过 {{循环节点id.迭代value}} 访问
                // 将当前迭代的数据设置到主上下文中，以节点ID为键
                Map<String, Object> currentIterationData = new HashMap<>();
                currentIterationData.put("item", item);
                currentIterationData.put("index", iterationCount);
                if (loopVariable != null && !loopVariable.isEmpty()) {
                    currentIterationData.put(loopVariable, item);
                }
                if (loopKey != null && !loopKey.isEmpty()) {
                    currentIterationData.put(loopKey, iterationCount);
                }
                NodeExecutionResult iterationResult = NodeExecutionResult.success(currentIterationData);
                loopContext.setVariable(nodeDefinition.getId(), iterationResult);
                // 执行循环的子节点
                long currStartTime = System.currentTimeMillis();
                NodeExecutionResult loopResult = executeChildNodes(childNodes, loopContext, workflowDefinition);
                loopResult.setStartTime(currStartTime);
                loopResult.setEndTime(System.currentTimeMillis());
                results.add(loopResult);
                iterationCount++;
            }
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success(results);
            result.setExecutionTime(executionTime);
            
            // 将最终结果也设置到上下文中，这样其他节点可以访问完整的循环结果
            context.setVariable(nodeDefinition.getId(), result);
            
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
     * 将对象转换为可遍历的列表
     */
    private List<Object> convertToIterable(Object data) {
        if (data instanceof List) {
            return (List<Object>) data;
        } else if (data instanceof Object[]) {
            return Arrays.asList((Object[]) data);
        } else if (data instanceof Collection) {
            return new ArrayList<>((Collection<?>) data);
        } else if (data instanceof Map) {
            // 对于Map，遍历其entrySet
            Map<?, ?> map = (Map<?, ?>) data;
            List<Object> result = new ArrayList<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Map<String, Object> entryMap = new HashMap<>();
                entryMap.put("key", entry.getKey());
                entryMap.put("value", entry.getValue());
                result.add(entryMap);
            }
            return result;
        } else {
            // 如果是单个对象，包装成列表
            List<Object> result = new ArrayList<>();
            result.add(data);
            return result;
        }
    }

    /**
     * 执行循环的所有子节点
     */
    private NodeExecutionResult executeChildNodes(List<NodeDefinition> childNodes, WorkflowContext context, WorkflowDefinition workflowDefinition) {
        List<Map> childResults = new ArrayList<>(childNodes.size());
        try {
            // 如果没有子节点，直接返回
            if (childNodes == null || childNodes.isEmpty()) {
                Map<String, Object> resultData = new HashMap<>();
                return NodeExecutionResult.success(resultData);
            }
            
            // 根据您的建议，查找loopStart节点作为起始节点
            NodeDefinition loopStartNode = null;
            for (NodeDefinition childNode : childNodes) {
                String nodeType = childNode.getData() != null ? childNode.getData().getType() : null;
                String nodeCode = childNode.getData() != null ? childNode.getData().getCode() : null;
                
                // 寻找loopStart类型的节点作为循环内部的起始节点
                if ("loopStart".equals(nodeType) || "loopStart".equals(nodeCode)) {
                    loopStartNode = childNode;
                    break;
                }
            }
            
            // 如果没有找到loopStart节点，返回错误
            if (loopStartNode == null) {
                return NodeExecutionResult.failure("循环内部未找到loopStart节点");
            }
            
            // 获取loopStart节点的下一个节点，因为loopStart节点只是标识节点，不需要执行
            NodeDefinition currentNode = workflowDefinition.getNextNode(loopStartNode.getId());
            Set<String> processedNodes = new HashSet<>();
            
            while (currentNode != null) {
                // 防止无限循环
                if (processedNodes.contains(currentNode.getId())) {
                    log.warn("检测到循环依赖，停止执行: {}", currentNode.getId());
                    break;
                }
                
                // 确保当前节点是循环的子节点
                boolean isChild = false;
                for (NodeDefinition childNode : childNodes) {
                    if (childNode.getId().equals(currentNode.getId())) {
                        isChild = true;
                        break;
                    }
                }
                
                if (!isChild) {
                    // 如果当前节点不是循环的子节点，则停止执行
                    break;
                }
                
                // 执行当前节点
                long startTime = System.currentTimeMillis();
                NodeExecutionResult nodeResult = executeCurrentNode(currentNode, context, workflowDefinition);
                long endTime = System.currentTimeMillis();
                nodeResult.setStartTime(startTime);
                nodeResult.setEndTime(endTime);
                // 记录节点执行结果
                context.setVariable(currentNode.getId(), nodeResult);
                processedNodes.add(currentNode.getId());
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("nodeId",currentNode.getId());
                resultMap.put("nodeType",currentNode.getData().getCode());
                resultMap.put("nodeName",currentNode.getData().getLabel());
                resultMap.put("nodeResult",nodeResult);
                childResults.add(resultMap);
                if (!nodeResult.isSuccess()) {
                    break; // 如果节点执行失败，返回失败结果
                }
                // 获取下一个节点（根据边的连接关系）
                currentNode = workflowDefinition.getNextNode(currentNode.getId());

            }
            return NodeExecutionResult.success(childResults);
        } catch (Exception e) {
            log.error("执行循环子节点异常: {}", e.getMessage(), e);
            return NodeExecutionResult.failure("执行循环子节点失败: " + e.getMessage());
        }
    }

    /**
     * 执行当前节点
     * 通过Spring上下文获取已注册的执行器
     */
    private NodeExecutionResult executeCurrentNode(NodeDefinition nodeDefinition, WorkflowContext context, WorkflowDefinition workflowDefinition) {
        String nodeType = nodeDefinition.getData() != null ? nodeDefinition.getData().getType() : null;

        if (nodeType == null) {
            return NodeExecutionResult.failure("节点类型为空");
        }

        // 使用统一的助手类来执行节点
        return com.jing.admin.core.workflow.core.executor.NodeExecutorHelper.executeNode(
            nodeType, nodeDefinition, context, workflowDefinition, applicationContext);
    }



    @Override
    public boolean supports(String nodeType) {
        return "loop".equalsIgnoreCase(nodeType);
    }
}