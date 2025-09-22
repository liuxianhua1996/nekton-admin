package com.jing.admin.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.WorkflowMapper;
import com.jing.admin.model.domain.Workflow;
import org.springframework.stereotype.Repository;

/**
 * 工作流数据访问层实现类
 * @author lxh
 * @date 2025/9/19
 */
@Repository
public class WorkflowRepository extends ServiceImpl<WorkflowMapper, Workflow> {
}