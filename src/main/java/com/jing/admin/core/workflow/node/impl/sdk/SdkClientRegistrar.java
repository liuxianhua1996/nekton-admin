package com.jing.admin.core.workflow.node.impl.sdk;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * SDK客户端注册器
 * 用于在Spring容器中自动注册SDK客户端
 */
@Component
public class SdkClientRegistrar {
    
    @PostConstruct
    public void registerSdkClients() {
        // 初始化默认SDK客户端
        SdkManager.initializeDefaultClients();
    }
}