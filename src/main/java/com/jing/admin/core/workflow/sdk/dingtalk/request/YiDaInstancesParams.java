package com.jing.admin.core.workflow.sdk.dingtalk.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/11/19
 **/
@Data
@Builder
public class YiDaInstancesParams {
    private String appType;
    private String systemToken;
    private String userId;
    private String language;
    private String formUuid;
    private String formDataJson;
}
