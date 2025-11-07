package com.jing.admin.model.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/11/7
 **/
@Data
public class WorkflowTestRequest {
    private String id;

    private JSONObject params;
}
