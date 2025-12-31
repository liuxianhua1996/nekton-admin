package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.WorkflowNodeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作流节点执行日志Mapper接口
 */
@Mapper
public interface WorkflowNodeLogMapper extends BaseMapper<WorkflowNodeLog> {
    // 继承BaseMapper，提供基本的CRUD操作
}