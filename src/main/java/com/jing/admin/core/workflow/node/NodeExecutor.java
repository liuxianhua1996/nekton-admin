package com.jing.admin.core.workflow.node;

import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.definition.NodeDefinition;

/**
 * 节点执行器接口
 * 定义了节点执行的基本方法
 */
public interface NodeExecutor {
    
    /**
     * 执行节点
     * 
     * @param nodeDefinition 节点定义
     * @param context 工作流执行上下文
     * @return 节点执行结果
     */
    NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context);
    
    /**
     * 判断是否支持执行指定类型的节点
     * 
     * @param nodeType 节点类型
     * @return 是否支持
     */
    boolean supports(String nodeType);
}