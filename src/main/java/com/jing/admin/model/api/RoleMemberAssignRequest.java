package com.jing.admin.model.api;

import lombok.Data;

@Data
public class RoleMemberAssignRequest {
    private String userId;
    private String roleId;
}
