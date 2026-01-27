package com.jing.admin.model.api;

import lombok.Data;

@Data
public class UserQueryRequest {
    private Long current = 1L;
    private Long size = 10L;
    private String username;
    private String email;
    private Integer enabled;
}
