package com.jing.admin.core.workflow.core.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.log.WorkflowNodeLogQueueService;
import com.jing.admin.core.workflow.log.WorkflowNodeLogTask;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.exception.NodeExecutor;
import com.jing.admin.model.domain.WorkflowNodeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 工作流执行引擎
 * 负责工作流的执行流程控制
 */
@Component
public class WorkflowEngine {

    @Autowired
    private List<NodeExecutor> nodeExecutors;
    
    @Autowired
    private WorkflowNodeLogQueueService workflowNodeLogQueueService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 执行工作流
     * 
     * @param workflowDefinition 工作流定义
     * @param context 工作流执行上下文
     * @return 执行结果
     */
    public WorkflowExecutionResult execute(WorkflowDefinition workflowDefinition, WorkflowContext context) {
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
                NodeExecutionResult nodeResult = executeNode(currentNode, context);
                
                // 如果节点执行失败，终止工作流
                if (!nodeResult.isSuccess()) {
                    context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
                    context.setErrorMessage("节点 " + currentNode.getId() + " 执行失败: " + nodeResult.getErrorMessage());
                    return new WorkflowExecutionResult(false, context, nodeResult.getErrorMessage());
                }
                
                // 获取下一个节点
                currentNode = workflowDefinition.getNextNode(currentNode.getId());
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
            return new WorkflowExecutionResult(false, context, "工作流执行异常: " + e.getMessage());
        }
    }
    
    /**
     * 执行单个节点
     * 
     * @param nodeDefinition 节点定义
     * @param context 工作流执行上下文
     * @return 节点执行结果
     */
    private NodeExecutionResult executeNode(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        String nodeType = nodeDefinition.getData() != null ? nodeDefinition.getData().getType() : null;
        String nodeName = nodeDefinition.getData() != null ? nodeDefinition.getData().getLabel() : "Unknown";
        
        // 记录节点开始执行日志
        WorkflowNodeLog nodeLog = new WorkflowNodeLog();
        nodeLog.setWorkflowInstanceId(context.getInstanceId());
        nodeLog.setWorkflowId(context.getDefinitionId());
        nodeLog.setNodeId(nodeDefinition.getId());
        nodeLog.setNodeName(nodeName);
        nodeLog.setNodeType(nodeType);
        nodeLog.setStatus("RUNNING");
        nodeLog.setStartTime(startTime);
        
        // 记录输入数据
        try {
            nodeLog.setInputData(objectMapper.writeValueAsString(context.getVariables()));
        } catch (JsonProcessingException e) {
            nodeLog.setInputData("无法序列化输入数据");
        }
        
        // 异步记录日志 - 添加到队列
        workflowNodeLogQueueService.addLogTask(new WorkflowNodeLogTask(nodeLog, WorkflowNodeLogTask.LogOperationType.INSERT));
        
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
            result = NodeExecutionResult.failure("节点执行异常: " + e.getMessage());
        }
        
        // 记录节点执行结束日志
        long endTime = System.currentTimeMillis();
        nodeLog.setEndTime(endTime);
        nodeLog.setExecutionTime(endTime - startTime);
        nodeLog.setStatus(result.isSuccess() ? "SUCCESS" : "FAILED");
        
        if (!result.isSuccess()) {
            nodeLog.setErrorMessage(result.getErrorMessage());
        }
        
        // 记录输出数据
        try {
            nodeLog.setOutputData(objectMapper.writeValueAsString(result.getData()));
        } catch (JsonProcessingException e) {
            nodeLog.setOutputData("无法序列化输出数据");
        }
        
        // 异步更新日志 - 添加到队列
        workflowNodeLogQueueService.addLogTask(new WorkflowNodeLogTask(nodeLog, WorkflowNodeLogTask.LogOperationType.UPDATE));
        
        return result;
    }
}