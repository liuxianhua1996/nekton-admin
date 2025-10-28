package com.jing.admin.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.WorkflowMapper;
import com.jing.admin.model.domain.Workflow;
import org.mapstruct.ap.internal.model.assignment.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 工作流数据访问层实现类
 *
 * @author lxh
 * @date 2025/9/19
 */
@Repository
public class WorkflowRepository extends ServiceImpl<WorkflowMapper, Workflow> {
    private final WorkflowMapper workflowMapper;

    public WorkflowRepository(WorkflowMapper workflowMapper) {
        this.workflowMapper = workflowMapper;
    }


    public int updateWorkflow(Workflow workflow) {
        LambdaUpdateWrapper<Workflow> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Workflow::getId, UUID.fromString(workflow.getId()))
                .eq(Workflow::getVersion, workflow.getVersion())
                .set(Workflow::getJsonData, workflow.getJsonData())
                .set(Workflow::getVersion, workflow.getVersion() + 1)
                .set(Workflow::getUpdateTime, workflow.getUpdateTime())
                .set(Workflow::getUpdateUserId, workflow.getUpdateUserId());
        return this.workflowMapper.update(updateWrapper);
    }

    public Workflow getById(String id) {
        QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return this.getOne(queryWrapper);
    }
}