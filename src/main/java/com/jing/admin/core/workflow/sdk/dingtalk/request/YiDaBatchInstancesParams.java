package com.jing.admin.core.workflow.sdk.dingtalk.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author lxh
 * @date 2025/11/19
 **/
@Data
@Builder
public class YiDaBatchInstancesParams {
    private String appType;
    private String systemToken;
    private String userId;
    private String formUuid;
    private List<String> formDataJsonList;
    private boolean keepRunningAfterException = false;
    private boolean asynchronousExecution = false;
    private boolean noExecuteExpression = false;
}
