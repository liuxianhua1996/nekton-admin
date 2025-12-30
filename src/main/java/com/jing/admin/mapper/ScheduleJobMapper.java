package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 调度任务Mapper接口
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {
    
    /**
     * 分页查询调度任务（关联用户表）
     *
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    IPage<ScheduleJobDTO> selectScheduleJobPageWithUser(Page<ScheduleJobDTO> page, @Param("query") ScheduleJobQueryRequest queryRequest);
}