package com.jing.admin.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.TenantMapper;
import com.jing.admin.mapper.TenantUserMapper;
import com.jing.admin.model.domain.Tenant;
import com.jing.admin.model.domain.TenantUser;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.TenantUseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 用户Repository类
 * 使用MyBatis-Plus的UserMapper进行数据访问
 */
@Repository
@Slf4j
public class TenantUserRepository extends ServiceImpl<TenantUserMapper, TenantUser> {

    private final TenantUserMapper tenantUserMapper;

    public TenantUserRepository(TenantUserMapper tenantUserMapper) {
        this.tenantUserMapper = tenantUserMapper;
    }

    public List<TenantUseDTO> queryUserTenants(String userId) {
        return tenantUserMapper.selectTenantUser(userId);
    }
}