package com.jing.admin.model.mapping;

import com.jing.admin.model.api.RoleRequest;
import com.jing.admin.model.domain.Role;
import com.jing.admin.model.dto.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapping {
    RoleMapping INSTANCE = Mappers.getMapper(RoleMapping.class);

    Role toEntity(RoleRequest request);

    RoleDTO toDTO(Role entity);
}
