package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Mapper(imports = {UUID.class, System.class})
public interface UserMapping {
    UserMapping INSTANCE = Mappers.getMapper(UserMapping.class);

//    @Mapping(target = "createTime", expression = "java(System.currentTimeMillis())")
//    @Mapping(target = "updateTime", expression = "java(System.currentTimeMillis())")
    UserDTO toDTO(User user);
}
