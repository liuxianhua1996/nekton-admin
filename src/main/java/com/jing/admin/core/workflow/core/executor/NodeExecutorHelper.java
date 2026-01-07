package com.jing.admin.core.workflow.core.executor;

import com.jing.admin.core.workflow.exception.NodeExecutor;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 节点执行器助手类
 * 统一处理NodeExecutor的获取和执行逻辑
 */
public class NodeExecutorHelper {
    
    /**
     * 从Spring上下文获取支持指定节点类型的执行器并执行节点
     * 
     * @param nodeType 节点类型
     * @param nodeDefinition 节点定义
     * @param context 工作流上下文
     * @param workflowDefinition 工作流定义
     * @param applicationContext Spring应用上下文
     * @return 节点执行结果
     */
    public static NodeExecutionResult executeNode(String nodeType, 
                                                  NodeDefinition nodeDefinition, 
                                                  WorkflowContext context, 
                                                  WorkflowDefinition workflowDefinition,
                                                  ApplicationContext applicationContext) {
        if (nodeType == null) {
            return NodeExecutionResult.failure("节点类型为空");
        }

        // 从Spring容器中获取所有NodeExecutor类型的Bean
        // 遍历查找支持该节点类型的执行器
        if (applicationContext != null) {
            Map<String, NodeExecutor> executorBeans = applicationContext.getBeansOfType(NodeExecutor.class);
            for (NodeExecutor executor : executorBeans.values()) {
                if (executor.supports(nodeType)) {
                    return executor.execute(nodeDefinition, context, workflowDefinition);
                }
            }
        }

        // 如果Spring上下文中没有找到合适的执行器，返回错误
        return NodeExecutionResult.failure("未找到支持节点类型 " + nodeType + " 的执行器");
    }
    
    /**
     * 根据节点定义获取节点类型
     * 
     * @param nodeDefinition 节点定义
     * @return 节点类型
     */
    public static String getNodeType(NodeDefinition nodeDefinition) {
        return nodeDefinition.getData() != null ? nodeDefinition.getData().getType() : null;
    }
}