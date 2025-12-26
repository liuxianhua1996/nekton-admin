package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.model.api.WorkflowQueryRequest;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.WorkflowDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工作流Mapper接口
 * @author lxh
 * @date 2025/9/19
 */
@Mapper
public interface WorkflowMapper extends BaseMapper<Workflow> {

    /**
     * 分页查询工作流（关联用户表）
     * @param page 分页对象
     * @return 分页结果
     */
    IPage<WorkflowDTO> selectWorkflowPageWithUser(
            Page<WorkflowDTO> page,
            @Param("query") WorkflowQueryRequest workflowQueryRequest
    );
}