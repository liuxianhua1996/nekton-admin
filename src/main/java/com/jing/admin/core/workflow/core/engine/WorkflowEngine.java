package com.jing.admin.core.workflow.core.engine;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.exception.NodeExecutor;
import com.jing.admin.model.domain.WorkflowNodeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 工作流执行引擎
 * 负责工作流的执行流程控制
 */
@Component
@Slf4j
public class WorkflowEngine {

    @Autowired
    private List<NodeExecutor> nodeExecutors;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行工作流
     *
     * @param workflowDefinition 工作流定义
     * @param context            工作流执行上下文
     * @return 执行结果
     */
    public WorkflowExecutionResult execute(WorkflowDefinition workflowDefinition, WorkflowContext context) {
        return execute(workflowDefinition, context, null);
    }

    /**
     * 执行工作流（支持回调）
     *
     * @param workflowDefinition 工作流定义
     * @param context            工作流执行上下文
     * @param callback           执行回调接口
     * @return 执行结果
     */
    public WorkflowExecutionResult execute(WorkflowDefinition workflowDefinition, WorkflowContext context, com.jing.admin.core.workflow.WorkflowExecutionCallback callback) {
        // 设置工作流实例ID
        if (context.getInstanceId() == null || context.getInstanceId().isEmpty()) {
            context.setInstanceId(UUID.randomUUID().toString());
        }

        // 设置工作流定义ID
        context.setDefinitionId(workflowDefinition.hashCode() + "");

        // 获取起始节点
        NodeDefinition currentNode = workflowDefinition.getStartNode();
        if (currentNode == null) {
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("未找到起始节点");
            return new WorkflowExecutionResult(false, context, "未找到起始节点");
        }

        // 设置当前节点
        context.setCurrentNodeId(currentNode.getId());
        context.setCurrentNodeName(currentNode.getData().getLabel());

        try {
            // 遍历执行节点
            while (currentNode != null) {
                // 执行当前节点
                log.info("[工作流执行][{}]: {}", context.getDefinitionId(), currentNode.getData().getLabel());
                NodeExecutionResult nodeResult = executeNode(currentNode, context, callback);
                // 如果节点执行失败，终止工作流
                if (!nodeResult.isSuccess()) {
                    log.info("[工作流执行][{}] 执行失败: {}",context.getDefinitionId(),currentNode.getData().getLabel(), nodeResult.getErrorMessage());
                    context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
                    context.setErrorMessage("节点 " + currentNode.getId() + " 执行失败: " + nodeResult.getErrorMessage());
                    context.setNodeResult(currentNode.getId(), NodeResult.builder()
                            .nodeType(currentNode.getType())
                            .nodeName(currentNode.getData().getLabel())
                            .nodeId(currentNode.getId())
                            .inputData(nodeResult.getInputData())
                            .executeResult(nodeResult.getData())
                            .errorMessage(nodeResult.getErrorMessage())
                            .build());
                    return new WorkflowExecutionResult(false, context, nodeResult.getErrorMessage());
                }
                log.info("[工作流执行][{}] 执行成功: {}",context.getDefinitionId(),currentNode.getData().getLabel(), JSON.toJSONString(nodeResult.getData()));
                context.setVariable(currentNode.getId(), nodeResult);
                context.setNodeResult(currentNode.getId(), NodeResult.builder()
                        .nodeType(currentNode.getType())
                        .nodeName(currentNode.getData().getLabel())
                        .nodeId(currentNode.getId())
                        .inputData(nodeResult.getInputData())
                        .executeResult(nodeResult.getData())
                        .build());

                // 获取下一个节点（支持条件分支）
                currentNode = workflowDefinition.getNextNodeConditional(currentNode.getId(), context);
                if (currentNode != null) {
                    context.setCurrentNodeId(currentNode.getId());
                }
            }

            // 工作流执行完成
            context.setStatus(WorkflowContext.WorkflowStatus.COMPLETED);
            return new WorkflowExecutionResult(true, context, "工作流执行完成");
        } catch (Exception e) {
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流执行异常: " + e.getMessage());
            log.error("工作流执行异常 {} 执行失败: {}",currentNode.getData().getLabel(), e.getMessage());
            return new WorkflowExecutionResult(false, context, "工作流执行异常: " + e.getMessage());
        }
    }

