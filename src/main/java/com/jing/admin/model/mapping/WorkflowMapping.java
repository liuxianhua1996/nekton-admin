package com.jing.admin.model.mapping;

import com.jing.admin.model.api.WorkflowRequest;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.model.dto.WorkflowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

/**
 * 工作流映射转换类
 * @author lxh
 * @date 2025/9/19
 */
@Mapper(imports = {UUID.class, System.class})
public interface WorkflowMapping {
    WorkflowMapping INSTANCE = Mappers.getMapper(WorkflowMapping.class);
    /**
     *
     * @param workflow
     * @return
     */
    WorkflowDTO toDTO(Workflow workflow);
}