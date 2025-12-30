package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.core.entity.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调度任务Mapper接口
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {
}