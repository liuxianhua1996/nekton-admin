package com.jing.admin.core.workflow.sdk.dingtalk;

import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.workflow.sdk.ISdkClient;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.core.workflow.sdk.dingtalk.api.DingTalkApi;
import com.jing.admin.core.workflow.sdk.dingtalk.api.DingTalkResult;
import com.jing.admin.core.workflow.sdk.dingtalk.api.DingTalkYiDaApi;
import com.jing.admin.core.workflow.sdk.dingtalk.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * DingTalkSDK -
 *
 * @author zhicheng
 * @version 1.0
 * @see
 * @since 2025/12/30
 */
@Component
public class DingTalkSDK implements ISdkClient {
    private Map<String, Object> config;
    private Map<String, DingTalkApi> dingTalkApiMap;
    @Autowired
    DingTalkYiDaApi dingTalkYiDaApi;
    
    // 策略映射表
    private final Map<String, BiFunction<DingTalkApi, Map<String, Object>, Object>> methodStrategyMap;
    
    public DingTalkSDK() {
        this.methodStrategyMap = new HashMap<>();
        this.methodStrategyMap.put("yida_insert", this::handleYiDaInsert);
        this.methodStrategyMap.put("yida_batch_save", this::handleYiDaBatchSave);
        this.methodStrategyMap.put("yida_insert_or_update", this::handleYiDaInsertOrUpdate);
        this.methodStrategyMap.put("yida_get_form_data", this::handleYiDaGetFormData);
        this.methodStrategyMap.put("yida_get_instance_info", this::handleYiDaGetInstanceInfo);
    }

    public DingTalkApi getDingTalkApi(GlobalParams globalParams){
        String json = globalParams.getParamValue();
        JSONObject jsonObject = JSONObject.parseObject(json);
        String apiKey = jsonObject.getString("apiKey");
        String apiSecret = jsonObject.getString("apiSecret");
        if(dingTalkApiMap == null) {
            dingTalkApiMap = new HashMap<>();
        }
        if(dingTalkApiMap.containsKey(apiKey)){
            return dingTalkApiMap.get(apiKey);
        }
        DingTalkApi dingTalkApi = new DingTalkApi(apiKey,apiSecret);
        dingTalkApiMap.put(apiKey, dingTalkApi);
        return dingTalkApi;
    }
    
    @Override
    public Object execute(String method, Map<String, Object> params, GlobalParams globalParams) {
        // 根据不同的方法执行不同的钉钉API调用
        DingTalkApi dingTalkApi = this.getDingTalkApi(globalParams);
        
        BiFunction<DingTalkApi, Map<String, Object>, Object> strategy = methodStrategyMap.get(method);
        if (strategy != null) {
            return strategy.apply(dingTalkApi, params);
        }
        
        return buildErrorResult("不支持的方法: " + method);
    }
    
    private Object handleYiDaInsert(DingTalkApi dingTalkApi, Map<String, Object> params) {
        try {
            YiDaInstancesParams yiDaInstancesParams = JSONObject.parseObject(
                JSONObject.toJSONString(params), YiDaInstancesParams.class);
            return dingTalkYiDaApi.instances(dingTalkApi, yiDaInstancesParams);
        } catch (Exception e) {
            return buildErrorResult("处理宜搭插入时发生错误: " + e.getMessage());
        }
    }
    
    private Object handleYiDaBatchSave(DingTalkApi dingTalkApi, Map<String, Object> params) {
        try {
            YiDaBatchInstancesParams yiDaBatchInstancesParams = JSONObject.parseObject(
                JSONObject.toJSONString(params), YiDaBatchInstancesParams.class);
            return dingTalkYiDaApi.batchInstances(dingTalkApi, yiDaBatchInstancesParams);
        } catch (Exception e) {
            return buildErrorResult("处理宜搭批量保存时发生错误: " + e.getMessage());
        }
    }
    
    private Object handleYiDaInsertOrUpdate(DingTalkApi dingTalkApi, Map<String, Object> params) {
        try {
            YiDaInstancesOrUpParams yiDaInstancesOrUpParams = JSONObject.parseObject(
                JSONObject.toJSONString(params), YiDaInstancesOrUpParams.class);
            return dingTalkYiDaApi.insertOrUpdate(dingTalkApi, yiDaInstancesOrUpParams);
        } catch (Exception e) {
            return buildErrorResult("处理宜搭插入或更新时发生错误: " + e.getMessage());
        }
    }
    
    private Object handleYiDaGetFormData(DingTalkApi dingTalkApi, Map<String, Object> params) {
        try {
            YiDaGetInstancesByIdParams yiDaGetInstancesByIdParams = JSONObject.parseObject(
                JSONObject.toJSONString(params), YiDaGetInstancesByIdParams.class);
            return dingTalkYiDaApi.getFormDataById(dingTalkApi, yiDaGetInstancesByIdParams);
        } catch (Exception e) {
            return buildErrorResult("获取宜搭表单数据时发生错误: " + e.getMessage());
        }
    }
    
    private Object handleYiDaGetInstanceInfo(DingTalkApi dingTalkApi, Map<String, Object> params) {
        try {
            YiDaGetInstancesByIdParams yiDaGetInstancesByIdParams = JSONObject.parseObject(
                JSONObject.toJSONString(params), YiDaGetInstancesByIdParams.class);
            return dingTalkYiDaApi.getInstancesInfoById(dingTalkApi, yiDaGetInstancesByIdParams);
        } catch (Exception e) {
            return buildErrorResult("获取宜搭实例信息时发生错误: " + e.getMessage());
        }
    }
    @Override
    public String getSystemIdentifier() {
        return "dingtalk_app";
    }

    public void initialize(Map<String, Object> config) {
        this.config = config;
    }

    private Object buildErrorResult(String errorMessage) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", errorMessage);
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }
}
