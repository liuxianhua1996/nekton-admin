package com.jing.admin.core.workflow.sdk.dingtalk.api;

import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.workflow.sdk.dingtalk.request.YiDaBatchInstancesParams;
import com.jing.admin.core.workflow.sdk.dingtalk.request.YiDaGetInstancesByIdParams;
import com.jing.admin.core.workflow.sdk.dingtalk.request.YiDaInstancesOrUpParams;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.jing.admin.core.workflow.sdk.dingtalk.dto.*;

/**
 * @author lxh
 * @date 2025/11/19
 **/
@Component
public class DingTalkYiDaApi {
    DingTalkConfig dingTalkConfig;

    /**
     * 创建表单实例
     * https://open.dingtalk.com/document/orgapp/save-form-data
     *
     * @param yiDaInstancesParams
     * @return
     */
    public DingTalkResult instances(DingTalkApi dingTalkApi, YiDaBatchInstancesParams yiDaInstancesParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dingTalkConfig.getBaseUrl() + "/v2.0/yida/forms/instances";
        HttpEntity requestEntity = dingTalkApi.getHttpEntity(yiDaInstancesParams);
        ResponseEntity<DingTalkResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DingTalkResult.class);
        DingTalkResult dingTalkResult = responseEntity.getBody();
        dingTalkResult.toJavaObject(String.class);
        return dingTalkResult;
    }
    public DingTalkResult batchInstances(DingTalkApi dingTalkApi,YiDaBatchInstancesParams yiDaBatchInstancesParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dingTalkConfig.getBaseUrl() + "/v1.0/yida/forms/instances/batchSave";
        HttpEntity requestEntity = dingTalkApi.getHttpEntity(yiDaBatchInstancesParams);
        ResponseEntity<DingTalkResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DingTalkResult.class);
        DingTalkResult dingTalkResult = responseEntity.getBody();
        dingTalkResult.toJavaObject(String.class);
        return dingTalkResult;
    }

    /**
     * 新增or更新
     * @param yiDaInstancesParams
     * @return
     */
    public DingTalkResult insertOrUpdate(DingTalkApi dingTalkApi, YiDaInstancesOrUpParams yiDaInstancesParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dingTalkConfig.getBaseUrl() + "/v2.0/yida/forms/instances/insertOrUpdate";
        HttpEntity requestEntity = dingTalkApi.getHttpEntity(yiDaInstancesParams);
        ResponseEntity<DingTalkResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DingTalkResult.class);
        DingTalkResult dingTalkResult = responseEntity.getBody();
        dingTalkResult.toJavaObject(String.class);
        return dingTalkResult;
    }

    public DingTalkResult getFormDataById(DingTalkApi dingTalkApi, YiDaGetInstancesByIdParams yiDaGetInstancesByIdParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dingTalkConfig.getBaseUrl() + "/v2.0/yida/forms/instances/%s".formatted(yiDaGetInstancesByIdParams.getId());
        String finalUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("appType", yiDaGetInstancesByIdParams.getAppType())
                .queryParam("systemToken", yiDaGetInstancesByIdParams.getSystemToken())
                .queryParam("userId", yiDaGetInstancesByIdParams.getUserId())
                .encode()
                .toUriString();
        HttpEntity requestEntity = dingTalkApi.getHttpEntity();
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, requestEntity, JSONObject.class);
        JSONObject jsonObject = responseEntity.getBody();
        DingTalkResult dingTalkResult = new DingTalkResult();
        dingTalkResult.setErrcode(0);
        dingTalkResult.setRequest_id("");
        dingTalkResult.setResult(jsonObject.toJavaObject(FormDataInfoDTO.class));
        return dingTalkResult;
    }
    public DingTalkResult getInstancesInfoById(DingTalkApi dingTalkApi,YiDaGetInstancesByIdParams yiDaGetInstancesByIdParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dingTalkConfig.getBaseUrl() + "/v2.0/yida/processes/instancesInfos/%s".formatted(yiDaGetInstancesByIdParams.getId());
        String finalUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("appType", yiDaGetInstancesByIdParams.getAppType())
                .queryParam("systemToken", yiDaGetInstancesByIdParams.getSystemToken())
                .queryParam("userId", yiDaGetInstancesByIdParams.getUserId())
                .queryParam("formUuid",yiDaGetInstancesByIdParams.getFormUuid())
                .encode()
                .toUriString();
        HttpEntity requestEntity = dingTalkApi.getHttpEntity();
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, requestEntity, JSONObject.class);
        JSONObject jsonObject = responseEntity.getBody();
        DingTalkResult dingTalkResult = new DingTalkResult();
        dingTalkResult.setErrcode(0);
        dingTalkResult.setRequest_id("");
        dingTalkResult.setResult(jsonObject.toJavaObject(ProcessInstanceDTO.class));
        return dingTalkResult;
    }
}
