package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.api.ScheduleJobRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * 调度任务实体与DTO映射
 */
@Mapper(imports = {UUID.class, System.class})
public interface ScheduleJobMapping {
    
    ScheduleJobMapping INSTANCE = Mappers.getMapper(ScheduleJobMapping.class);

    /**
     * 将请求DTO转换为实体
     */
    ScheduleJob toEntity(ScheduleJobRequest request);

    /**
     * 将实体转换为响应DTO
     */
    ScheduleJobDTO toDTO(ScheduleJob entity);

    /**
     * 将请求DTO转换为实体（用于更新）
     */
    ScheduleJob updateEntityFromRequest(ScheduleJobRequest request);

}