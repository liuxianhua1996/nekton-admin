package com.jing.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        return interceptor;
    }

    @Bean
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator();
    }
    /**
     * 全局配置，由于多数据源原因 所以需要 代码配置
     **/
    @Scope("prototype")
    @Bean(name = "globalConfig")
    @ConfigurationProperties(prefix = "mybatis-plus.global-config")
    public GlobalConfig globalConfig(){
        GlobalConfig globalConfig = new GlobalConfig();
        return globalConfig;
    }
}

