package com.jing.admin.core.sdk.dingtalk.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author lxh
 * @date 2025/11/19
 **/
@Slf4j
@Data
public class DingTalkApi {

    private String accessToken = "";
    private long expireIn = 0L;
    private String appKey = "";
    private String appSecret = "";
    private final String headTokenName = "x-acs-dingtalk-access-token";

    public void DingTalkApi(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.oauth();
    }

    public String getAccessToken() {
        this.checkToken();
        return this.accessToken;
    }

    public void oauth() {
        String url = DingTalkConfig.getBaseUrl() + DingTalkConfig.getToken;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        JSONObject data = new JSONObject();
        data.put("appKey", appKey);
        data.put("appSecret", appSecret);
        HttpEntity requestEntity = new HttpEntity<>(data, requestHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JSONObject.class);
        JSONObject result = responseEntity.getBody();
        this.accessToken = result.getString("accessToken");
        this.expireIn = System.currentTimeMillis() + result.getLongValue("expireIn") * 1000;
        log.info("初始化 DingTalk API 成功 {}", this.accessToken);
    }

    public HttpEntity getHttpEntity(Object data) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(this.headTokenName, this.accessToken);
        HttpEntity requestEntity = new HttpEntity<>(data, requestHeaders);
        return requestEntity;
    }

    public HttpEntity getHttpEntity() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(this.headTokenName, this.accessToken);
        HttpEntity requestEntity = new HttpEntity<>(null, requestHeaders);
        return requestEntity;
    }

    /**
     * 检查token失效
     */
    public void checkToken() {
        if (this.expireIn - System.currentTimeMillis() < (30 * 60 * 1000)) {
            this.oauth();
        }
    }
}
