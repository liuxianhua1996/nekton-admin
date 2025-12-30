package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.model.dto.ScheduleJobLogDTO;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 调度任务执行记录Mapper接口
 */
@Mapper
public interface ScheduleJobLogMapper extends BaseMapper<ScheduleJobLog> {
    
    /**
     * 分页查询调度任务执行记录（关联用户表）
     *
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    IPage<ScheduleJobLogDTO> selectScheduleJobLogPageWithUser(Page<ScheduleJobLogDTO> page, @Param("query") ScheduleJobQueryRequest queryRequest);
}