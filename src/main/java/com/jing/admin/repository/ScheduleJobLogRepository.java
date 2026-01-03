package com.jing.admin.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.ScheduleJobLogMapper;
import com.jing.admin.model.api.ScheduleJobLogQueryRequest;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.model.dto.ScheduleJobLogDTO;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import org.springframework.stereotype.Repository;

/**
 * 调度任务执行记录Repository类
 */
@Repository
public class ScheduleJobLogRepository extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLog> {
    
    /**
     * 分页查询调度任务执行记录（关联用户表）
     *
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public IPage<ScheduleJobLogDTO> selectScheduleJobLogPageWithUser(Page<ScheduleJobLogDTO> page, ScheduleJobLogQueryRequest queryRequest) {
        return this.baseMapper.selectScheduleJobLogPageWithUser(page, queryRequest);
    }
}