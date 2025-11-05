package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.Tenant;
import com.jing.admin.model.domain.TenantUser;
import com.jing.admin.model.dto.TenantUseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface TenantUserMapper extends BaseMapper<TenantUser> {

    List<TenantUseDTO> selectTenantUser(@Param("userId") String userId);

}