package com.jing.admin.model.dto;

import lombok.Data;

/**
 * 工作流数据传输对象
 * @author lxh
 * @date 2025/9/19
 */
@Data
public class WorkflowDTO {
    /**
     * 工作流ID
     */
    private String id;
    
    /**
     * 工作流名称
     */
    private String name;
    
    /**
     * 工作流描述
     */
    private String description;
    
    /**
     * 工作流JSON数据
     * (在PostgreSQL中映射为TEXT类型)
     * 仅用于持久化存储，不参与查询操作
     */
    private String jsonData;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 状态：0-草稿，1-发布
     */
    private Integer status;
    
    /**
     * 创建人ID
     */
    private String createUserId;
}