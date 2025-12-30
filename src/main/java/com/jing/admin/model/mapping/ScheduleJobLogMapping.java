package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.core.dto.ScheduleJobLogRequest;
import com.jing.admin.core.dto.ScheduleJobLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * 调度任务执行记录实体与DTO映射
 */
@Mapper(imports = {UUID.class, System.class})
public interface ScheduleJobLogMapping {
    
    ScheduleJobLogMapping INSTANCE = Mappers.getMapper(ScheduleJobLogMapping.class);

    /**
     * 将请求DTO转换为实体
     */
    ScheduleJobLog toEntity(ScheduleJobLogRequest request);

    /**
     * 将实体转换为响应DTO
     */
    ScheduleJobLogResponse toResponse(ScheduleJobLog entity);

    /**
     * 将请求DTO转换为实体（用于更新）
     */
    ScheduleJobLog updateEntityFromRequest(ScheduleJobLogRequest request);
}