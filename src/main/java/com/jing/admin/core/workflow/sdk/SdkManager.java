package com.jing.admin.core.workflow.sdk;

import com.jing.admin.core.workflow.sdk.dingtalk.DingTalkSDK;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK管理器
 * 负责注册和获取各种SDK客户端实例
 */
@Component
public class SdkManager implements InitializingBean {
    
    private static final Map<String, ISdkClient> sdkClients = new HashMap<>();
    
    @Autowired
    private List<ISdkClient> sdkClientList;

    /**
     * 注册SDK客户端
     * 
     * @param systemIdentifier 系统标识
     * @param sdkClient SDK客户端实例
     */
    public static void registerSdkClient(String systemIdentifier, ISdkClient sdkClient) {
        sdkClients.put(systemIdentifier, sdkClient);
    }
    
    /**
     * 获取SDK客户端
     *
     * @param systemIdentifier 系统标识
     * @return SDK客户端实例
     */
    public static ISdkClient getSdkClient(String systemIdentifier) {
        return sdkClients.get(systemIdentifier);
    }

    /**
     * 检查是否支持指定的系统
     *
     * @param systemIdentifier 系统标识
     * @return 是否支持
     */
    public static boolean supports(String systemIdentifier) {
        return sdkClients.containsKey(systemIdentifier);
    }
    
    /**
     * 在Spring完成依赖注入后自动初始化SDK客户端
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 清空静态map，防止重复注册
        sdkClients.clear();
        
        // 将Spring管理的SDK客户端注册到静态map中
        if (sdkClientList != null) {
            for (ISdkClient sdkClient : sdkClientList) {
                registerSdkClient(sdkClient.getSystemIdentifier(), sdkClient);
            }
        }
    }
}