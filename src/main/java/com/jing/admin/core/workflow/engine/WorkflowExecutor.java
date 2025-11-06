package com.jing.admin.core.workflow.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.conversion.WorkflowJsonConverter;
import com.jing.admin.core.workflow.definition.WorkflowDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * 工作流执行器
 * 提供工作流执行的高级接口
 */
@Component
public class WorkflowExecutor {
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 从JSON字符串执行工作流
     * 
     * @param workflowJson 工作流JSON字符串
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(String workflowJson) {
        try {
            // 转换JSON为工作流定义
            WorkflowDefinition workflowDefinition = WorkflowJsonConverter.convertFromJson(workflowJson);
            
            // 创建工作流执行上下文
            WorkflowContext context = new WorkflowContext();
            
            // 执行工作流
            return workflowEngine.execute(workflowDefinition, context);
        } catch (IOException e) {
            WorkflowContext context = new WorkflowContext();
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流JSON解析失败: " + e.getMessage());
            
            return new WorkflowExecutionResult(false, context, "工作流JSON解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 从JSON文件执行工作流
     * 
     * @param jsonFilePath JSON文件路径
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonFile(String jsonFilePath) {
        try {
            // 读取JSON文件
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
            if (inputStream == null) {
                throw new IOException("无法找到工作流JSON文件: " + jsonFilePath);
            }
            
            String workflowJson = objectMapper.readValue(inputStream, String.class);
            
            // 执行工作流
            return executeFromJson(workflowJson);
        } catch (IOException e) {
            WorkflowContext context = new WorkflowContext();
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("读取工作流JSON文件失败: " + e.getMessage());
            
            return new WorkflowExecutionResult(false, context, "读取工作流JSON文件失败: " + e.getMessage());
        }
    }
}