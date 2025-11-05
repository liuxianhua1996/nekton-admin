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
    
    /**
     * 检查用户是否有指定租户的访问权限
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 如果用户有权限访问该租户，返回true，否则返回false
     */
    boolean checkUserTenantAccess(@Param("userId") String userId, @Param("tenantId") String tenantId);

}