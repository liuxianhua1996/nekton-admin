package com.jing.admin.core.tenant;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Component
@Primary
public class TenantDynamicDataSource extends AbstractRoutingDataSource {
    @Autowired
    private DynamicDataSourceManager dataSourceManager;

    @PostConstruct
    public void init() {
        // 设置默认数据源（公共库）
        setDefaultTargetDataSource(dataSourceManager.getDefaultDataSource());

        // 初始目标数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("common_db", dataSourceManager.getDefaultDataSource());
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        // 返回当前租户ID作为数据源查找键
        return TenantContextHolder.getTenantId();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || "common_db".equals(tenantId)) {
            // 没有租户信息或使用公共库，返回默认数据源
            return dataSourceManager.getDefaultDataSource();
        }
        // 从动态管理器获取租户特定的数据源
        DataSource dataSource = dataSourceManager.getDataSource(tenantId);
        if (dataSource != null) {
            return dataSource;
        }
        throw new IllegalArgumentException("No data source found for tenant: " + tenantId);
    }
}
