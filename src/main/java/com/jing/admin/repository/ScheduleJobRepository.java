package com.jing.admin.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 调度任务Repository类
 */
@Repository
public class ScheduleJobRepository extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {
    
    /**
     * 分页查询调度任务（关联用户表）
     *
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public IPage<ScheduleJobDTO> selectScheduleJobPageWithUser(Page<ScheduleJobDTO> page, ScheduleJobQueryRequest queryRequest) {
        return this.baseMapper.selectScheduleJobPageWithUser(page, queryRequest);
    }

    public ScheduleJob getById(String id){
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return this.getOne(queryWrapper);
    }
}