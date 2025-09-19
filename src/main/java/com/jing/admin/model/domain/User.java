package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jing.admin.core.constant.Role;
import lombok.Data;

import java.util.Collection;

@Data
@TableName("tb_users")
public class User extends Base  {
    private String username;

    private String password;

    private String email;
    private int enabled = 1;

    // 不映射到数据库字段，用于存储用户角色
    @TableField(exist = false)
    private Collection<Role> roles;
}