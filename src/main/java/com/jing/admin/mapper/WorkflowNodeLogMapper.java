package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.model.api.WorkflowNodeLogQueryRequest;
import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.model.dto.WorkflowNodeLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工作流节点执行日志Mapper接口
 */
@Mapper
public interface WorkflowNodeLogMapper extends BaseMapper<WorkflowNodeLog> {
    // 继承BaseMapper，提供基本的CRUD操作
    
    /**
     * 分页查询节点执行日志（关联用户表）
     */
    IPage<WorkflowNodeLogDTO> selectNodeLogPageWithUser(@Param("page") Page<WorkflowNodeLogDTO> page, 
                                                         @Param("query") WorkflowNodeLogQueryRequest queryRequest);
}