package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Collection;

@Data
@TableName("tb_users")
public class User {
    @TableId
    private String id;

    private String username;

    private String password;

    private String email;
    private int enabled = 1;
    private long createTime;
    private long updateTime;
    private String createUserId;
    private String updateUserId;
}