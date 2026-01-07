package com.jing.admin.core.workflow.config;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工作流配置类
 * 配置工作流相关的Bean
 */
@Configuration
public class WorkflowConfig {
    
    @Bean
    public ParameterConverter parameterConverter() {
        return new ParameterConverter();
    }

    // NodeExecutor实现类现在通过@Component注解自动注册到Spring容器
    // 不再需要手动创建列表
}