package com.jing.admin.core.sdk.dingtalk.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FromDataInfoDTO - 表单实例
 *
 * @author zhicheng
 * @version 1.0
 * @see
 * @since 2025/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDataInfoDTO {
    private String modifiedTimeGMT;
    private JSONObject formData;
    private String formInstId;
}
