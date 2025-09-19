package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * User实体到UserDTO的映射接口
 * 使用MapStruct自动生成映射实现
 * 
 * @author lxh
 * @date 2025/9/18
 **/
@Mapper(imports = {UUID.class, System.class})
public interface UserMapping {
    UserMapping INSTANCE = Mappers.getMapper(UserMapping.class);

    /**
     * 将User实体映射为UserDTO
     * 
     * @param user 用户实体
     * @return 用户DTO
     */
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", source = "enabled")
    UserDTO toDTO(User user);
}
