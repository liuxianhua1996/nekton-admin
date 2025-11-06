package com.jing.admin.core.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.AdminApplication;
import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.conversion.WorkflowJsonConverter;
import com.jing.admin.core.workflow.definition.WorkflowDefinition;
import com.jing.admin.core.workflow.engine.WorkflowEngine;
import com.jing.admin.core.workflow.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.engine.WorkflowExecutor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.info("----------------------------------------");
            log.info("开始执行工作流...");
            // 执行工作流
            WorkflowExecutionResult result = workflowExecutor.executeFromJson(workflowJson);
            if (result.getContext() != null) {
                WorkflowContext workflowContext = result.getContext();
                log.info("工作流实例ID: " + workflowContext.getInstanceId());
                log.info("工作流状态: " + workflowContext.getStatus());
                
                if (workflowContext.getNodeResults() != null && !workflowContext.getNodeResults().isEmpty()) {
                    log.info("\n节点执行结果:");
                    workflowContext.getNodeResults().forEach((nodeId, nodeResult) -> {
                        log.info("节点ID: {} 节点名称: {} 结果: {}", nodeResult.getNodeId(),nodeResult.getNodeName(),nodeResult.getExecuteResult());
                    });
                }
                
                if (workflowContext.getVariables() != null && !workflowContext.getVariables().isEmpty()) {
                    log.info("\n工作流变量:");
                    workflowContext.getVariables().forEach((key, value) -> {
                        log.info("变量名: " + key + ", 值: " + value);
                    });
                }
            }

            // 输出执行结果
            log.info("工作流执行结果:");
            log.info("执行状态: " + (result.isSuccess() ? "成功" : "失败"));
            log.info("执行消息: " + result.getMessage());
            log.info("----------------------------------------");
        } catch (IOException e) {
            System.err.println("读取工作流JSON文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}