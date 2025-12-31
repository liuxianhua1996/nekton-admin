package com.jing.admin.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.mapper.WorkflowNodeLogMapper;
import org.springframework.stereotype.Repository;

/**
 * 工作流节点执行日志数据访问层实现类
 */
@Repository
public class WorkflowNodeLogRepository extends ServiceImpl<WorkflowNodeLogMapper, WorkflowNodeLog> {
    // 继承ServiceImpl，提供基本的CRUD操作
}