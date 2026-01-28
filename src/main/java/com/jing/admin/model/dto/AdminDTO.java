package com.jing.admin.model.dto;

import lombok.Data;

@Data
public class AdminDTO {
    private String id;
    private String userId;
    private String userName;
    private String adminType;
    private long createTime;
    private long updateTime;
    private String createUserId;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
}
