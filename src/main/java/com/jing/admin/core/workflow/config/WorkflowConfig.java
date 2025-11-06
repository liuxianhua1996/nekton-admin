package com.jing.admin.core.workflow.config;

import com.jing.admin.core.workflow.conversion.ParameterConverter;
import com.jing.admin.core.workflow.node.NodeExecutor;
import com.jing.admin.core.workflow.processor.impl.EndNodeProcessor;
import com.jing.admin.core.workflow.processor.impl.SdkNodeProcessor;
import com.jing.admin.core.workflow.processor.impl.StartNodeProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流配置类
 * 配置工作流相关的Bean
 */
@Configuration
public class WorkflowConfig {
    
    /**
     * 配置节点执行器列表
     */
    @Bean
    public List<NodeExecutor> nodeExecutors() {
        ParameterConverter parameterConverter = new ParameterConverter();
        List<NodeExecutor> executors = new ArrayList<>();
        executors.add(new StartNodeProcessor(parameterConverter));
        executors.add(new EndNodeProcessor(parameterConverter));
        executors.add(new SdkNodeProcessor(parameterConverter));
        return executors;
    }
}