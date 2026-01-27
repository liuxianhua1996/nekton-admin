package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体类
 * @author lxh
 * @date 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user_roles")
public class UserRole extends Base {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 租户ID
     */
    private String tenantId;
}