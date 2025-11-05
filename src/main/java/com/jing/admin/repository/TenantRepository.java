package com.jing.admin.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.TenantMapper;
import com.jing.admin.model.domain.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用户Repository类
 * 使用MyBatis-Plus的UserMapper进行数据访问
 */
@Repository
@Slf4j
public class TenantRepository extends ServiceImpl<TenantMapper, Tenant> {

    private final TenantMapper tenantMapper;

    public TenantRepository(TenantMapper tenantMapper) {
        this.tenantMapper = tenantMapper;
    }
}