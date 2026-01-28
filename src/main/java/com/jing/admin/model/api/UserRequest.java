package com.jing.admin.model.api;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Integer enabled;
}
