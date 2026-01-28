package com.jing.admin.model.mapping;

import com.jing.admin.model.api.AdminRequest;
import com.jing.admin.model.domain.Admin;
import com.jing.admin.model.dto.AdminDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapping {
    AdminMapping INSTANCE = Mappers.getMapper(AdminMapping.class);

    Admin toEntity(AdminRequest request);

    AdminDTO toDTO(Admin entity);
}
