package com.jing.admin.core.workflow.sdk;


import com.jing.admin.core.workflow.sdk.dingtalk.DingTalkSDK;

import java.util.HashMap;
import java.util.Map;

/**
 * SDK管理器
 * 负责注册和获取各种SDK客户端实例
 */
public class SdkManager {
    
    private static final Map<String, ISdkClient> sdkClients = new HashMap<>();
    
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
     * 初始化默认SDK客户端
     */
    public static void initializeDefaultClients() {
        // 注册默认的SDK客户端
        registerSdkClient("dingtalk_app",new DingTalkSDK());
//      registerSdkClient("kingdee_sky", new KingdeeSkySDK());
    }

}