package com.jing.admin.core.workflow.core.engine;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.WorkflowExecutionCallback;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.exception.NodeExecutor;
import com.jing.admin.model.domain.WorkflowNodeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 工作流执行引擎
 * 负责工作流的执行流程控制
 */
@Component
@Slf4j
public class WorkflowEngine implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

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
                NodeExecutionResult nodeResult = executeNode(currentNode, context, workflowDefinition, callback);
                // 如果节点执行失败，终止工作流
                if (!nodeResult.isSuccess()) {
                    log.info("[工作流执行][{}] 执行失败: {}", context.getDefinitionId(), currentNode.getData().getLabel(), nodeResult.getErrorMessage());
                    context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
                    context.setErrorMessage("节点 " + currentNode.getId() + " 执行失败: " + nodeResult.getErrorMessage());
                    context.setNodeResult(currentNode.getId(), NodeResult.builder()
                            .nodeType(currentNode.getType())
                            .nodeName(currentNode.getData().getLabel())
                            .nodeId(currentNode.getId())
                            .startTime(nodeResult.getStartTime())
                            .endTime(nodeResult.getEndTime())
                            .inputData(nodeResult.getInputData())
                            .executeResult(nodeResult.getData())
                            .errorMessage(nodeResult.getErrorMessage())
                            .build());
                    return new WorkflowExecutionResult(false, context, nodeResult.getErrorMessage());
                }
                log.info("[工作流执行][{}] 执行成功: {}", context.getDefinitionId(), currentNode.getData().getLabel(), JSON.toJSONString(nodeResult.getData()));
                context.setVariable(currentNode.getId(), nodeResult);
                context.setNodeResult(currentNode.getId(), NodeResult.builder()
                        .nodeType(currentNode.getType())
                        .nodeName(currentNode.getData().getLabel())
                        .nodeId(currentNode.getId())
                        .startTime(nodeResult.getStartTime())
                        .endTime(nodeResult.getEndTime())
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
            log.error("工作流执行异常 {} 执行失败: {}", currentNode.getData().getLabel(), e.getMessage());
            return new WorkflowExecutionResult(false, context, "工作流执行异常: " + e.getMessage());
        }
    }

    /**
     * 执行单个节点（支持回调）
     *
     * @param nodeDefinition 节点定义
     * @param context        工作流执行上下文
     * @param callback       执行回调接口
     * @return 节点执行结果
     */
    private NodeExecutionResult executeNode(NodeDefinition nodeDefinition, WorkflowContext context, WorkflowDefinition workflowDefinition, WorkflowExecutionCallback callback) {
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
                // 使用统一的助手类来执行节点
                result = com.jing.admin.core.workflow.core.executor.NodeExecutorHelper.executeNode(
                        nodeType, nodeDefinition, context, workflowDefinition, applicationContext);
            }
        } catch (Exception e) {
            log.error("节点执行异常: {}", e.getMessage());
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
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        return result;
    }
}