    /**
     * 执行单个节点
     *
     * @param nodeDefinition 节点定义
     * @param context        工作流执行上下文
     * @return 节点执行结果
     */
    private NodeExecutionResult executeNode(NodeDefinition nodeDefinition, WorkflowContext context) {
        return executeNode(nodeDefinition, context, null);
    }

    /**
     * 执行单个节点（支持回调）
     *
     * @param nodeDefinition 节点定义
     * @param context        工作流执行上下文
     * @param callback       执行回调接口
     * @return 节点执行结果
     */
    private NodeExecutionResult executeNode(NodeDefinition nodeDefinition, WorkflowContext context, com.jing.admin.core.workflow.WorkflowExecutionCallback callback) {
        long startTime = System.currentTimeMillis();
        String nodeType = nodeDefinition.getData() != null ? nodeDefinition.getData().getType() : null;
        String nodeName = nodeDefinition.getData() != null ? nodeDefinition.getData().getLabel() : "Unknown";

        // 如果有回调，在执行前调用
        if (callback != null) {
            NodeResult nodeResult = com.jing.admin.core.workflow.model.NodeResult.builder()
                    .nodeId(nodeDefinition.getId())
                    .nodeName(nodeName)
                    .startTime(startTime)
                    .nodeType(nodeType)
                    .success(true)
                    .executeResult(null)
                    .errorMessage(null)
                    .build();
            callback.onExecutionProgress(nodeResult, com.jing.admin.core.workflow.WorkflowExecutionCallback.ExecutionStatus.BEFORE_EXECUTION);
        }
        NodeExecutionResult result;

        try {
            // 获取节点类型
            if (nodeType == null) {
                result = NodeExecutionResult.failure("节点类型为空");
            } else {
                // 查找支持该节点类型的执行器
                NodeExecutor executor = nodeExecutors.stream()
                        .filter(e -> e.supports(nodeType))
                        .findFirst()
                        .orElse(null);

                if (executor == null) {
                    result = NodeExecutionResult.failure("未找到支持节点类型 " + nodeType + " 的执行器");
                } else {
                    // 执行节点
                    result = executor.execute(nodeDefinition, context);
                }
            }
        } catch (Exception e) {
            log.error("节点执行异常: {}",  e.getMessage());
            result = NodeExecutionResult.failure("节点执行异常: " + e.getMessage());
            // 如果有回调，在错误时调用
            if (callback != null) {
                NodeResult nodeResult = com.jing.admin.core.workflow.model.NodeResult.builder()
                        .nodeId(nodeDefinition.getId())
                        .nodeName(nodeName)
                        .nodeType(nodeType)
                        .sort(0) // 可能需要从context获取当前执行顺序
                        .success(false)
                        .executeResult(null)
                        .errorMessage(e.getMessage())
                        .build();
                callback.onExecutionProgress(nodeResult, com.jing.admin.core.workflow.WorkflowExecutionCallback.ExecutionStatus.ERROR);
            }
        }

        // 记录节点执行结束日志
        long endTime = System.currentTimeMillis();

        // 如果有回调，在执行后调用
        if (callback != null) {
            com.jing.admin.core.workflow.model.NodeResult nodeResult = com.jing.admin.core.workflow.model.NodeResult.builder()
                    .nodeId(nodeDefinition.getId())
                    .nodeName(nodeName)
                    .nodeType(nodeType)
                    .endTime(endTime)
                    .inputData(result.getInputData())
                    .success(result.isSuccess())
                    .executeResult(result.getData())
                    .errorMessage(result.getErrorMessage())
                    .build();
            callback.onExecutionProgress(nodeResult, com.jing.admin.core.workflow.WorkflowExecutionCallback.ExecutionStatus.AFTER_EXECUTION);
        }

        return result;
    }
}