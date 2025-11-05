package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Data
@TableName("tb_tenants")
public class Tenant  extends Base{
    private String tenantCode;
    private String tenantName;
    private String description;
    /**
     * -- ACTIVE, INACTIVE, SUSPENDED
     */
    private String status;
}
