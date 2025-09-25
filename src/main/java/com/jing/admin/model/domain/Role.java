package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * @author lxh
 * @date 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_roles")
public class Role extends Base {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;
}