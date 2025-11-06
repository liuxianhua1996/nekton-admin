package com.jing.admin.core.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.AdminApplication;
import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.conversion.WorkflowJsonConverter;
import com.jing.admin.core.workflow.definition.WorkflowDefinition;
import com.jing.admin.core.workflow.engine.WorkflowEngine;
import com.jing.admin.core.workflow.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.engine.WorkflowExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * 工作流测试类
 * 用于测试工作流执行引擎
 */
@Component
public class WorkflowTestApplication {
    @Autowired
    WorkflowExecutor workflowExecutor;
    
    public void test() {
        try {
            // 从resources目录读取workflow.json文件
            InputStream inputStream = WorkflowTestApplication.class.getClassLoader().getResourceAsStream("workflow.json");
            if (inputStream == null) {
                System.err.println("无法找到workflow.json文件");
                return;
            }
            
            // 读取JSON文件内容为字符串
            String workflowJson = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            
            System.out.println("开始执行工作流...");
            System.out.println("工作流JSON内容:");
            System.out.println(workflowJson);
            System.out.println("----------------------------------------");
            
            // 执行工作流
            WorkflowExecutionResult result = workflowExecutor.executeFromJson(workflowJson);
            
            // 输出执行结果
            System.out.println("工作流执行结果:");
            System.out.println("执行状态: " + (result.isSuccess() ? "成功" : "失败"));
            System.out.println("执行消息: " + result.getMessage());
            
            if (result.getContext() != null) {
                WorkflowContext workflowContext = result.getContext();
                System.out.println("工作流实例ID: " + workflowContext.getInstanceId());
                System.out.println("工作流状态: " + workflowContext.getStatus());
                
                if (workflowContext.getNodeResults() != null && !workflowContext.getNodeResults().isEmpty()) {
                    System.out.println("\n节点执行结果:");
                    workflowContext.getNodeResults().forEach((nodeId, nodeResult) -> {
                        System.out.println("节点ID: " + nodeId + ", 结果: " + nodeResult);
                    });
                }
                
                if (workflowContext.getVariables() != null && !workflowContext.getVariables().isEmpty()) {
                    System.out.println("\n工作流变量:");
                    workflowContext.getVariables().forEach((key, value) -> {
                        System.out.println("变量名: " + key + ", 值: " + value);
                    });
                }
            }
            
        } catch (IOException e) {
            System.err.println("读取工作流JSON文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}