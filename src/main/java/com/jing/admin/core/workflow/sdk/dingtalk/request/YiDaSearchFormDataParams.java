package com.jing.admin.core.workflow.sdk.dingtalk.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lxh
 * @date 2025/11/19
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YiDaSearchFormDataParams {
    private String appType;
    private String systemToken;
    private String userId;
    private String id;
    private String formUuid;
}
