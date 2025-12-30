package com.jing.admin.core.workflow.model;

import lombok.Builder;
import lombok.Data;

/**
 * GlobalParams -
 *
 * @author zhicheng
 * @version 1.0
 * @see
 * @since 2025/12/30
 */
@Data
@Builder
public class GlobalParams {
    private String apiKeyId;
    /**
     * 参数键
     */
    private String paramKey;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 参数用途类型 (global_variable, db_config, api_config等)
     */
    private String paramType;

    /**
     * 值的数据类型 (string, number, boolean, json等)
     */
    private String valueType;
}
