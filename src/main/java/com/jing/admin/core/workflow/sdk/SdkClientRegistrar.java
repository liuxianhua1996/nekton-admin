package com.jing.admin.core.workflow.sdk;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


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