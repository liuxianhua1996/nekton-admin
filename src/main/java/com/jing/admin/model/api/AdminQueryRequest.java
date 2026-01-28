package com.jing.admin.model.api;

import lombok.Data;

@Data
public class AdminQueryRequest {
    private Long current = 1L;
    private Long size = 10L;
    private String userId;
    private String username;
    private String adminType;
}
