package com.jing.admin.core.tenant;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicDataSourceManager {

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    private DataSource defaultDataSource;
    private DataSource dataSource;
    // 使用 setter 注入而不是字段注入
    @Autowired
    public void setDataSource(@Qualifier("commonDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
        this.defaultDataSource = dataSource;
    }


    @PostConstruct
    public void init() {
        // 创建默认数据源（公共库）
        this.defaultDataSource = dataSource;
        dataSourceMap.put("common_db", dataSource);
        // 加载所有活跃租户的数据源
        loadAllActiveTenantDataSources();
    }

    /**
     * 获取数据源
     */
    public DataSource getDataSource(String tenantId) {
        if (tenantId == null) {
            return defaultDataSource;
        }

        DataSource dataSource = dataSourceMap.get(tenantId);
        if (dataSource == null) {
            // 懒加载：如果数据源不存在，从数据库加载
            dataSource = loadTenantDataSource(tenantId);
        }
        return dataSource;
    }

    /**
     * 动态添加数据源
     */
    public synchronized void addDataSource(String tenantId, DataSource dataSource) {
        dataSourceMap.put(tenantId, dataSource);
    }

    /**
     * 移除数据源
     */
    public synchronized void removeDataSource(String tenantId) {
        DataSource dataSource = dataSourceMap.remove(tenantId);
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    /**
     * 获取默认数据源
     */
    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    /**
     * 加载所有活跃租户数据源
     */
    private void loadAllActiveTenantDataSources() {
        // 使用默认数据源查询租户信息
        JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDataSource);

        String sql = "SELECT tenant_id, db_name, jdbc_url, username, password " +
                "FROM tb_tenant_metadata WHERE status = 'ACTIVE'";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String tenantId = rs.getString("tenant_id");
            try {
                DataSource dataSource = createDataSourceFromMetadata(rs);
                dataSourceMap.put(tenantId, dataSource);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create data source for tenant: " + tenantId, e);
            }
            return null;
        });
    }

    /**
     * 加载单个租户数据源
     */
    private DataSource loadTenantDataSource(String tenantId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDataSource);

        String sql = "SELECT tenant_id, db_name, jdbc_url, username, password " +
                "FROM tenant_metadata WHERE tenant_id = ? AND status = 'ACTIVE'";

        return jdbcTemplate.query(sql, new Object[]{tenantId}, rs -> {
            if (rs.next()) {
                DataSource dataSource = createDataSourceFromMetadata(rs);
                dataSourceMap.put(tenantId, dataSource);
                return dataSource;
            }
            throw new IllegalArgumentException("Tenant not found or inactive: " + tenantId);
        });
    }

    /**
     * 从元数据创建数据源
     */
    private DataSource createDataSourceFromMetadata(ResultSet rs) throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(rs.getString("jdbc_url"));
        dataSource.setUsername(rs.getString("username"));
        dataSource.setPassword(rs.getString("password")); // 实际项目中需要解密
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(15);
        dataSource.setMinimumIdle(3);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setConnectionTestQuery("SELECT 1");

        return dataSource;
    }
}