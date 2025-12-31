package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Data
@TableName("tb_tenants")
public class Tenant{
    @TableId
    private String id;
    private String tenantCode;
    private String tenantName;
    private String description;
    /**
     * -- ACTIVE, INACTIVE, SUSPENDED
     */
    private String status;
    private long createTime;
    private long updateTime;
    private String createUserId;
    private String updateUserId;
}
