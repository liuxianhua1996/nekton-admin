package com.jing.admin.model.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private String id;
    private String name;
    private String description;
    private long createTime;
    private long updateTime;
    private String createUserId;
    private String updateUserId;
}
