package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关联实体类
 * @author lxh
 * @date 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_role_menu")
public class RoleMenu extends Base {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;
}