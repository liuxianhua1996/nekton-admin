package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.WorkflowDTO;
import org.springframework.beans.BeanUtils;

/**
 * 工作流映射转换类
 * @author lxh
 * @date 2025/9/19
 */
public class WorkflowMapping {
    
    /**
     * 将DTO转换为实体类
     * @param dto 工作流DTO
     * @return 工作流实体类
     */
    public static Workflow toEntity(WorkflowDTO dto) {
        if (dto == null) {
            return null;
        }
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(dto, workflow);
        return workflow;
    }
    
    /**
     * 将实体类转换为DTO
     * @param entity 工作流实体类
     * @return 工作流DTO
     */
    public static WorkflowDTO toDTO(Workflow entity) {
        if (entity == null) {
            return null;
        }
        WorkflowDTO dto = new WorkflowDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}