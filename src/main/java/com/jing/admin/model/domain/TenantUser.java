package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Data
@TableName("tb_user_tenant")
public class TenantUser extends Base {
    private String userId;
    private String tenantId;
}
