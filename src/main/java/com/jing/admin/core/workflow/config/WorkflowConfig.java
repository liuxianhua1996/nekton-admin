package com.jing.admin.core.workflow.config;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.exception.NodeExecutor;
import com.jing.admin.core.workflow.node.impl.EndNode;
import com.jing.admin.core.workflow.node.impl.SdkNode;
import com.jing.admin.core.workflow.node.impl.StartNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        executors.add(new StartNode(parameterConverter));
        executors.add(new EndNode(parameterConverter));
        executors.add(new SdkNode(parameterConverter));
        return executors;
    }
}