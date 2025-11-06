package com.jing.admin.core.workflow.core.engine;

import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.exception.NodeExecutor;
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
        // 获取节点类型
        String nodeType = nodeDefinition.getData() != null ? nodeDefinition.getData().getType() : null;
        
        if (nodeType == null) {
            return NodeExecutionResult.failure("节点类型为空");
        }
        
        // 查找支持该节点类型的执行器
        NodeExecutor executor = nodeExecutors.stream()
                .filter(e -> e.supports(nodeType))
                .findFirst()
                .orElse(null);
        
        if (executor == null) {
            return NodeExecutionResult.failure("未找到支持节点类型 " + nodeType + " 的执行器");
        }
        
        // 执行节点
        return executor.execute(nodeDefinition, context);
    }
}