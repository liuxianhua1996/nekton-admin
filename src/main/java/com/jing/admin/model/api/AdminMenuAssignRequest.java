package com.jing.admin.model.api;

import lombok.Data;

import java.util.List;

@Data
public class AdminMenuAssignRequest {
    private List<String> menuIds;
}
