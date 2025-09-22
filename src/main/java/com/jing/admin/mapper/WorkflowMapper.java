package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.Workflow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作流Mapper接口
 * @author lxh
 * @date 2025/9/19
 */
@Mapper
public interface WorkflowMapper extends BaseMapper<Workflow> {
}