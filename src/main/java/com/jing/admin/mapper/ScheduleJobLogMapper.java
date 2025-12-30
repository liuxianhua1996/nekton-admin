package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.ScheduleJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调度任务执行记录Mapper接口
 */
@Mapper
public interface ScheduleJobLogMapper extends BaseMapper<ScheduleJobLog> {
}