package com.jing.admin.core.workflow.sdk;

import com.jing.admin.core.workflow.model.GlobalParams;

import java.util.Map;

/**
 * SDK客户端通用接口
 * 定义所有第三方SDK需要实现的通用方法
 */
public interface ISdkClient {
    
    /**
     * 执行SDK方法
     * 
     * @param method 方法名
     * @param params 参数
     * @return 执行结果
     */
    Object execute(String method, Map<String, Object> params, GlobalParams globalParams);
    
    /**
     * 获取SDK系统标识
     * 
     * @return 系统标识
     */
    String getSystemIdentifier();
    
    /**
     * 初始化SDK客户端
     * 
     * @param config 配置参数
     */
    void initialize(Map<String, Object> config);
}