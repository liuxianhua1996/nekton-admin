package com.jing.admin.model.api;

import lombok.Data;

/**
 * 工作流查询请求类
 * @author lxh
 * @date 2025/9/19
 */
@Data
public class WorkflowQueryRequest {
    /**
     * 当前页码，从1开始
     */
    private Integer current = 1;
    
    /**
     * 每页显示条数
     */
    private Integer size = 10;
    
    /**
     * 工作流名称（模糊查询）
     */
    private String name;
    
    /**
     * 工作流状态（0-草稿，1-发布）
     */
    private Integer status;
    
    /**
     * 创建人ID
     */
    private String createUserId;
}